package com.banxian.entity;

import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;



/**
 * 实体表
 */
@TableSeg(tableName = "user_login", id="id")
public class UserLoginFormBean extends FormMap<String,Object>{

	/**
	 *@descript
	  * @author _wsq 2016-03-10
	  * @version 2.0v
	 */
	private static final long serialVersionUID = 1L;
}
