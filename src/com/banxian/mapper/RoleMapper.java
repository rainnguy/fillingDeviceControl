package com.banxian.mapper;

import java.util.List;

import com.banxian.entity.RoleFormMap;
import com.banxian.mapper.base.BaseMapper;

public interface RoleMapper extends BaseMapper{
	public List<RoleFormMap> seletUserRole(RoleFormMap map);
	
	public void deleteById(RoleFormMap map);
}
