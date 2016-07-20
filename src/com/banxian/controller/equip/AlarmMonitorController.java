package com.banxian.controller.equip;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.controller.index.BaseController;
import com.banxian.entity.equip.AlarmInfoMap;
import com.banxian.mapper.equip.AlarmInfoMapper;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.DateUtil;
import com.banxian.util.JsonUtils;
import com.banxian.util.POIUtils;
import com.banxian.util.SysConsts;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/gasAlarm/")
public class AlarmMonitorController extends BaseController {
	
	@Inject
	private AlarmInfoMapper alarmInfoMapper;
	
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
	 * 历史报警信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findHistoryAlarmData")
	public PageView findHistoryAlarmData(String pageNow, String pageSize) throws Exception {

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
	 * 导出历史报警信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/export")
	public void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String fileName = "历史报警_" + DateUtil.getCurrDate4();
		AlarmInfoMap alarmInfoMap = findHasHMap(AlarmInfoMap.class);
		// 用户权限
		alarmInfoMap.put(SysConsts.ROLE_KEY, Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		alarmInfoMap.put(SysConsts.ORG_CODE, Common.findAttrValue(SysConsts.ORG_CODE));
		
		String exportData = alarmInfoMap.getStr("exportData");// 列表头的json字符串

		List<Map<String, Object>> listMap = JsonUtils.parseJSONList(exportData);

		List<AlarmInfoMap> list = AlarmInfoMap.mapper().findHistoryAlarmData(alarmInfoMap);
		
		POIUtils.exportToExcel(response, listMap, list, fileName);
	}
	
	/**
	 * update display alarm tips
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("deleteAlarmInfo")
	@ResponseBody
	public String deleteAlarmInfo(Model model) throws Exception {

		AlarmInfoMap infoMap = getFormMap(AlarmInfoMap.class);
		
		udpateAlarmInfoStatus(infoMap);
		
		infoMap.deleteById(getPara("id"));
		return "success";
		 
	}

	private void udpateAlarmInfoStatus(AlarmInfoMap infoMap) {

		String alarmId = getPara(SysConsts.ID);
		AlarmInfoMap resultMap = infoMap.findById(alarmId);
		Map<String, String> parameterMap = new HashMap<String, String>();
		parameterMap.put(SysConsts.DEVICE_ID, String.valueOf((Integer) resultMap.get(SysConsts.DEVICE_ID)));
		parameterMap.put(SysConsts.ALARM_DEF_ID, String.valueOf((Integer) resultMap.get(SysConsts.ALARM_DEF_ID)));
		parameterMap.put(SysConsts.ALARM_TIME, DateUtil.formatDateByFormat((Timestamp) resultMap.get(SysConsts.ALARM_TIME), DateUtil.const1));
		alarmInfoMapper.updateAlarmStatus(parameterMap);
		
		
	}
}