package com.banxian.bean;

import com.banxian.entity.base.FormMap;

/**
 * 页面历史信息实体
 * 
 * @author xk
 * 
 */
public class HistoryInfoBean extends FormMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 530531574905031812L;

	/** 站点编号 */
	private String orgId = null;
	/** 站点名称 */
	private String orgName = null;
	/** 当前时间 */
	private String currTime = null;
	/** 设备名称 */
	private String deviceName = null;
	/** 储罐压力 */
	private String storagePressure = null;
	/** LNG液位 */
	private String lngLiquidPosition = null;
	/** LNG差压 */
	private String differentialPressure = null;
	/** LNG高度 */
	private String lngHeight = null;
	/** LNG重量 */
	private String lngWeight = null;
	/** 泵前压力 */
	private String beforePressure = null;
	/** 泵后压力 */
	private String affterPressure = null;
	/** 泵池温度 */
	private String pumpTemp = null;
	/** 变频器频率 */
	private String converterFrequency = null;
	/** 变频器电流 */
	private String converterCurrent = null;
	/** 仪表风压力 */
	private String meterPressure = null;
	/** 环境温度 */
	private String ambientTemp = null;
}
