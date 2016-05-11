package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.equip.AlarmInfoMap;
import com.banxian.entity.equip.UnsolvedAlarmInfoMap;
import com.banxian.mapper.base.BaseMapper;

public interface AlarmInfoMapper extends BaseMapper {

	public List<AlarmInfoMap> findAlarmData(AlarmInfoMap alarmInfoMap);

	public List<UnsolvedAlarmInfoMap> findUnsolvedAlarmData(
			UnsolvedAlarmInfoMap unsolvedAlarmInfoMap);
}
