package com.banxian.controller.equip;

import java.io.IOException;
import java.util.HashMap;
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
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
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
	 * 单站详细信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("equipDetail")
	public String findDetailData(ModelMap model) throws Exception {

		DeviceInfoMap deviceInfoMap = getFormMap(DeviceInfoMap.class);

		// 用户所属站的编号
		deviceInfoMap.put(SysConsts.ORG_CODE, deviceInfoMap.get(SysConsts.ORG_CODE));
		
		System.out.println(DeviceInfoMap.mapper().findHistoryData(deviceInfoMap));
		
		model.addAttribute("equipData", DeviceInfoMap.mapper().findHistoryData(deviceInfoMap));

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
	
	@RequestMapping("/export")
	public void download(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
	}
}