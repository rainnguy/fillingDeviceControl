package com.banxian.entity.equip;

import com.banxian.annotation.TableSeg;
import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.AlarmInfoMapper;
import com.banxian.util.SpringIocUtils;

@TableSeg(tableName = "alarm_info_temp", id="id")
public class AlarmInfoMap extends FormMap<String, Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2610254052059409702L;

	public static AlarmInfoMapper mapper() {
		return SpringIocUtils.getBean(AlarmInfoMapper.class);
	}
}
