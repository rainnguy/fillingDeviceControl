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
import com.banxian.entity.UserFormBean;
import com.banxian.exception.SystemException;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.DateUtil;
import com.banxian.util.PasswordHelper;
import com.banxian.util.SysConsts;

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
		UserFormBean userFormMap = getFormMap(UserFormBean.class);
		userFormMap=toFormMap(userFormMap, pageNow, pageSize);
        pageView.setRecords(UserFormBean.mapper().findUserPage(userFormMap));//不调用默认分页,调用自已的mapper中findUserPage
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
			UserFormBean userFormMap = getFormMap(UserFormBean.class);
			PasswordHelper passwordHelper = new PasswordHelper();
			passwordHelper.encryptPassword(userFormMap);
			userFormMap.put("parentId", "0");
			userFormMap.put("orgId", Common.findAttrValue(SysConsts.STATION_ID));
			userFormMap.put("roleId", txtGroupsSelect.trim());
			userFormMap.put("tel", "123456789");
			userFormMap.put("mail", "123456@qq.com");
			userFormMap.put("delFlag", "N");
			userFormMap.put("userStat", "0");
			userFormMap.put("operCode", Common.findAttrValue(SysConsts.OPER_CODE));
			fillCommValeu(userFormMap);
			userFormMap.save();
		} catch (Exception e) {
			throw new SystemException("添加账号异常");
		}
		return "success";
	}
	
	public void fillCommValeu(Map<String, Object> targetMap){
		targetMap.put("updateTime", DateUtil.getCurrDate());
		
	}

	@ResponseBody
	@RequestMapping("deleteEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-删除用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			new UserFormBean().deleteByAttribute("id", id);
		}
		return "success";
	}

	@RequestMapping("editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara(SysConsts.USER_ID);
		if(Common.isNotEmpty(id)){
			model.addAttribute("user", new UserFormBean().findById(id));
		}
		return Common.BACKGROUND_PATH + "/system/user/edit";
	}

	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="用户管理-修改用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(String txtGroupsSelect) throws Exception {
		UserFormBean userFormMap = getFormMap(UserFormBean.class);
		userFormMap.put("roleId", txtGroupsSelect.trim());
		userFormMap.update();
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
		UserFormBean account = new UserFormBean();
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
		UserFormBean userFormMap = getFormMap(UserFormBean.class);
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