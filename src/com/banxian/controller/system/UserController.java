package com.banxian.controller.system;


import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.annotation.SystemLog;
import com.banxian.controller.index.BaseController;
import com.banxian.entity.ResUserFormMap;
import com.banxian.entity.UserFormMap;
import com.banxian.entity.UserGroupsFormMap;
import com.banxian.exception.SystemException;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.PasswordHelper;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/user/")
public class UserController extends BaseController {
	
	@RequestMapping("list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/user/list";
	}

	@ResponseBody
	@RequestMapping("findByPage")
	public PageView findByPage( String pageNow,
			String pageSize) throws Exception {
		UserFormMap userFormMap = getFormMap(UserFormMap.class);
		userFormMap=toFormMap(userFormMap, pageNow, pageSize);
        pageView.setRecords(UserFormMap.mapper().findUserPage(userFormMap));//不调用默认分页,调用自已的mapper中findUserPage
        return pageView;
	}

	@RequestMapping("addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/user/add";
	}

	@ResponseBody
	@RequestMapping("addEntity")
	@SystemLog(module="系统管理",methods="用户管理-新增用户")//凡需要处理业务逻辑的.都需要记录操作日志
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	public String addEntity(String txtGroupsSelect){
		try {
			UserFormMap userFormMap = getFormMap(UserFormMap.class);
			userFormMap.put("txtGroupsSelect", txtGroupsSelect);
			PasswordHelper passwordHelper = new PasswordHelper();
			userFormMap.put("password","123456789");
			passwordHelper.encryptPassword(userFormMap);
			userFormMap.save();
			if (!Common.isEmpty(txtGroupsSelect)) {
				String[] txt = txtGroupsSelect.split(",");
				UserGroupsFormMap userGroupsFormMap =null;
				for (String roleId : txt) {
					userGroupsFormMap = new UserGroupsFormMap();
					userGroupsFormMap.put("userId", userFormMap.get("id"));
					userGroupsFormMap.put("roleId", roleId);
					userGroupsFormMap.save();
				}
			}
		} catch (Exception e) {
			 throw new SystemException("添加账号异常");
		}
		return "success";
	}

	@ResponseBody
	@RequestMapping("deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-删除用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			new UserGroupsFormMap().deleteByAttribute("userId", id);
			new ResUserFormMap().deleteByAttribute("userId", id);
			new UserFormMap().deleteByAttribute("id", id);
		}
		return "success";
	}

	@RequestMapping("editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara("id");
		if(Common.isNotEmpty(id)){
			model.addAttribute("user", new UserFormMap().findById(id));
		}
		return Common.BACKGROUND_PATH + "/system/user/edit";
	}

	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-修改用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(String txtGroupsSelect) throws Exception {
		UserFormMap userFormMap = getFormMap(UserFormMap.class);
		userFormMap.put("txtGroupsSelect", txtGroupsSelect);
		userFormMap.update();
		new UserGroupsFormMap().deleteByAttribute("userId", userFormMap.get("id"));
		if(!Common.isEmpty(txtGroupsSelect)){
			String[] txt = txtGroupsSelect.split(",");
			UserGroupsFormMap userGroupsFormMap = null;
			for (String roleId : txt) {
			    userGroupsFormMap = new UserGroupsFormMap();
				userGroupsFormMap.put("userId", userFormMap.get("id"));
				userGroupsFormMap.put("roleId", roleId);
				userGroupsFormMap.save();
			}
		}
		return "success";
	}
	/**
	 * 验证账号是否存在
	 * 
	 * @author _wsq 2016-03-10
	 * @version 2.0v
	 * @param name
	 * @return
	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(String name) {
		UserFormMap account = new UserFormMap();
		account.put("accountName", name);
		account=account.findbyFrist();
		if (account == null) {
			return true;
		} else {
			return false;
		}
	}
	
	//密码修改
	@RequestMapping("updatePassword")
	public String updatePassword(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/user/updatePassword";
	}
	
	//保存新密码
	@RequestMapping("editPassword")
	@ResponseBody
	@Transactional//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-修改密码")//凡需要处理业务逻辑的.都需要记录操作日志
	public Map<String, Object> editPassword() throws Exception{
		// 当验证都通过后，把用户信息放在session里
		UserFormMap userFormMap = getFormMap(UserFormMap.class);
		Subject user = SecurityUtils.getSubject();
		UsernamePasswordToken token = new UsernamePasswordToken(user.getPrincipal().toString(), userFormMap.getStr("oldpassword"));
		Map<String, Object> map = new HashMap<String, Object>(); 
		try {
			user.login(token);
			userFormMap.put("id", Common.findUserSessionId());
			userFormMap.put("accountName", user.getPrincipal());
			userFormMap.put("password", userFormMap.get("newpassword"));
			//这里对修改的密码进行加密
			PasswordHelper passwordHelper = new PasswordHelper();
			passwordHelper.encryptPassword(userFormMap);
			userFormMap.update();
			map.put("results", "success");
			map.put("messages", "密码修改成功!");
		} catch (LockedAccountException lae) {
			token.clear();
			map.put("results", "error");
			map.put("messages", "用户已经被锁定不能登录，请与管理员联系！");
		}  catch (AuthenticationException e) {
			token.clear();
			map.put("results", "error");
			map.put("messages", "用户或密码不正确！！");
		}
		return map;
	}
}