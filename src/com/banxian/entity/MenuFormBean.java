package com.banxian.entity;


import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;
import com.banxian.mapper.SysMenuMapper;
import com.banxian.util.SpringIocUtils;



/**
 * 菜单实体表
 */
@TableSeg(tableName = "sys_menu", id="menuId")
public class MenuFormBean extends FormMap<String,Object>{
	/**
	 *@descript
 	 * @author _wsq 2016-03-10
	 * @version 2.0v
	 */
	private static final long serialVersionUID = 1L;
	
	public static SysMenuMapper mapper() {
		return SpringIocUtils.getBean(SysMenuMapper.class);
	}
}
