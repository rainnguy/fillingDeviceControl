package com.banxian.entity;

import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;
import com.banxian.mapper.RoleMapper;
import com.banxian.util.SpringIocUtils;

/**
 * 实体表
 */
@TableSeg(tableName = "ly_role", id = "id")
public class RoleFormMap extends FormMap<String, Object> {

	/**
	 * @descript
	 * @author _wsq 2016-03-10
	 * @version 2.0v
	 */
	private static final long serialVersionUID = 1L;
	
	public static RoleMapper mapper() {
		return SpringIocUtils.getBean(RoleMapper.class);
	}

}
