package com.banxian.mapper;

import java.util.List;
import java.util.Map;

import com.banxian.mapper.base.BaseMapper;

public interface HistoryInfoMapper extends BaseMapper {

	public List<Map<String, Object>> findByPage2();
}
