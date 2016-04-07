package com.banxian.controller.system;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.annotation.SystemLog;
import com.banxian.controller.index.BaseController;
import com.banxian.entity.RoleFormMap;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/role/")
public class RoleController extends BaseController {

	@RequestMapping("list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/role/list";
	}

	@ResponseBody
	@RequestMapping("findByPage")
	public PageView findByPage(String pageNow,
			String pageSize) throws Exception {
		RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
		pageView=roleFormMap.findByPage(getPageView(pageNow, pageSize));
		return pageView;
	}

	@RequestMapping("addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/role/add";
	}

	@ResponseBody
	@RequestMapping("addEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="组管理-新增组")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
		roleFormMap.save();
		return "success";
	}

	@ResponseBody
	@RequestMapping("deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="组管理-删除组")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		RoleFormMap roleFormMap = new RoleFormMap();
		for (String id : ids) {
			roleFormMap.deleteById(id);
		}
		return "success";
	}

	@RequestMapping("editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		if(Common.isNotEmpty(id)){
			RoleFormMap roleFormMap = new RoleFormMap();
			model.addAttribute("role", roleFormMap.findById(id));
		}
		return Common.BACKGROUND_PATH + "/system/role/edit";
	}

	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="组管理-修改组")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity() throws Exception {
		RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
		roleFormMap.update();
		return "success";
	}
	
	
	@RequestMapping("selRole")
	public String seletRole(Model model) throws Exception {
		RoleFormMap roleFormMap = getFormMap(RoleFormMap.class);
		Object userId = roleFormMap.get("userId");
		if(null!=userId){
			List<RoleFormMap> list = RoleFormMap.mapper().seletUserRole(roleFormMap);
			String ugid = "";
			for (RoleFormMap ml : list) {
				ugid += ml.get("id")+",";
			}
			ugid = Common.trimComma(ugid);
			model.addAttribute("txtRoleSelect", ugid);
			model.addAttribute("userRole", list);
			if(StringUtils.isNotBlank(ugid)){
				roleFormMap.put("where", " id not in ("+ugid+")");
			}
		}
		List<RoleFormMap> roles = roleFormMap.findByWhere();
		model.addAttribute("role", roles);
		return Common.BACKGROUND_PATH + "/system/user/roleSelect";
	}

}