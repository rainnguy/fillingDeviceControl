package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.StationMapper;
import com.banxian.util.SpringIocUtils;

/**
 * 站点信息实体表
 * 
 * @author xk
 * 
 */
public class StationMap extends FormMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -431708646594929870L;

	public static StationMapper mapper() {
		return SpringIocUtils.getBean(StationMapper.class);
	}
}
