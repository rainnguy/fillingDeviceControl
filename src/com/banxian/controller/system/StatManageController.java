package com.banxian.controller.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.banxian.controller.index.BaseController;
import com.banxian.util.Common;

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

}