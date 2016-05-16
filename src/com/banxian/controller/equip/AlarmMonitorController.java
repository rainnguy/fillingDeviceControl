package com.banxian.controller.equip;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.controller.index.BaseController;
import com.banxian.entity.equip.AlarmInfoMap;
import com.banxian.entity.equip.UnsolvedAlarmInfoMap;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.SysConsts;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/gasAlarm/")
public class AlarmMonitorController extends BaseController {
	
	/**
	 * 历史报警
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("historyAlarm")
	public String historyAlarm(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/equip/historyAlarm";
	}
	
	/**
	 * 历史 报警信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findHistoryAlarmData")
	public PageView findHistoryAlarmData(String pageNow, String pageSize)
			throws Exception {

		AlarmInfoMap alarmInfoMap = getFormMap(AlarmInfoMap.class);
		alarmInfoMap = toFormMap(alarmInfoMap, pageNow, pageSize);
		// 用户权限
		alarmInfoMap.put(SysConsts.ROLE_KEY,Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		alarmInfoMap.put(SysConsts.ORG_CODE,Common.findAttrValue(SysConsts.ORG_CODE));

		pageView.setRecords(AlarmInfoMap.mapper().findHistoryAlarmData(alarmInfoMap));

		return pageView;
	}
	
	/**
	 * 未处理报警
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("unsolvedAlarm")
	public String unsolvedAlarm(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/equip/unsolvedAlarm";
	}

	/**
	 * 未处理报警信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("unsolvedAlarmList")
	public PageView unsolvedAlarmList(String pageNow, String pageSize)
			throws Exception {

		UnsolvedAlarmInfoMap unsolvedAlarmMap = getFormMap(UnsolvedAlarmInfoMap.class);
		unsolvedAlarmMap = toFormMap(unsolvedAlarmMap, pageNow, pageSize);

		// 用户所属站的编号
		unsolvedAlarmMap.put(SysConsts.ORG_CODE,
				Common.findAttrValue(SysConsts.ORG_CODE));

		pageView.setRecords(UnsolvedAlarmInfoMap.mapper()
				.findUnsolvedAlarmData(unsolvedAlarmMap));

		return pageView;
	}
}