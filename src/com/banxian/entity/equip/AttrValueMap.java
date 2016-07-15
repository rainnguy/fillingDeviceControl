package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.CollectDataMapper;
import com.banxian.util.SpringIocUtils;

public class AttrValueMap extends FormMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3787337605231066253L;

	public static CollectDataMapper mapper() {
		return SpringIocUtils.getBean(CollectDataMapper.class);
	}
}
