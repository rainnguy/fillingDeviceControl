package com.banxian.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个是列表树形式显示的实体, 这里的字段是在前台显示所有的,可修改
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
public class TreeObject {
	private Integer id;
	private Integer parentId;
	private String menuName;
	private String parentName;
	private String menuKey;
	private String menuUrl;
	private Integer sortNo;
	private String menuType;
	private String menuDesc;
	private String menuIcon;
	private Integer menuHide;
	private List<TreeObject> children = new ArrayList<TreeObject>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getMenuKey() {
		return menuKey;
	}

	public void setMenuKey(String menuKey) {
		this.menuKey = menuKey;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public Integer getSortNo() {
		return sortNo;
	}

	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public String getMenuDesc() {
		return menuDesc;
	}

	public void setMenuDesc(String menuDesc) {
		this.menuDesc = menuDesc;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public Integer getMenuHide() {
		return menuHide;
	}

	public void setMenuHide(Integer menuHide) {
		this.menuHide = menuHide;
	}

	public List<TreeObject> getChildren() {
		return children;
	}

	public void setChildren(List<TreeObject> children) {
		this.children = children;
	}

}
