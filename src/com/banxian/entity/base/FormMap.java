package com.banxian.entity.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.banxian.annotation.TableSeg;
import com.banxian.exception.ParameterException;
import com.banxian.exception.SystemException;
import com.banxian.mapper.base.BaseMapper;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.SpringIocUtils;

/**
 * SpringMvc 把请求的所有参数封装到Map中,提供最常用的方法
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@SuppressWarnings("unchecked")
public abstract class FormMap<K, V> extends HashMap<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	public BaseMapper getRepository() {
		return SpringIocUtils.getBean(BaseMapper.class);
	}
	
	/**
	 * 1：调用公共findByPage接口,必须传入PageView对象!<br/>
	 * 2：根据多字段分页查询 <br/>
	 * 3：如果是多个id,用","组成字符串. <br/>
	 * 4：formMap.put("id","xxx")<br/>
	 * 5：formMap.put("name","xxx") <br/>
	 * 6：兼容模糊查询。 formMap.put("name","用户%") 或 formMap.put("name","%用户%") <br/>
	 * 7：兼容排序查询 : formMap.put("$orderby","order by id desc");  <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @return PageView
	 */
	public PageView findByPage(PageView pageView) {
		this.put((K)"paging", (V)pageView);
		pageView.setRecords(getRepository().findByPage(this));
		return pageView;
	}

	/**
	 * 1：返回所有数据<br/>
	 * 
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> findByAll() {
		return (List<T>) getRepository().findByNames(this);
	}
	
	
	/**
	 * 1：根据ID删除数据<br/>
	 * 2：如是多个Id,则 id="xxx,xxx,xxx"<br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @throws Exception
	 */
	public void deleteById(String id) throws Exception{
		TableSeg table = (TableSeg) this.getClass().getAnnotation(TableSeg.class);
		if (Common.isNotEmpty(id)) {
			this.put((K)table.id(), (V)id);
			getRepository().deleteById(this);
		} else {
			String results = "{\"results\":\"error\",\"message\":\"参数错误，id 不能为空！\"}";
			throw new ParameterException(results);
		}
	}

	/**
	 * 1：保存数据,保存数据后返回子类对象的所有数据包括id. <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * @throws Exception
	 */
	public <T> T save() throws Exception {
		getRepository().addEntity(this);
		TableSeg table = (TableSeg) this.getClass().getAnnotation(TableSeg.class);
		String table_id=table.id();
		if(table_id.split(",").length==1){
			if(this.get(table_id)==null){
				this.put((K)table_id, this.get("id"));
				this.remove("id");
			}
		}
		return (T) this;
	}

	/**
	 * 1：更新数据<br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @throws Exception
	 */
	public <T> T update() throws Exception {
		TableSeg table = (TableSeg) this.getClass().getAnnotation(TableSeg.class);
		String id = String.valueOf(this.get(table.id()));
		if (Common.isNotEmpty(id)) {
			getRepository().editEntity(this);
		} else {
			String results = "{\"results\":\"error\",\"message\":\"参数错误，" + this.getClass() + "没有找到" + table.id()
					+ "的值！\"}";
			throw new ParameterException(results);
		}
		return (T) this;
	}
	
	/**
	 * 1：根据ID获取数据 <br/>
	 * 2：如是多个Id,则 id="xxx,xxx,xxx"<br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * @return T
	 * @throws Exception
	 */
	public <T> T findById(String id){
		TableSeg table = (TableSeg) this.getClass().getAnnotation(TableSeg.class);
		if (Common.isNotEmpty(id)) {
			try {
				this.put((K) table.id(), (V) id);
				return (T) getRepository().findById(this);
			} catch (Exception e) {
				String results = "{\"results\":\"error\",\"message\":\""+e+"\"}";
				throw new SystemException(results);
			}
		} else {
			String results = "{\"results\":\"error\",\"message\":\"参数错误，id 不能为空！\"}";
			throw new ParameterException(results);
		}
	}
	
	/**
	 * 1：批量保存数据,如果是mysql,在驱动连接加上&allowMultiQueries=true这个参数 <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @throws Exception
	 */
	public <T> void batchSave(List<T> formMap) throws Exception{
		 getRepository().batchSave(formMap);
	}

	/**
	 * 1：自定义where查询条件,返回是一个List<T>集合 <br/>
	 * <b>2：注意：这个方法有SQL注入风险,请谨慎使用</b><br/>
	 * 3：返回查询条件数据,如不传入.则返回所有数据..如果附加条件.如下 <br/>
	 * 4：formMap.put('where","id=XX and name= XX order by XX") <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @return List<T>
	 */
	public <T> List<T> findByWhere() {
		return (List<T>) getRepository().findByWhere(this);
	}
	
	/**
	 * 1：根据属性获取数据<br/>
	 * 2：如果是多个属性值,用","组成字符串. <br/>
	 * 3：formMap.put("id","xxx") 或 formMap.put("id","xxx,xxx,xxx")<br/>
	 * 4：formMap.put("name","xxx") 或 formMap.put("name","xxx,xxx,xxx") <br/>
	 * 5：如果值是“,”逗号分隔，则不支持模糊查询
	 * 6：兼容模糊查询。 formMap.put("name","用户%") 或 formMap.put("name","%用户%") <br/>
	 * 7：兼容排序查询 : formMap.put("$orderby","order by id desc");  <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * @return List<T>
	 * @throws Exception
	 */
	public <T> List<T> findByNames() {
		return (List<T>) getRepository().findByNames(this);
	}
	
	/**
	 * 1：根据属性查询数据，返回第一个对象 <br/>
	 * 2：如果是多个属性值,用","组成字符串. <br/>
	 * 3：formMap.put("id","xxx") 或 formMap.put("id","xxx,xxx,xxx")<br/>
	 * 4：formMap.put("name","xxx") 或 formMap.put("name","xxx,xxx,xxx") <br/>
	 * 5：如果值是“,”逗号分隔，则不支持模糊查询
	 * 6：兼容模糊查询。 formMap.put("name","用户%") 或 formMap.put("name","%用户%") <br/>
	 * 7：兼容排序查询 : formMap.put("$orderby","order by id desc");  <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * @return T
	 */
	public <T> T findbyFrist(){
		List<T> ms = (List<T>) getRepository().findByNames(this);
		if(null != ms && ms.size()>=1)
			return (T) ms.get(0);
		else
			return null;
	}
	
	/**
	 * 2：如果是多个属性,用","组成字符串. <br/>
	 * 3：formMap.put("id","xxx") 或 formMap.put("id","xxx,xxx,xxx")<br/>
	 * 4：formMap.put("name","xxx") 或 formMap.put("name","xxx,xxx,xxx") <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @throws Exception
	 */
	public void deleteByNames()throws Exception{
		 getRepository().deleteByNames(this);
	}
	
	/**
	 * 1：根据某个字段删除数据 <br/>
	 * 2：如果是多个属性值,用","组成字符串,value=xxx,xxx,xxx <br/>
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @throws Exception
	 */
	public void deleteByAttribute(String key,Object value) throws Exception{
		this.put((K)"key", (V)key);
		this.put((K)"value", (V)value);
		getRepository().deleteByAttribute(this);
	}
	
	
	/**
	 * 1：根据某个字段查询数据 <br/>
	 * 2：如果是多个属性值,用","组成字符串. value=xxx,xxx,xxx <br/>
	 * 3：如果值是“,”逗号分隔，则不支持模糊查询
	 * 4：兼容模糊查询。 value="用户%" 或  value="%用户%" <br/> 
	 * <b>author：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</b><br/>
	 * <b>date：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2015-10-26</b><br/>
	 * <b>version：</b><br/>
	 * <b>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.0v</b>
	 * 
	 * @return List<T>
	 */
	public <T> List<T> findByAttribute(String key,Object value){
			this.put((K)"key", (V)key);
			this.put((K)"value", (V)value);
			return (List<T>) getRepository().findByAttribute(this);
	}
	
	/**
	 * Get attribute of mysql type: varchar, char, enum, set, text, tinytext,
	 * mediumtext, longtext
	 */
	public String getStr(String attr) {
		return (String) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: int, integer, tinyint(n) n > 1, smallint,
	 * mediumint
	 */
	public Integer getInt(String attr) {
		return (Integer) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: bigint, unsign int
	 */
	public Long getLong(String attr) {
		return (Long) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: unsigned bigint
	 */
	public java.math.BigInteger getBigInteger(String attr) {
		return (java.math.BigInteger) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: date, year
	 */
	public java.util.Date getDate(String attr) {
		return (java.util.Date) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: time
	 */
	public java.sql.Time getTime(String attr) {
		return (java.sql.Time) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: timestamp, datetime
	 */
	public java.sql.Timestamp getTimestamp(String attr) {
		return (java.sql.Timestamp) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: real, double
	 */
	public Double getDouble(String attr) {
		return (Double) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: float
	 */
	public Float getFloat(String attr) {
		return (Float) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: bit, tinyint(1)
	 */
	public Boolean getBoolean(String attr) {
		return (Boolean) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: decimal, numeric
	 */
	public java.math.BigDecimal getBigDecimal(String attr) {
		return (java.math.BigDecimal) this.get(attr);
	}

	/**
	 * Get attribute of mysql type: binary, varbinary, tinyblob, blob,
	 * mediumblob, longblob
	 */
	public byte[] getBytes(String attr) {
		return (byte[]) this.get(attr);
	}

	/**
	 * Get attribute of any type that extends from Number
	 */
	public Number getNumber(String attr) {
		return (Number) this.get(attr);
	}

	/**
	 * Return attribute names of this model.
	 */
	public String[] getAttrNames() {
		Set<K> attrNameSet = this.keySet();
		return attrNameSet.toArray(new String[attrNameSet.size()]);
	}

	/**
	 * Return attribute values of this model.
	 */
	public Object[] getAttrValues() {
		Collection<V> attrValueCollection = values();
		return attrValueCollection.toArray(new Object[attrValueCollection.size()]);
	}

}
