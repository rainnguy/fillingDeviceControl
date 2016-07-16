package com.banxian.mapper.equip;

import java.util.List;

import com.banxian.entity.equip.AttrKeyMap;
import com.banxian.entity.equip.AttrStaMap;
import com.banxian.entity.equip.AttrValueMap;
import com.banxian.mapper.base.BaseMapper;

public interface CollectDataMapper extends BaseMapper {

	public void insertAttrKeyTemp(List<AttrKeyMap> attrKeyList);

	public void insertAttrStatusTemp(List<AttrStaMap> attrStaList);

	public void insertAttrValueTemp(List<AttrValueMap> attrValueList);

	public void insertAttrKey(List<AttrKeyMap> attrKeyList);

	public void insertAttrStatus(List<AttrStaMap> attrStaList);

	public void insertAttrValue(List<AttrValueMap> attrValueList);
}
