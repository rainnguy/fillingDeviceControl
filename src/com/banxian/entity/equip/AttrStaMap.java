package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.CollectDataMapper;
import com.banxian.util.SpringIocUtils;

public class AttrStaMap extends FormMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6366897745812488567L;

	public static CollectDataMapper mapper() {
		return SpringIocUtils.getBean(CollectDataMapper.class);
	}
}
