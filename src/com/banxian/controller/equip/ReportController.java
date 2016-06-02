package com.banxian.controller.equip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.controller.index.BaseController;
import com.banxian.entity.equip.EchartsBean;
import com.banxian.entity.equip.GraphDataMap;
import com.banxian.entity.equip.Series;
import com.banxian.util.Common;
import com.banxian.util.DateUtil;
import com.banxian.util.EhcacheUtils;
import com.banxian.util.SysConsts;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/gasReport/")
public class ReportController extends BaseController {

//	@RequestMapping("list")
//	public String listUI() throws Exception {
//		return Common.BACKGROUND_PATH + "/system/monitor/list";
//	}
	
	/**
	 * 折线图
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("lineGraph")
	public String lineGraph(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		
		Map<String, String> orgCodeMap = new HashMap<String, String>();
		// 设置默认的空值
		orgCodeMap.put("", "选择站点");
		@SuppressWarnings("unchecked")
		// 获取所有的站点
		List<Map<String, Object>> stationMap =  (List<Map<String, Object>>) EhcacheUtils.get(SysConsts.SYS_ORGA_DATA);

		// 用户权限
		String roleKey=Common.findAttrValue(SysConsts.ROLE_KEY);
		if("admin".equals(roleKey)){
			for(Map<String, Object> map : stationMap){
				String orgName = (String) map.get("orgName");
				String orgNum = (String) map.get("orgCode");
				orgCodeMap.put(orgNum, orgName);
			}
		}else{
			// 用户所属站的编号
			String selfOrgCode=Common.findAttrValue(SysConsts.ORG_CODE);
			for(Map<String, Object> map : stationMap){
				//  当前记录的编号
				String orgNum = map.get("orgCode").toString();
				if(selfOrgCode.equals(orgNum)){
					// 当前记录的名称
					String orgName = map.get("orgName").toString();
					orgCodeMap.put(orgNum, orgName);
				}
			}
		}
		
		model.addAttribute("orgValue", orgCodeMap);
		model.addAttribute(SysConsts.ORG_TYPE, Common.findAttrValue(SysConsts.ORG_TYPE));
		
		return Common.BACKGROUND_PATH + "/system/equip/lineGraph";
	}
	
	/**
	 * 折线图数据
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findGraphData")
	public Map<String, Object> findGraphData(ModelMap model) throws Exception {
		
		GraphDataMap graphDataMap = getFormMap(GraphDataMap.class);
		
		EchartsBean echartBean = dealWithGraphData(graphDataMap);
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		// 数据分组
		dataMap.put("legend", echartBean.legend);
		// 横坐标
		dataMap.put("axis", echartBean.axis);
		// 纵坐标
		dataMap.put("series", echartBean.series);
		
		return dataMap;
	}

	/**
	 * 处理折线图的数据
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

		// 设定横坐标值
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
				// 如果当前数据的时间与横坐标一致，则获取纵坐标的值，否则纵坐标设为0
				if (timeTemp == tempList.size() + 1) {
					// 设定纵坐标数据
					tempList.add(Integer.valueOf(map.get("lngLiquidPosition")
							.toString()));
				} else {
					for (int i = tempList.size() + 1; i < timeTemp - 1; i++) {
						tempList.add(0);
					}
					tempList.add(Integer.valueOf(map.get("lngLiquidPosition")
							.toString()));
				}
			} else {
				// 设备id不同则把设备名称加到数据分组中,把纵坐标数据加到纵坐标中
				legend.add(map.get("deviceName").toString());
				if (tempList.size() != 0) {
					// 当月或当日，在当前时间以后的数据纵坐标都设为0
					for (int i = tempList.size(); i < axis.size(); i++) {
						tempList.add(Integer.valueOf("0"));
					}
					series.add(new Series(lastDeviceName, "line", tempList));
					tempList = new ArrayList<Integer>();
				}

				// 设定纵坐标数据
				for (int i = 0; i < timeTemp - 1; i++) {
					tempList.add(Integer.valueOf("0"));
				}
				tempList.add(Integer.valueOf(map.get("lngLiquidPosition")
						.toString()));
			}

			lastDevId = currtDevId;
			lastDeviceName = map.get("deviceName").toString();
		}
		// 当月或当日，在当前时间以后的数据纵坐标都设为0
		for (int i = tempList.size(); i < axis.size(); i++) {
			tempList.add(Integer.valueOf("0"));
		}
		series.add(new Series(lastDeviceName, "line", tempList));

		EchartsBean echartsBean = new EchartsBean(legend, axis, series);
		return echartsBean;
	}
}