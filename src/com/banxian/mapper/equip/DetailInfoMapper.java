package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.equip.DetailInfoMap;
import com.banxian.mapper.base.BaseMapper;

public interface DetailInfoMapper extends BaseMapper {

	public List<DetailInfoMap> findDetailData(DetailInfoMap detailInfoMap);
}
