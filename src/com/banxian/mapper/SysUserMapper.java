package com.banxian.mapper;

import java.util.List;

import com.banxian.entity.UserFormBean;
import com.banxian.mapper.base.BaseMapper;


public interface SysUserMapper extends BaseMapper{

	public List<UserFormBean> findUserPage(UserFormBean sysUserMap);
	
	public List<UserFormBean> findUserByAccName(UserFormBean sysUserMap);
	
}
