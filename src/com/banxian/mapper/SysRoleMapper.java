package com.banxian.mapper;

import java.util.List;

import com.banxian.entity.RoleFormBean;
import com.banxian.mapper.base.BaseMapper;

public interface SysRoleMapper extends BaseMapper {
	
	public List<RoleFormBean> seletUserRole(RoleFormBean map);

	public void deleteById(RoleFormBean map);
}
