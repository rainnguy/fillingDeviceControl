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
	
//	@ResponseBody
//	@RequestMapping("findByPage")
//	public PageView findByPage( String pageNow,
//			String pageSize) throws Exception {
//		ServerInfoFormMap serverInfoFormMap = getFormMap(ServerInfoFormMap.class);
//		pageView=serverInfoFormMap.findByPage(getPageView(pageNow, pageSize));
//		return pageView;
//	}
	
	@RequestMapping("info")
	public String info(Model model) throws Exception {
		model.addAttribute("cpu", PropertiesUtils.findPropertiesKey("cpu"));
		model.addAttribute("jvm", PropertiesUtils.findPropertiesKey("jvm"));
		model.addAttribute("ram", PropertiesUtils.findPropertiesKey("ram"));
		model.addAttribute("toEmail", PropertiesUtils.findPropertiesKey("toEmail"));
		return Common.BACKGROUND_PATH + "/system/monitor/info";
	}
	
	@RequestMapping("monitor")
	public String monitor() throws Exception {
		return Common.BACKGROUND_PATH + "/system/monitor/monitor";
	}
	
//	@RequestMapping("systemInfo")
//	public String systemInfo(Model model) throws Exception {
//		model.addAttribute("systemInfo", SystemInfo.SystemProperty());
//		return Common.BACKGROUND_PATH + "/system/monitor/systemInfo";
//	}
	
//	@ResponseBody
//	@RequestMapping("usage")
//	public ServerInfoFormMap usage(Model model) throws Exception {
//		return SystemInfo.usage(new Sigar());
//	}
	/**
	 * 修改配置　
	 * @param request
	 * @param nodeId
	 * @return
	 * @throws Exception
	 */
    @ResponseBody
	@RequestMapping("/modifySer")
    public Map<String, Object> modifySer(String key,String value) throws Exception{
    	Map<String, Object> dataMap = new HashMap<String,Object>();
    	try {
		// 从输入流中读取属性列表（键和元素对）
    		PropertiesUtils.modifyProperties(key, value);
		} catch (Exception e) {
			dataMap.put("flag", false);
		}
    	dataMap.put("flag", true);
		return dataMap;
    }
}