package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.DetailInfoMapper;
import com.banxian.util.SpringIocUtils;

/**
 * 查询历史信息实体
 * 
 * @author xk
 * 
 */
public class DetailInfoMap extends FormMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2827626782006461043L;

	public static DetailInfoMapper mapper() {
		return SpringIocUtils.getBean(DetailInfoMapper.class);
	}

}
