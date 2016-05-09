package com.banxian.entity.equip;

import java.util.List;

/**
 * 图表纵坐标数据
 * 
 * @author xk
 *
 */
public class Series {

	public String name;
    public String type;
    public List<Integer> data;
    public Series(String name, String type, List<Integer> data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
