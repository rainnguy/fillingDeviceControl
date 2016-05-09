package com.banxian.entity.equip;

import java.util.ArrayList;
import java.util.List;

/**
 * Echarts图表实体
 * 
 * @author xk
 * 
 */
public class EchartsBean {

	public List<String> legend = new ArrayList<String>();// 数据分组
	public List<String> axis = new ArrayList<String>();// 横坐标
	public List<Series> series = new ArrayList<Series>();// 纵坐标

	public EchartsBean(List<String> legendList, List<String> categoryList,
			List<Series> seriesList) {
		super();
		this.legend = legendList;
		this.axis = categoryList;
		this.series = seriesList;
	}
}
