package com.banxian.controller.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.bean.DeviceInfoBean;
import com.banxian.bean.HistoryInfoBean;
import com.banxian.controller.index.BaseController;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.JsonUtils;
import com.banxian.util.POIUtils;
import com.banxian.util.PropertiesUtils;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/staManage/")
public class StatManageController extends BaseController {

	@RequestMapping("list")
	public String listUI() throws Exception {
		return Common.BACKGROUND_PATH + "/system/monitor/list";
	}

	// @ResponseBody
	// @RequestMapping("findByPage")
	// public PageView findByPage(String pageNow, String pageSize)
	// throws Exception {
	// DeviceInfoBean deviceInfoBean = getFormMap(DeviceInfoBean.class);
	// deviceInfoBean=toFormMap(deviceInfoBean, pageNow, pageSize);
	// pageView.setRecords(DeviceInfoBean.mapper().findByPage(deviceInfoBean));//不调用默认分页,调用自已的mapper中findByPage
	//
	//
	// // pageView = deviceInfoBean.findByPage(getPageView(pageNow, pageSize));
	// return pageView;
	// }

	/**
	 * 历史信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findByPage2")
	public PageView findByPage2(String pageNow, String pageSize)
			throws Exception {
		DeviceInfoBean deviceInfoBean = getFormMap(DeviceInfoBean.class);
		deviceInfoBean = toFormMap(deviceInfoBean, pageNow, pageSize);

		@SuppressWarnings("unused")
		List<Map<String, Object>> deviceInfoMap = DeviceInfoBean.mapper()
				.findByPage2();

		List<HistoryInfoBean> infoBeanList = new ArrayList<HistoryInfoBean>();
		HistoryInfoBean infoBean = new HistoryInfoBean();
		// 当前数据的时间
		String currTime = "";
		// 上一条数据的时间
		String lastTime = "";
		// 当前数据的设备id
		String currDeviceId = "";
		// 上一条数据的设备id
		String lastDeviceId = "";
		// 当前数据的设备类型
		String currType = "";
		// 上一条数据的设备类型
		String lastType = "";
		// 计数器，list的一条数据有5个属性
		int temp = 0;
		for (Map<String, Object> map : deviceInfoMap) {
			temp++;
			currTime = map.get("currTime").toString();
			currDeviceId = map.get("deviceId").toString();
			currType = map.get("deviceType").toString();

			// 判断时间相同
			if (currTime.equals(lastTime)) {
				// 判断设备类型相同
				if (currType.equals(lastType) && "1".equals(currType)) {
					// 判断设备ID相同
					if (currDeviceId.equals(lastDeviceId)) {
						switch (temp) {
						case 1:
							// 储罐压力
							infoBean.put("storagePressure", map
									.get("attrValue").toString());
							break;
						case 2:
							// 储罐液位
							infoBean.put("lngLiquidPosition",
									map.get("attrValue").toString());
							break;
						case 3:
							// LNG差压
							infoBean.put("differentialPressure",
									map.get("attrValue").toString());
							break;
						case 4:
							// LNG高度
							infoBean.put("lngHeight", map.get("attrValue")
									.toString());
							break;
						case 5:
							// LNG重量
							infoBean.put("lngWeight", map.get("attrValue")
									.toString());
							break;
						}
					} else {
						infoBean = new HistoryInfoBean();
						infoBean.put("orgId", map.get("orgId").toString());
						infoBean.put("orgName", map.get("orgName").toString());
						infoBean.put("currTime", map.get("currTime").toString());
						infoBean.put("deviceName", map.get("deviceName")
								.toString());
						// 储罐压力
						infoBean.put("storagePressure", map.get("attrValue")
								.toString());
						infoBean.put("meterPressure", map.get("meterPressure")
								.toString());
						infoBean.put("ambientTemp", map.get("ambientTemp")
								.toString());
					}
				} else if (currType.equals(lastType) && "2".equals(currType)) {
					if (currDeviceId.equals(lastDeviceId)) {
						switch (temp) {
						case 1:
							// 泵前压力
							infoBean.put("beforePressure", map.get("attrValue")
									.toString());
							break;
						case 2:
							// 泵后压力
							infoBean.put("affterPressure", map.get("attrValue")
									.toString());
							break;
						case 3:
							// 泵池温度
							infoBean.put("pumpTemp", map.get("attrValue")
									.toString());
							break;
						case 4:
							// 变频器频率
							infoBean.put("converterFrequency",
									map.get("attrValue").toString());
							break;
						case 5:
							// 变频器电流
							infoBean.put("converterCurrent",
									map.get("attrValue").toString());
							break;
						}
					} else {
						infoBean = new HistoryInfoBean();
						infoBean.put("orgId", map.get("orgId").toString());
						infoBean.put("orgName", map.get("orgName").toString());
						infoBean.put("currTime", map.get("currTime").toString());
						infoBean.put("deviceName", map.get("deviceName")
								.toString());
						// 泵前压力
						infoBean.put("beforePressure", map.get("attrValue")
								.toString());
						infoBean.put("meterPressure", map.get("meterPressure")
								.toString());
						infoBean.put("ambientTemp", map.get("ambientTemp")
								.toString());
					}
				} else if (currType.equals(lastType) == false
						&& "1".equals(currType)) {
					infoBean = new HistoryInfoBean();
					infoBean.put("orgId", map.get("orgId").toString());
					infoBean.put("orgName", map.get("orgName").toString());
					infoBean.put("currTime", map.get("currTime").toString());
					infoBean.put("deviceName", map.get("deviceName").toString());
					// 储罐压力
					infoBean.put("storagePressure", map.get("attrValue")
							.toString());
					infoBean.put("meterPressure", map.get("meterPressure")
							.toString());
					infoBean.put("ambientTemp", map.get("ambientTemp")
							.toString());
				} else if (currType.equals(lastType) == false
						&& "2".equals(currType)) {
					infoBean = new HistoryInfoBean();
					infoBean.put("orgId", map.get("orgId").toString());
					infoBean.put("orgName", map.get("orgName").toString());
					infoBean.put("currTime", map.get("currTime").toString());
					infoBean.put("deviceName", map.get("deviceName").toString());
					// 泵前压力
					infoBean.put("beforePressure", map.get("attrValue")
							.toString());
					infoBean.put("meterPressure", map.get("meterPressure")
							.toString());
					infoBean.put("ambientTemp", map.get("ambientTemp")
							.toString());
				}
			} else {
				infoBean = new HistoryInfoBean();
				infoBean.put("orgId", map.get("orgId").toString());
				infoBean.put("orgName", map.get("orgName").toString());
				infoBean.put("currTime", map.get("currTime").toString());
				infoBean.put("deviceName", map.get("deviceName").toString());
				infoBean.put("meterPressure", map.get("meterPressure")
						.toString());
				infoBean.put("ambientTemp", map.get("ambientTemp").toString());

				if ("1".equals(currType)) {
					// 储罐压力
					infoBean.put("storagePressure", map.get("attrValue")
							.toString());
				} else if ("2".equals(currType)) {
					// 泵前压力
					infoBean.put("beforePressure", map.get("attrValue")
							.toString());
				}
			}

			lastTime = currTime;
			lastDeviceId = currDeviceId;
			lastType = currType;

			if (temp == 5) {
				temp = 0;
				infoBeanList.add(infoBean);
			}
		}

		pageView.setRecords(infoBeanList);

		return pageView;
	}

	@RequestMapping("info")
	public String info(Model model) throws Exception {
		model.addAttribute("cpu", PropertiesUtils.findPropertiesKey("cpu"));
		model.addAttribute("jvm", PropertiesUtils.findPropertiesKey("jvm"));
		model.addAttribute("ram", PropertiesUtils.findPropertiesKey("ram"));
		model.addAttribute("toEmail",
				PropertiesUtils.findPropertiesKey("toEmail"));
		return Common.BACKGROUND_PATH + "/system/monitor/info";
	}

	@RequestMapping("monitor")
	public String monitor() throws Exception {
		return Common.BACKGROUND_PATH + "/system/monitor/monitor";
	}

	// @RequestMapping("systemInfo")
	// public String systemInfo(Model model) throws Exception {
	// model.addAttribute("systemInfo", SystemInfo.SystemProperty());
	// return Common.BACKGROUND_PATH + "/system/monitor/systemInfo";
	// }
	//
	// @ResponseBody
	// @RequestMapping("usage")
	// public ServerInfoFormMap usage(Model model) throws Exception {
	// return SystemInfo.usage(new Sigar());
	// }

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
		return Common.BACKGROUND_PATH + "/system/monitor/detail";
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
		return Common.BACKGROUND_PATH + "/system/monitor/historyInfo";
	}

	@RequestMapping("/export")
	public void download(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String fileName = "用户列表";
		DeviceInfoBean deviceInfoBean = findHasHMap(DeviceInfoBean.class);
		// exportData =
		// [{"colkey":"sql_info","name":"SQL语句","hide":false},
		// {"colkey":"total_time","name":"总响应时长","hide":false},
		// {"colkey":"avg_time","name":"平均响应时长","hide":false},
		// {"colkey":"record_time","name":"记录时间","hide":false},
		// {"colkey":"call_count","name":"请求次数","hide":false}
		// ]
		String exportData = deviceInfoBean.getStr("exportData");// 列表头的json字符串

		List<Map<String, Object>> listMap = JsonUtils.parseJSONList(exportData);

		List<DeviceInfoBean> lis = DeviceInfoBean.mapper().findByPage(
				deviceInfoBean);
		POIUtils.exportToExcel(response, listMap, lis, fileName);
	}
}