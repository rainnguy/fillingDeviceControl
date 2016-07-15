package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.StationFormBean;
import com.banxian.entity.equip.StationMap;
import com.banxian.mapper.base.BaseMapper;

public interface StationMapper extends BaseMapper {

	public List<StationMap> findStationData(StationMap stationMap);
	
	public List<StationFormBean> findStationList(StationFormBean staFormBean);

	public List<StationFormBean> findStationByOrgName(StationFormBean staFormBean);
	
	public int getOrgMaxCode();
}
