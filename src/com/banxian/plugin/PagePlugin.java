package com.banxian.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.PropertyException;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMapping.Builder;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.log4j.Logger;

import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;
import com.banxian.exception.SystemException;
import com.banxian.util.Common;
import com.banxian.util.EhcacheUtils;


/**
 * Mybatis的分页查询插件，通过拦截StatementHandler的prepare方法来实现。
 * 只有在参数列表中包括Page类型的参数时才进行分页查询。 在多参数的情况下，只对第一个Page类型的参数生效。
 * 另外，在参数列表中，Page类型的参数无需用@Param来标注
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@SuppressWarnings("unchecked")
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PagePlugin implements Interceptor {
	private final static Logger logger = Logger.getLogger(PagePlugin.class);
	private static String dialect = null;// 数据库类型
	private static String pageSqlId = ""; // mybaits的数据库xml映射文件中需要拦截的ID(正则匹配)

	@SuppressWarnings("rawtypes")
	public Object intercept(Invocation ivk) throws Throwable {
		if (ivk.getTarget() instanceof RoutingStatementHandler) {
			RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");
			/**
			 * 方法1：通过ＩＤ来区分是否需要分页．.*query.* 方法2：传入的参数是否有page参数，如果有，则分页，
			 */
			// if (mappedStatement.getId().matches(pageSqlId)) { // 拦截需要分页的SQL
			BoundSql boundSql = delegate.getBoundSql();
			Object parameterObject = boundSql.getParameterObject();// 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
			if (parameterObject == null) {
				// throw new
				// NullPointerException("boundSql.getParameterObject() is null!");
				return ivk.proceed();
			} else {
				if (mappedStatement.getId().indexOf(".BaseMapper.") > -1) {
					Connection connection = (Connection) ivk.getArgs()[0];
					// parameterObject = toHashMap(model, pageView);
					// 公共方法被调用
					FormMap formMap = null;
					if (parameterObject instanceof FormMap) {
						formMap = toHashMap(parameterObject);
					} else if (parameterObject instanceof Map) {
						Map map = (Map) parameterObject;
						if (map.containsKey("list")) {
							List<Object> lists = (List<Object>) map.get("list");
							joinSql(connection, mappedStatement, boundSql, formMap, lists);
							return ivk.proceed();
						}
					} else {
						throw new SystemException("调用公共方法，传入参数有错误！具体请看参数说明！");
					}
					joinSql(connection, mappedStatement, boundSql, formMap, null);
					return ivk.proceed();
				}
				PageView pageView = null;
				if (parameterObject instanceof PageView) { // 参数就是Pages实体
					pageView = (PageView) parameterObject;
				} else if (parameterObject instanceof Map) {
					for (Entry entry : (Set<Entry>) ((Map) parameterObject).entrySet()) {
						if (entry.getValue() instanceof PageView) {
							pageView = (PageView) entry.getValue();
							break;
						}
					}
				} else { // 参数为某个实体，该实体拥有Pages属性
					pageView = ReflectHelper.getValueByFieldType(parameterObject, PageView.class);
					if (pageView == null) {
						return ivk.proceed();
					}
				}
				if (pageView == null) {
					return ivk.proceed();
				}
				String sql = boundSql.getSql();
				Connection connection = (Connection) ivk.getArgs()[0];
				setPageParameter(sql, connection, mappedStatement, boundSql, parameterObject, pageView);
				String pageSql = generatePagesSql(sql, pageView);
				ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); // 将分页sql语句反射回BoundSql.
			}
			// }
		}
		return ivk.proceed();
	}

	/**
	 * 从数据库里查询总的记录数并计算总页数，回写进分页参数<code>PageParameter</code>,这样调用者就可用通过 分页参数
	 * <code>PageParameter</code>获得相关信息。
	 * 
	 * @param sql
	 * @param connection
	 * @param mappedStatement
	 * @param boundSql
	 * @param page
	 * @throws SQLException
	 */
	private void setPageParameter(String sql, Connection connection, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject, PageView pageView) throws SQLException {
		// 记录总记录数
		PreparedStatement countStmt = null;
		ResultSet rs = null;
		String countSql ="";
		try {
			try {
			 countSql = "select count(1) from " + suffixStr(removeOrderBys(sql)); // 记录统计
			countStmt = connection.prepareStatement(countSql);
			ReflectHelper.setValueByFieldName(boundSql, "sql", countSql);
			DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
			parameterHandler.setParameters(countStmt);
			} catch (Exception e) {
				PagePlugin.logger.error(countSql+" 统计Sql出错,自动转换为普通统计Sql语句!");
				countSql = "select count(1) from (" + sql+ ") tmp_count"; 
				countStmt = connection.prepareStatement(countSql);
				ReflectHelper.setValueByFieldName(boundSql, "sql", countSql);
				DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
				parameterHandler.setParameters(countStmt);
			}
			rs = countStmt.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = ((Number) rs.getObject(1)).intValue();
			}
			logger.debug(".http-8080-2 ==> count(1) Total: "+count);
			pageView.setRowCount(count);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				countStmt.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * select id, articleNo, sum(ddd) ss, articleName, (SELECT loginName from
	 * oss_userinfo u where u.id=userId) loginName, (SELECT userName from
	 * oss_userinfo u where u.id=userId) userName, sum(ddd) ss from article
	 * 兼容以上子查询 //去除sql ..from 前面的字符。考虑 aafrom fromdd 等等情况
	 */
	public static String suffixStr(String toSql) {
		int sun = toSql.indexOf("from");
		String f1 = toSql.substring(sun - 1, sun);
		String f2 = toSql.substring(sun + 4, sun + 5);
		if (f1.trim().isEmpty() && f2.trim().isEmpty()) {// 判断第一个from的前后是否为空
			String s1 = toSql.substring(0, sun);
			int s0 = s1.indexOf("(");
			if (s0 > -1) {
				int se1 = s1.indexOf("select");
				if (s0 < se1) {
					if (se1 > -1) {
						String ss1 = s1.substring(se1 - 1, se1);
						String ss2 = s1.substring(se1 + 6, se1 + 7);
						if (ss1.trim().isEmpty() && ss2.trim().isEmpty()) {// 判断第一个from的前后是否为空
							return suffixStr(toSql.substring(sun + 5));
						}
					}
				}
				int se2 = s1.indexOf("(select");
				if (se2 > -1) {
					String ss2 = s1.substring(se2 + 7, se2 + 8);
					if (ss2.trim().isEmpty()) {// 判断第一个from的前后是否为空
						return suffixStr(toSql.substring(sun + 5));
					}
				}
				if (se1 == -1 && se2 == -1) {
					return toSql.substring(sun + 5);
				} else {
					toSql = toSql.substring(sun + 5);
				}
			} else {
				toSql = toSql.substring(sun + 5);
			}
		}
		return toSql;
	}

	public static void main(String[] args) throws Exception {  
		  
        String sql = "select orderdate, o.orderid, o.status, o.note,o.totalcost  from omorder o where 1 = 1  "  
        + "<#if orderdate_1 ??>     and o.orderdate >= '${orderdate_1}'  </#if>  "  
        + "<#if orderdate_2 ??>     and o.orderdate < '${orderdate_2}'  </#if>   <#if orderid ??>  "  
        + "   and o.orderid = '${orderid}'  </#if>  <#if note ??>     and o.note like '%${note}%' "  
        + " </#if>    <#if orderstatus ??>     and o.status = '${orderstatus}'  </#if>";  
        Map<String, Object> root = new HashMap<String, Object>();  
        root.put("orderdate_1", "2012-01-01");  
        root.put("msg", "您已经完成了第一个FreeMarker的示例");  
        System.out.println(sql);  
  
    }  
	/**
	 * 去除Sql的orderBy。
	 * 
	 * @param toSql
	 * @return String
	 * 
	 */
	private static String removeOrderBys(String toSql) {
		int sun = toSql.indexOf("order");
		if (sun > -1) {
			String f1 = toSql.substring(sun - 1, sun);
			String f2 = toSql.substring(sun + 5, sun + 5);
			if (f1.trim().isEmpty() && f2.trim().isEmpty()) {// 判断第一个from的前后是否为空
				String zb = toSql.substring(sun);
				int s0 = zb.indexOf(")");
				if (s0 > -1) {// from之前是否有括号
					String s1 = toSql.substring(0, sun);
					String s2 = zb.substring(s0);
					return removeOrderBys(s1 + s2);
				} else {
					toSql = toSql.substring(0, sun);
				}
			}
		}
		return toSql;
	}

	/**
	 * 根据数据库方言，生成特定的分页sql
	 * 
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePagesSql(String sql, PageView page) {
		if (page != null) {
			if ("mysql".equals(dialect)) {
				return buildPageSqlForMysql(sql, page).toString();
			} else if ("oracle".equals(dialect)) {
				return buildPageSqlForOracle(sql, page).toString();
			} else if ("SQLServer2008".equals(dialect)) {
				return buildPageSqlForSQLServer2008Dialect(sql, page).toString();
			}
		}
		return sql;
	}

	/**
	 * mysql的分页语句
	 * 
	 * @param sql
	 * @param page
	 * @return String
	 */
	public StringBuilder buildPageSqlForMysql(String sql, PageView page) {
		StringBuilder pageSql = new StringBuilder(100);
		String beginrow = String.valueOf((page.getPageNow() - 1) * page.getPageSize());
		pageSql.append(sql);
		pageSql.append(" limit " + beginrow + "," + page.getPageSize());
		return pageSql;
	}

	/**
	 * 参考hibernate的实现完成oracle的分页
	 * 
	 * @param sql
	 * @param page
	 * @return String
	 */
	public StringBuilder buildPageSqlForOracle(String sql, PageView page) {
		StringBuilder pageSql = new StringBuilder(100);
		String beginrow = String.valueOf((page.getPageNow()) * page.getPageSize());
		String endrow = String.valueOf(page.getPageNow() + 1 * page.getPageSize());

		pageSql.append("select * from ( select temp.*, rownum row_id from ( ");
		pageSql.append(sql);
		pageSql.append(" ) temp where rownum <= ").append(endrow);
		pageSql.append(") where row_id > ").append(beginrow);
		return pageSql;
	}

	/**
	 * 参考hibernate的实现完成SQLServer2008的分页
	 * 
	 * @param sql
	 * @param page
	 * @return String
	 */
	public String buildPageSqlForSQLServer2008Dialect(String sql, PageView page) {
		SQLServer2008Dialect dialect = new SQLServer2008Dialect();
		String sqlbuild = dialect.getLimitString(sql, (page.getPageNow() - 1) * page.getPageSize(), page.getPageSize());
		return sqlbuild;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties p) {
		dialect = p.getProperty("dialect");
		if (isEmpty(dialect)) {
			try {
				throw new PropertyException("dialectName or dialect property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
			}
		}
		pageSqlId = p.getProperty("pageSqlId");// 根据id来区分是否需要分页
		if (isEmpty(pageSqlId)) {
			try {
				throw new PropertyException("pageSqlId property is not found!");
			} catch (PropertyException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void joinSql(Connection connection, MappedStatement mappedStatement, BoundSql boundSql, FormMap<String, Object> formMap, List<Object> formMaps) throws Exception {
		Object table = null;
		String sql = "";
		Map<String, Object> mapfield=null;
		String field = null;
		List<ParameterMapping> bpm =new ArrayList<ParameterMapping>();
		Configuration configuration = mappedStatement.getConfiguration();
		if (null != formMap) {
			table = formMap.get("oss_table");
			mapfield=(Map<String, Object>) EhcacheUtils.get(table);
			field = mapfield.get("field").toString();
			sql = " select "+field+" from " + String.valueOf(table);
		}
		String sqlId = mappedStatement.getId();
		sqlId = sqlId.substring(sqlId.lastIndexOf(".") + 1);
		if ("findByWhere".equals(sqlId)) {// 根据条件查询
			if (null != formMap.get("where") && !isEmpty(formMap.get("where").toString())) {
				sql += " where " + formMap.get("where").toString();
			}
		} else if ("findByPage".equals(sqlId)) {// 分页时..必须 有PageView对象
			String[] fe = field.split(",");
			String param = "";
			for (String string : fe) {
				if (formMap.containsKey(string)) {
					Object v = formMap.get(string);
					if (v.toString().indexOf("%") > -1)// 处理模糊查询
					{
						if(v.toString().indexOf("%")==v.toString().lastIndexOf("%")&&v.toString().indexOf("%")==0)
						{	
							v = v.toString().substring(1);
							param += " and " + string + " like CONCAT('%', ?)";
						}else if(v.toString().indexOf("%")==v.toString().lastIndexOf("%")&&v.toString().indexOf("%")==v.toString().length())
						{	v = v.toString().substring(0,v.toString().length()-1);
							param += " and " + string + " like CONCAT(?, '%')";	
						}else
						{ 
							v = v.toString().substring(1,v.toString().length()-1);
							param += " and " + string + " like CONCAT(CONCAT('%', ?), '%')";	
						}
					} else {
						param += " and " + string + " = ?";
					}
					Builder mapping = new ParameterMapping.Builder(configuration, string, v.getClass());
					ParameterMapping parameter = mapping.build();
					bpm.add(parameter);
					ReflectHelper.setValueByFieldName(boundSql, "parameterMappings", bpm);
				}
			}
			if (StringUtils.isNotBlank(param)) {
				param = param.substring(param.indexOf("and") + 4);
				sql += " where " + param;
			}
			Object by = formMap.get("$orderby");
			if (null != by) {
				sql += " " + by;
			}
			Object paging = formMap.get("paging");
			if (null == paging) {
				throw new SystemException("调用findByPage接口,必须传入PageView对象! formMap.set(\"paging\", pageView);");
			} else if (isEmpty(paging.toString())) {
				throw new SystemException("调用findByPage接口,必须传入PageView对象! formMap.set(\"paging\", pageView);");
			}
			PageView pageView = (PageView) paging;
			setCount(sql,mappedStatement, connection, boundSql, pageView);
			sql = generatePagesSql(sql, pageView);
		} else if ("deleteByNames".equals(sqlId)) {
			sql = "delete from " + table + " where ";
			String param = "";
			Map<String, Object> map = new HashMap<String, Object>();
			for (Entry<String, Object> entry : formMap.entrySet()) {
				if (!"oss_table".equals(entry.getKey()) && null != entry.getValue() && !"_t".equals(entry.getKey())){
					String[] v = String.valueOf(entry.getValue()).split(",");
					String vs = "";
					for (int i = 0; i < v.length; i++) {
						vs+="?,";
						Builder mapping = new ParameterMapping.Builder(configuration, entry.getKey()+"_"+i, entry.getValue().getClass());
						ParameterMapping parameter = mapping.build();
						bpm.add(parameter);
						map.put(entry.getKey()+"_"+i, v[i]);
					}
					vs=Common.trimComma(vs);
					if(vs.split(",").length==1)
					    param += " and "+entry.getKey()+" = "+vs;
					else
						param += " and "+entry.getKey()+" in ("+vs+")";
					
				}
			}
			for (Entry<String, Object> entry : map.entrySet()) {
				formMap.put(entry.getKey(), entry.getValue());
			}
			if (StringUtils.isNotBlank(param)) {
				param = param.substring(param.indexOf("and") + 4);
				sql += param;
			}
		} else if ("findById".equals(sqlId)) {
			Object o = (Object) boundSql.getParameterObject();
		   TableSeg tb = (TableSeg) o.getClass().getAnnotation(TableSeg.class);
		   Map<String, Object> map = (Map<String, Object>) o;
		   String keyid = String.valueOf(map.get(tb.id()));
		   String[] v = keyid.split(",");
		   String vs = "";
		   for (int i = 0; i < v.length; i++) {
				vs+="?,";
				Builder mapping = new ParameterMapping.Builder(configuration, "value_"+i, v[i].getClass());
				ParameterMapping parameter = mapping.build();
				bpm.add(parameter);
				map.put("value_"+i, v[i]);
			}
			vs=Common.trimComma(vs);
			if(vs.split(",").length==1)
				sql += " where "+tb.id()+" = "+vs;
			else
				sql += " where "+tb.id()+" in ("+vs+")";
			
		} else if ("deleteById".equals(sqlId)) {
			sql = "delete from " + table + " where ";
				Object o = (Object) boundSql.getParameterObject();
			   TableSeg tb = (TableSeg) o.getClass().getAnnotation(TableSeg.class);
			   Map<String, Object> map = (Map<String, Object>) o;
			   String keyid = String.valueOf(map.get(tb.id()));
			   String[] v = keyid.split(",");
			   String vs = "";
			   for (int i = 0; i < v.length; i++) {
					vs+="?,";
					Builder mapping = new ParameterMapping.Builder(configuration, "value_"+i, v[i].getClass());
					ParameterMapping parameter = mapping.build();
					bpm.add(parameter);
					map.put("value_"+i, v[i]);
				}
				vs=Common.trimComma(vs);
				if(vs.split(",").length==1)
					sql += " "+tb.id()+" = "+vs;
				else
					sql += " "+tb.id()+" in ("+vs+")";
				
		} else if ("deleteByAttribute".equals(sqlId)) {
			sql = "delete from " + table + " where " + formMap.get("key");
			if(null!=formMap.get("value"))
			{
				String[] v = String.valueOf(formMap.get("value")).split(",");
				String vs = "";
				Map map = (Map) boundSql.getParameterObject();
				for (int i = 0; i < v.length; i++) {
					vs+="?,";
					Builder mapping = new ParameterMapping.Builder(configuration, "value_"+i, v[i].getClass());
					ParameterMapping parameter = mapping.build();
					bpm.add(parameter);
					map.put("value_"+i, v[i]);
				}
				vs=Common.trimComma(vs);
				sql += " in ("+vs+")";
			}
		} else if ("findByNames".equals(sqlId)) {
			String[] fe = field.split(",");
			String param = "";
			for (String string : fe) {
				if (formMap.containsKey(string)) {
					Object v = formMap.get(string);
					if (v.toString().indexOf("%") > -1)// 处理模糊查询
					{
						if(v.toString().indexOf("%")==v.toString().lastIndexOf("%")&&v.toString().indexOf("%")==0)
						{	
							v = v.toString().substring(1);
							param += " and " + string + " like CONCAT('%', ?)";
						}else if(v.toString().indexOf("%")==v.toString().lastIndexOf("%")&&v.toString().indexOf("%")==v.toString().length())
						{	v = v.toString().substring(0,v.toString().length()-1);
							param += " and " + string + " like CONCAT(?, '%')";	
						}else
						{ 
							v = v.toString().substring(1,v.toString().length()-1);
							param += " and " + string + " like CONCAT(CONCAT('%', ?), '%')";	
						}
					} else {
						param += " and " + string + " = ?";
					}
					Builder mapping = new ParameterMapping.Builder(configuration, string, v.getClass());
					ParameterMapping parameter = mapping.build();
					bpm.add(parameter);
				}
			}
			
			if (StringUtils.isNotBlank(param)) {
				param = param.substring(param.indexOf("and") + 4);
				sql += " where " + param;

			}
			Object by = formMap.get("$orderby");
			if (null != by) {
				sql += " " + by;
			}
		} else if ("findByAttribute".equals(sqlId)) {
			String key = formMap.get("key")+"";
			sql = "select "+field+" from " + table + " where " + key;
			Object v = formMap.get("value");
			Map map = (Map) boundSql.getParameterObject();
			if (null != v && v.toString().indexOf("%") > -1)// 处理模糊查询
			{
				if(v.toString().indexOf("%")==v.toString().lastIndexOf("%")&&v.toString().indexOf("%")==0)
				{	
					v = v.toString().substring(1);
					sql +=" like CONCAT('%', ?)";
				}else if(v.toString().indexOf("%")==v.toString().lastIndexOf("%")&&v.toString().indexOf("%")==v.toString().length())
				{	v = v.toString().substring(0,v.toString().length()-1);
					sql +=" like CONCAT(?, '%')";	
				}else
				{ 
					v = v.toString().substring(1,v.toString().length()-1);
					sql +=" like CONCAT(CONCAT('%', ?), '%')";	
				}
				Builder mapping = new ParameterMapping.Builder(configuration, "key_", v.getClass());
				ParameterMapping parameter = mapping.build();
				bpm.add(parameter);
				map.put("key_", v);
			} else {
				String[] vv = String.valueOf(v).split(",");
				String vs = "";
				for (int i = 0; i < vv.length; i++) {
					vs+="?,";
					Builder mapping = new ParameterMapping.Builder(configuration, "key_"+i, vv[i].getClass());
					ParameterMapping parameter = mapping.build();
					bpm.add(parameter);
					map.put("key_"+i, vv[i]);
				}
				vs=Common.trimComma(vs);
				sql += " in ("+vs+")";

			}
		} else if ("addEntity".equals(sqlId)) {
			String[] fe = field.split(",");
			String fieldString = "";
			String fieldValues = "";
			for (String string : fe) {
				Object v = formMap.get(string);
				if (null != v && !isEmpty(v.toString())) {
					fieldString += string + ",";
					fieldValues += "?,";
					Builder mapping = new ParameterMapping.Builder(configuration, string, v.getClass());
					ParameterMapping parameter = mapping.build();
					bpm.add(parameter);
				}
				
			}
			sql = "insert into " + table.toString() + " (" + Common.trimComma(fieldString) + ")  values (" + Common.trimComma(fieldValues) + ")";
		} else if ("editEntity".equals(sqlId)) {
			String[] fe = field.split(",");
			String fieldString = "";
			String where = "";
			String column_key = "";
			for (String string : fe) {
				Object v = formMap.get(string);
				if (null != v && !isEmpty(v.toString())) {
					String key = mapfield.get("column_key").toString();
					if (!isEmpty(key)) {
						if (key.equals(string)) {
							column_key=key;
							where = "where " + column_key + " = ?";
						} else {
							fieldString += string + "= ?,";
							Builder mapping = new ParameterMapping.Builder(configuration, string, v.getClass());
							ParameterMapping parameter = mapping.build();
							bpm.add(parameter);
						}
					} else {
						throw new SystemException("update操作没有找到主键!");
					}
				}
			}
			if(Common.isNotEmpty(column_key)){
				Builder mapping = new ParameterMapping.Builder(configuration, column_key,formMap.get(column_key).getClass());
				ParameterMapping parameter = mapping.build();
				bpm.add(parameter);
			}
			sql = "update " + table.toString() + " set " + Common.trimComma(fieldString) + " " + where;
		} else if ("batchSave".equals(sqlId)) {
			if (null != formMaps && formMaps.size() > 0) {
				table = toHashMap(formMaps.get(0)).get("oss_table");
				mapfield=(Map<String, Object>) EhcacheUtils.get(table);
				field = mapfield.get("field").toString();
				sql = "insert into " + table;
				String fieldString = "";
				String fs = "";
				String fd = "";
				String fieldValues = "";
				String fvs = "";
				for (int i = 0; i < formMaps.size(); i++) {
					Object object = formMaps.get(i);
					FormMap<String, Object> froMmap = (FormMap<String, Object>) object;
					String[] fe = field.split(",");
					for (String string : fe) {
						Object v = froMmap.get(string);
						if (null != v && !isEmpty(v.toString())) {
							fieldString += string + ",";
							fieldValues += "'" + Common.mysqltoString(v.toString()) + "',";
						}
					}
					if (i == 0) {
						fd = fieldString;
					}
					fvs += "(" + Common.trimComma(fieldValues) + "),";
					fs += " insert into " + table.toString() + " (" + Common.trimComma(fieldString) + ")  values (" + Common.trimComma(fieldValues) + ");";
					fieldValues = "";
					fieldString = "";
				}
				String v = Common.trimComma(fvs);
				sql = "insert into " + table.toString() + " (" + Common.trimComma(fd) + ")  values " + Common.trimComma(fvs) + "";
				String[] vs = v.split("\\),");
				boolean b = false;
				for (String string : vs) {
					if (string.split(",").length != fd.split(",").length) {
						b = true;
					}
				}
				if (b) {
					sql = fs.substring(0, fs.length() - 1);
				}
			}else{
				throw new SystemException("调用公共方法异常! error: this list->obj is null");
			}
		} else {
			throw new SystemException("调用公共方法异常!");
		}
		if(bpm.size()>0){
			ReflectHelper.setValueByFieldName(boundSql, "parameterMappings", bpm);
		}
		ReflectHelper.setValueByFieldName(boundSql, "sql", sql);
	}
	/**
	 * 从数据库里查询总的记录数并计算总页数，回写进分页参数<code>PageParameter</code>,这样调用者就可用通过 分页参数
	 * <code>PageParameter</code>获得相关信息。
	 * 
	 * @param sql
	 * @param mappedStatement 
	 * @param connection
	 * @param mappedStatement
	 * @param boundSql
	 * @param page
	 * @throws SQLException
	 */
	private void setCount(String sql, MappedStatement mappedStatement, Connection connection, BoundSql boundSql, PageView pageView) throws SQLException {
		// 记录总记录数
		PreparedStatement countStmt = null;
		ResultSet rs = null;
		try {
			String countSql = "select count(1) from " + suffixStr(removeOrderBys(sql));
			countStmt = connection.prepareStatement(countSql);
            BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(),countSql.toString(),boundSql.getParameterMappings(),boundSql.getParameterObject());    
            setParameters(countStmt,mappedStatement,countBS,boundSql.getParameterObject());    
			rs = countStmt.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = ((Number) rs.getObject(1)).intValue();
			}
			pageView.setRowCount(count);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				countStmt.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 判断变量是否为空
	 * 
	 * @param s
	 * @return
	 */
	public boolean isEmpty(String s) {
		if (null == s || "".equals(s) || "".equals(s.trim()) || "null".equalsIgnoreCase(s)) {
			return true;
		} else {
			return false;
		}
	}

	public FormMap<String, Object> toHashMap(Object parameterObject) {
		FormMap<String, Object> froMmap = (FormMap<String, Object>) parameterObject;
		try {
			String name = parameterObject.getClass().getName();
			Class<?> clazz = Class.forName(name);
			boolean flag = clazz.isAnnotationPresent(TableSeg.class); // 某个类是不是存在TableSeg注解
			if (flag) {
				TableSeg table = (TableSeg) clazz.getAnnotation(TableSeg.class);
				logger.info(" 公共方法被调用,传入参数 ==>> " + froMmap);
				froMmap.put("oss_table", table.tableName());
			} else {
				throw new SystemException("在" + name + " 没有找到数据库表对应该的注解!");
			}
			return froMmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return froMmap;
	}
	/**  
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler  
     * @param ps  
     * @param mappedStatement  
     * @param boundSql  
     * @param parameterObject  
     * @throws SQLException  
     */    
    @SuppressWarnings("rawtypes")
	private void setParameters(PreparedStatement ps,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject) throws SQLException {    
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());    
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();    
        if (parameterMappings != null) {    
            Configuration configuration = mappedStatement.getConfiguration();    
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();    
            MetaObject metaObject = parameterObject == null ? null: configuration.newMetaObject(parameterObject);    
            for (int i = 0; i < parameterMappings.size(); i++) {    
                ParameterMapping parameterMapping = parameterMappings.get(i);    
                if (parameterMapping.getMode() != ParameterMode.OUT) {    
                    Object value;    
                    String propertyName = parameterMapping.getProperty();    
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);    
                    if (parameterObject == null) {    
                        value = null;    
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {    
                        value = parameterObject;    
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {    
                        value = boundSql.getAdditionalParameter(propertyName);    
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)&& boundSql.hasAdditionalParameter(prop.getName())) {    
                        value = boundSql.getAdditionalParameter(prop.getName());    
                        if (value != null) {    
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));    
                        }    
                    } else {    
                        value = metaObject == null ? null : metaObject.getValue(propertyName);    
                    }    
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();    
                    if (typeHandler == null) {    
                        throw new ExecutorException("There was no TypeHandler found for parameter "+ propertyName + " of statement "+ mappedStatement.getId());    
                    }    
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());    
                }    
            }    
        }    
    } 
}
