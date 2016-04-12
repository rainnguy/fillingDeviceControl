package com.banxian.mapper;

import java.util.List;

import com.banxian.entity.MenuFormBean;
import com.banxian.mapper.base.BaseMapper;

public interface SysMenuMapper extends BaseMapper {

	public List<MenuFormBean> findChildlists(MenuFormBean map);

	public List<MenuFormBean> findRes(MenuFormBean map);

	public void updateSortOrder(List<MenuFormBean> map);

	public List<MenuFormBean> findUserResourcess(String userId);

}
