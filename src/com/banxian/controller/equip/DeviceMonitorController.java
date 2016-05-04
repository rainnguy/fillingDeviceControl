package com.banxian.controller.equip;

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
@RequestMapping("/deviceMonitor/")
public class DeviceMonitorController extends BaseController {

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


}