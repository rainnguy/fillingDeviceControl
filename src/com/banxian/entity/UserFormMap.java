package com.banxian.entity;

import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;
import com.banxian.mapper.UserMapper;
import com.banxian.util.SpringIocUtils;

/**
 * user实体表
 */
@TableSeg(tableName = "ly_user", id = "id")
public class UserFormMap extends FormMap<String, Object> {

	/**
	 * @descript
	 * @author _wsq 2016-03-10
	 * @version 2.0v
	 */
	private static final long serialVersionUID = 1L;
	public static UserMapper mapper() {
		return SpringIocUtils.getBean(UserMapper.class);
	}
}
