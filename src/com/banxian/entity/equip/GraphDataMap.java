package com.banxian.entity.equip;

import com.banxian.entity.base.FormMap;
import com.banxian.mapper.equip.GraphDataMapper;
import com.banxian.util.SpringIocUtils;

/**
 * 图表信息实体
 * 
 * @author xk
 * 
 */
public class GraphDataMap extends FormMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8838486850322647934L;

	public static GraphDataMapper mapper() {
		return SpringIocUtils.getBean(GraphDataMapper.class);
	}
}
