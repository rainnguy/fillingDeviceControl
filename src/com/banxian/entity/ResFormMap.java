package com.banxian.entity;


import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;
import com.banxian.mapper.ResourcesMapper;
import com.banxian.util.SpringIocUtils;



/**
 * 菜单实体表
 */
@TableSeg(tableName = "ly_resources", id="id")
public class ResFormMap extends FormMap<String,Object>{
	/**
	 *@descript
 	 * @author _wsq 2016-03-10
	 * @version 2.0v
	 */
	private static final long serialVersionUID = 1L;
	
	public static ResourcesMapper mapper() {
		return SpringIocUtils.getBean(ResourcesMapper.class);
	}
}
