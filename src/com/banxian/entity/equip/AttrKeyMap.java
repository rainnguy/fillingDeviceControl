package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.CollectDataMapper;
import com.banxian.util.SpringIocUtils;

public class AttrKeyMap extends FormMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3671826391907228496L;

	public static CollectDataMapper mapper() {
		return SpringIocUtils.getBean(CollectDataMapper.class);
	}
}
