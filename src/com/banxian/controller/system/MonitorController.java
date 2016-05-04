package com.banxian.controller.system;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.controller.index.BaseController;
import com.banxian.util.Common;
import com.banxian.util.PropertiesUtils;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/monitor/")
public class MonitorController extends BaseController {

	@RequestMapping("list")
	public String listUI() throws Exception {
		return Common.BACKGROUND_PATH + "/system/monitor/list";
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

}