package com.banxian.controller.equip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.banxian.entity.equip.DeviceInfoMap;
import com.banxian.entity.equip.EchartsBean;
import com.banxian.entity.equip.GraphDataMap;
import com.banxian.entity.equip.Series;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.DateUtil;
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
	public PageView findHistoryData(String pageNow, String pageSize)
			throws Exception {

		DeviceInfoMap deviceInfoMap = getFormMap(DeviceInfoMap.class);
		deviceInfoMap = toFormMap(deviceInfoMap, pageNow, pageSize);
		// 用户权限
		deviceInfoMap.put(SysConsts.ROLE_KEY,
				Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		deviceInfoMap.put(SysConsts.ORG_CODE,
				Common.findAttrValue(SysConsts.ORG_CODE));

		pageView.setRecords(DeviceInfoMap.mapper().findHistoryData(
				deviceInfoMap));

		return pageView;
	}

	/**
	 * 折线图数据
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("findGraphData")
	public String findGraphData(ModelMap model) throws Exception {

		GraphDataMap graphDataMap = getFormMap(GraphDataMap.class);
		
		EchartsBean echartBean = dealWithGraphData(graphDataMap);
	
		//数据分组
		model.addAttribute("graphLegend", echartBean.legend);
		//横坐标
		model.addAttribute("graphAxis", echartBean.axis);
		//纵坐标
		model.addAttribute("graphSeries", echartBean.series);
		
		return Common.BACKGROUND_PATH + "/system/equip/lineGraph";
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
	public Map<String, Object> modifySer(String key, String value)
			throws Exception {
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
	 * 单站详细信息
	 * 
	 * @author xk
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detail")
	public String detail(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/equip/detail";
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
			model.addAttribute(SysConsts.ORG_TYPE,
					Common.findAttrValue(SysConsts.ORG_TYPE));
		}
		return Common.BACKGROUND_PATH + "/system/equip/historyInfo";
	}

//	/**
//	 * 折线图
//	 * 
//	 * @param model
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("lineGraph")
//	public String lineGraph(Model model) throws Exception {
//		model.addAttribute("res", findByRes());
//
//		// model.addAttribute(SysConsts.ORG_TYPE,
//		// Common.findAttrValue(SysConsts.ORG_TYPE));
//
//		return "redirect:findGraphData.sxml";
//	}

	@RequestMapping("/export")
	public void download(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
	}
	
	/**
	 * 
	 * @param graphDataMap
	 * @return
	 */
	private EchartsBean dealWithGraphData(GraphDataMap graphDataMap) {
		
		// 用户所属站的编号
		graphDataMap.put(SysConsts.ORG_CODE,
				Common.findAttrValue(SysConsts.ORG_CODE));
		// 获取当前的年月
		Date nowDate = new Date();
		graphDataMap.put("currYearMonth", DateUtil.format(nowDate, "yyyyMM"));
		List<GraphDataMap> mapList = GraphDataMap.mapper().findGraphData(
				graphDataMap);
		// 数据分组
		List<String> legend = new ArrayList<String>();
		// 横坐标
		List<String> axis = null;
		// 纵坐标
		List<Series> series = new ArrayList<Series>();
		// 页面输入的“日”
		String day = "";
		if (graphDataMap.get("day") != null) {
			day = graphDataMap.get("day").toString();
		}

		// 页面输入的“月”
		String month = "";
		if (graphDataMap.get("month") != null) {
			month = graphDataMap.get("month").toString();
		}
		// 页面输入的“年”
		String year = "";
		if (graphDataMap.get("year") != null) {
			year = graphDataMap.get("year").toString();
		}

		if (day.isEmpty() == false) {
			axis = new ArrayList<String>(Arrays.asList(new String[] { "01",
					"02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
					"12" }));
		} else {
			// 获取该月的最后一天
			String lastDay = null;
			String tempDay = null;
			if (month.isEmpty() == false) {
				tempDay = DateUtil.getMonthEnd(month);
			} else {
				tempDay = DateUtil.getMonthEnd(DateUtil.format(new Date()));
			}
			lastDay = tempDay.substring(tempDay.length() - 2);

			if ("31".equals(lastDay)) {
				axis = new ArrayList<String>(Arrays.asList(new String[] { "01",
						"02", "03", "04", "05", "06", "07", "08", "09", "10",
						"11", "12", "13", "14", "15", "16", "17", "18", "19",
						"20", "21", "22", "23", "24", "25", "26", "27", "28",
						"29", "30", "31" }));
			} else if ("30".equals(lastDay)) {
				axis = new ArrayList<String>(Arrays.asList(new String[] { "01",
						"02", "03", "04", "05", "06", "07", "08", "09", "10",
						"11", "12", "13", "14", "15", "16", "17", "18", "19",
						"20", "21", "22", "23", "24", "25", "26", "27", "28",
						"29", "30" }));
			} else if ("28".equals(lastDay)) {
				axis = new ArrayList<String>(Arrays.asList(new String[] { "01",
						"02", "03", "04", "05", "06", "07", "08", "09", "10",
						"11", "12", "13", "14", "15", "16", "17", "18", "19",
						"20", "21", "22", "23", "24", "25", "26", "27", "28" }));
			} else {
				axis = new ArrayList<String>(Arrays.asList(new String[] { "01",
						"02", "03", "04", "05", "06", "07", "08", "09", "10",
						"11", "12", "13", "14", "15", "16", "17", "18", "19",
						"20", "21", "22", "23", "24", "25", "26", "27", "28",
						"29" }));
			}
		}

		String currtDevId = "";
		String lastDevId = "";
		List<Integer> tempList = new ArrayList<Integer>();
		String lastDeviceName = null;
		for (GraphDataMap map : mapList) {
			currtDevId = map.get("deviceId").toString();

			// 获取当前数据的时间
			int timeTemp = Integer.valueOf(map.get("currTime").toString());

			// 设备id没变则为同一组数据
			if (currtDevId != null && currtDevId.equals(lastDevId)) {

				if (timeTemp == tempList.size() + 1) {
					// 设定纵坐标数据
					tempList.add(Integer.valueOf(map.get("lngLiquidPosition").toString()));
				} else {
					for (int i = tempList.size() + 1; i < timeTemp - 1; i++) {
						tempList.add(0);
					}
					tempList.add(Integer.valueOf(map.get("lngLiquidPosition").toString()));
				}
			} else {
				// 设备id不同则把设备名称加到数据分组中,把纵坐标数据加到纵坐标中
				legend.add(map.get("deviceName").toString());
				if (tempList.size() != 0) {
					//TODO
					for(int i=tempList.size();i<axis.size();i++){
						tempList.add(Integer.valueOf("0"));
					}
					series.add(new Series(lastDeviceName, "line", tempList));
					tempList = new ArrayList<Integer>();
				}

				// 设定纵坐标数据
				for (int i = 0; i < timeTemp - 1; i++) {
					tempList.add(Integer.valueOf("0"));
				}
				tempList.add(Integer.valueOf(map.get("lngLiquidPosition").toString()));
			}

			lastDevId = currtDevId;
			lastDeviceName = map.get("deviceName").toString();
		}

		for(int i=tempList.size();i<axis.size();i++){
			tempList.add(Integer.valueOf("0"));
		}
		series.add(new Series(lastDeviceName, "line", tempList));
		
		EchartsBean echartsBean = new EchartsBean(legend, axis, series);
		return echartsBean;
	}
}