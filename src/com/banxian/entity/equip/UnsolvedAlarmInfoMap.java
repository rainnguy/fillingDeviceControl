package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.AlarmInfoMapper;
import com.banxian.util.SpringIocUtils;

public class UnsolvedAlarmInfoMap extends FormMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3515389518903159762L;

	public static AlarmInfoMapper mapper() {
		return SpringIocUtils.getBean(AlarmInfoMapper.class);
	}
	
}
