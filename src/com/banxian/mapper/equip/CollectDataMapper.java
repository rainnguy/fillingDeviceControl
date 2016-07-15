package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.equip.AttrKeyMap;
import com.banxian.entity.equip.AttrStaMap;
import com.banxian.entity.equip.AttrValueMap;
import com.banxian.mapper.base.BaseMapper;

public interface CollectDataMapper extends BaseMapper {

	public void insertAttrKeyTemp(List<AttrKeyMap> attrKeyList);
	
	public void insertAttrStaTemp(List<AttrStaMap> attrStaList);
	
	public void insertAttrValueTemp(List<AttrValueMap> attrValueList);
	
}
