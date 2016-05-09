package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.equip.GraphDataMap;
import com.banxian.mapper.base.BaseMapper;

public interface GraphDataMapper extends BaseMapper {

	public List<GraphDataMap> findGraphData(GraphDataMap graphDataMap);
}
