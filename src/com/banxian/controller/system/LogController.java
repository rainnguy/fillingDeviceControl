package com.banxian.controller.system;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.controller.index.BaseController;
import com.banxian.entity.OperLogFormBean;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/log/")
public class LogController extends BaseController {

	@RequestMapping("list")
	public String listUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/log/list";
	}

	@ResponseBody
	@RequestMapping("findByPage")
	public PageView findByPage( String pageNow,
			String pageSize) throws Exception {
		OperLogFormBean logFormMap = getFormMap(OperLogFormBean.class);
		String order = " order by id asc";
		logFormMap.put("$orderby", order);
        pageView=logFormMap.findByPage(getPageView(pageNow, pageSize));
		return pageView;
	}
	
	
}