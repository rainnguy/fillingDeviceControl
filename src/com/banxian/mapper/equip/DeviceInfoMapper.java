package com.banxian.mapper.equip;

import java.util.List;
import java.util.Map;

import com.banxian.entity.equip.DeviceInfoMap;
import com.banxian.mapper.base.BaseMapper;

public interface DeviceInfoMapper extends BaseMapper {

	public List<DeviceInfoMap> findHistoryData(DeviceInfoMap deviceInfoMap);
}
