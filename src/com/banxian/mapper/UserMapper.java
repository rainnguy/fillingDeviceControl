package com.banxian.mapper;

import java.util.List;

import com.banxian.entity.UserFormMap;
import com.banxian.mapper.base.BaseMapper;


public interface UserMapper extends BaseMapper{

	public List<UserFormMap> findUserPage(UserFormMap userFormMap);
	
}
