package com.banxian.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.banxian.annotation.TableSeg;
import com.banxian.exception.SystemException;

public class ConfigUtils {
	private final Logger logger = Logger.getLogger(ConfigUtils.class);
	private final String GET_STATION_SQL = "SELECT * FROM SYS_ORGA";
	public static void main(String[] args) {
		new ConfigUtils().initTableField();
	}
	/**
	 * 初始化数据库表字段到缓存
	 */
	public void initTableField() {
		// 记录总记录数
		Statement countStmt = null;
		ResultSet rs = null;
		Connection connection = null; // 表示数据库的连接对象
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Properties pro = PropertiesUtils.getjdbcProperties();
			Class.forName(pro.getProperty("jdbc.driverClass")); // 1、使用CLASS
			String url = pro.getProperty("jdbc.url");
			String db = url.substring(url.lastIndexOf("/")+1);
			if(db.indexOf("?")>-1){
				db=db.substring(0, db.indexOf("?"));
			}
			connection = DriverManager.getConnection(url, pro.getProperty("jdbc.username"),
					pro.getProperty("jdbc.password")); // 2、连接数据库
			String packageName = "com.banxian.entity";
			// List<String> classNames = getClassName(packageName);
			List<String> classNames = ClassUtil.getClassName(packageName, true);
			String tabs = "";
			if (classNames != null) {
				for (String className : classNames) {
					Class<?> clazz = Class.forName(className);
					boolean flag = clazz.isAnnotationPresent(TableSeg.class); // 某个类是不是存在TableSeg注解
					if (flag) {
						TableSeg table = (TableSeg) clazz.getAnnotation(TableSeg.class);
						tabs+="'"+table.tableName()+"',";
						map.put(table.tableName(), table.id());
					} 
				}
			}
			tabs=Common.trimComma(tabs);
			//尽量减少对数据库/IO流操作,一次查询所有表的的字段
			//mysql
			//String sql = "select TABLE_NAME,group_concat(COLUMN_NAME) COLUMN_NAME from information_schema.columns where table_name in ("+tabs+") and table_schema = '"+db+"'  GROUP BY TABLE_NAME" ;
			//sqlserver
			String sql ="";
			if(url.toUpperCase().indexOf("MYSQL")>-1){//mysql
				sql = "select TABLE_NAME,group_concat(COLUMN_NAME) COLUMN_NAME from information_schema.columns where table_name in ("+tabs+") and table_schema = '"+db+"'  GROUP BY TABLE_NAME" ;
			}else if(url.toUpperCase().indexOf("SQLSERVER")>-1){//sqlserver
				sql ="SELECT TABLE_NAME,COLUMN_NAME from( "+
						"SELECT t.name TABLE_NAME, "+
						"(SELECT name+',' FROM syscolumns  "+
						"WHERE id=a.id "+
						 "FOR XML PATH('')) AS COLUMN_NAME "+
						"FROM syscolumns a inner join sysobjects t on a.id = t.id and t.name in ("+tabs+")) a "+
						"GROUP BY a.TABLE_NAME,a.COLUMN_NAME";
			}else if(url.toUpperCase().indexOf("ORACLE")>-1){//ORACLE
				sql ="select TABLE_NAME,WMSYS.WM_CONCAT(column_name)COLUMN_NAME from user_tab_columns"
						  +" where Table_Name in ("+tabs+") GROUP BY table_name"; 
			}else{
				throw new SystemException(" -------------- error ----  ConfigUtils.java  ----  获取数据库类型失败  ------------------------ ");
			}
			
			countStmt = connection.createStatement();
			rs = countStmt.executeQuery(sql);
			while (rs.next()) {
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("field", Common.trimComma(rs.getString("COLUMN_NAME")));
				String ble =rs.getString("TABLE_NAME");//表名
				m.put("column_key", map.get(ble));//获取表的主键
				System.out.println(ble);
				EhcacheUtils.put(ble, m);//某表对应的主键和字段放到缓存
			}
			
			initialTableData(countStmt);
		} catch (Exception e) {
			logger.error(" 初始化数据失败,没法加载表字段到缓存 -->> "+e.fillInStackTrace());
			e.printStackTrace();
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
	
	@SuppressWarnings("unchecked")
	private void initialTableData(Statement countStmt) throws SQLException {
		ResultSet rs = null;
		rs = countStmt.executeQuery(GET_STATION_SQL);
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapData = null;
		while (rs.next()) {
			mapData = new HashMap<String, Object>();
			Map<String, Object> mapfield=(Map<String, Object>) EhcacheUtils.get("sys_orga");
			String field = mapfield.get("field").toString();
			String[] columnArr = field.split(",");
			for(int i = 0; i < columnArr.length; i++){
				mapData.put(columnArr[i], rs.getObject(columnArr[i]));
			}
			dataList.add(mapData);
			EhcacheUtils.put(SysConsts.SYS_ORGA_DATA, dataList);//某表对应的主键和字段放到缓存
		}
	}
	
}
