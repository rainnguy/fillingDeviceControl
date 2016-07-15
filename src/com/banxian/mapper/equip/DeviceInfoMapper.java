package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.equip.DeviceInfoMap;
import com.banxian.mapper.base.BaseMapper;

public interface DeviceInfoMapper extends BaseMapper {

	public List<DeviceInfoMap> findHistoryData(DeviceInfoMap deviceInfoMap);
	
	/**
	 * 获取设备id
	 * @param deviceInfoMap
	 * @return
	 */
	public List<DeviceInfoMap> findDeviceId(DeviceInfoMap deviceInfoMap);
}
