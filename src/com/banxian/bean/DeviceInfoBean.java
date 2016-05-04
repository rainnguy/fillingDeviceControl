package com.banxian.bean;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.HistoryInfoMapper;
import com.banxian.util.SpringIocUtils;

/**
 * 查询历史信息实体
 * 
 * @author xk
 * 
 */
public class DeviceInfoBean extends FormMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2827626782006461043L;

	public static HistoryInfoMapper mapper() {
		return SpringIocUtils.getBean(HistoryInfoMapper.class);
	}

}
