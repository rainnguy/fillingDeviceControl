package com.banxian.controller.equip;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.controller.index.BaseController;
import com.banxian.entity.equip.DetailInfoMap;
import com.banxian.entity.equip.DeviceInfoMap;
import com.banxian.entity.equip.UnsolvedAlarmInfoMap;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.DateUtil;
import com.banxian.util.JsonUtils;
import com.banxian.util.POIUtils;
import com.banxian.util.PropertiesUtils;
import com.banxian.util.SysConsts;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/gasEquip/")
public class gasEquipController extends BaseController {
	
	/**
	 * 历史信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findHistoryData")
	public PageView findHistoryData(String pageNow, String pageSize) throws Exception {

		DeviceInfoMap deviceInfoMap = getFormMap(DeviceInfoMap.class);
		deviceInfoMap = toFormMap(deviceInfoMap, pageNow, pageSize);
		// 用户权限
		deviceInfoMap.put(SysConsts.ROLE_KEY, Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		deviceInfoMap.put(SysConsts.ORG_CODE, Common.findAttrValue(SysConsts.ORG_CODE));

		pageView.setRecords(DeviceInfoMap.mapper().findHistoryData(deviceInfoMap));

		return pageView;
	}
	
	/**
	 * 单站详细信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("equipDetail")
	public String findDetailData(ModelMap model) throws Exception {

		DetailInfoMap detailInfoMap = getFormMap(DetailInfoMap.class);
		UnsolvedAlarmInfoMap unsolvedAlarmInfoMap = getFormMap(UnsolvedAlarmInfoMap.class);
		
		// 用户所属站的编号
		unsolvedAlarmInfoMap.put(SysConsts.ORG_CODE, detailInfoMap.get(SysConsts.ORG_CODE));
		
		List<DetailInfoMap> detailInfo = DetailInfoMap.mapper().findDetailData(detailInfoMap);
		
		model.addAttribute("equipData", detailInfo);
		model.addAttribute("unsolvedAlarmData", UnsolvedAlarmInfoMap.mapper().
				findUnsolvedAlarmData(unsolvedAlarmInfoMap));
		model.addAttribute(SysConsts.ORG_CODE,detailInfoMap.get(SysConsts.ORG_CODE)); 
		if(0 < detailInfo.size()) {
			model.addAttribute("stationName", detailInfo.get(0).get(SysConsts.STATION_NAME));
		}else {
			model.addAttribute("stationName", "未知站");
		}
				
		return Common.BACKGROUND_PATH + "/system/equip/equipDetail";

	}

	/**
	 * 修改配置　
	 * 
	 * @param request
	 * @param nodeId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/modifySer")
	public Map<String, Object> modifySer(String key, String value) throws Exception {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		try {
			// 从输入流中读取属性列表（键和元素对）
			PropertiesUtils.modifyProperties(key, value);
		} catch (Exception e) {
			dataMap.put("flag", false);
		}
		dataMap.put("flag", true);
		return dataMap;
	}

	/**
	 * 历史信息
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("historyInfo")
	public String historyInfo(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		// 判断权限不是admin
		if ("admin".equals(Common.findAttrValue(SysConsts.ROLE_KEY)) == false) {
			// 获取站点类型
			model.addAttribute(SysConsts.ORG_TYPE, Common.findAttrValue(SysConsts.ORG_TYPE));
		}
		return Common.BACKGROUND_PATH + "/system/equip/historyInfo";
	}
	
	/**
	 * 导出历史信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/export")
	public void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String fileName = "历史信息_" + DateUtil.getCurrDate4();
		DeviceInfoMap deviceInfoMap = findHasHMap(DeviceInfoMap.class);
		// 用户权限
		deviceInfoMap.put(SysConsts.ROLE_KEY, Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		deviceInfoMap.put(SysConsts.ORG_CODE, Common.findAttrValue(SysConsts.ORG_CODE));
		
		String exportData = deviceInfoMap.getStr("exportData");// 列表头的json字符串

		List<Map<String, Object>> listMap = JsonUtils.parseJSONList(exportData);

		List<DeviceInfoMap> list = DeviceInfoMap.mapper().findHistoryData(deviceInfoMap);
		
		POIUtils.exportToExcel(response, listMap, list, fileName);
	}
}