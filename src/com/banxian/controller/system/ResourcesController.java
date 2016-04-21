package com.banxian.controller.system;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.annotation.SystemLog;
import com.banxian.controller.index.BaseController;
import com.banxian.entity.ButtonFormBean;
import com.banxian.entity.MenuFormBean;
import com.banxian.entity.Params;
import com.banxian.mapper.SysMenuMapper;
import com.banxian.util.Common;
import com.banxian.util.TreeObject;
import com.banxian.util.TreeUtil;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/resources/")
public class ResourcesController extends BaseController {
	
	@Inject
	private SysMenuMapper sysMenuMapper;
	
	/**
	 * @param model
	 *            存放返回界面的model
	 * @return
	 */
	@ResponseBody
	@RequestMapping("treelists")
	public MenuFormBean findByPage(Model model) {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		String order = " order by sortNo asc";
		resFormMap.put("$orderby", order);
		List<MenuFormBean> mps = resFormMap.findByNames();
		List<TreeObject> list = new ArrayList<TreeObject>();
		for (MenuFormBean map : mps) {
			TreeObject ts = new TreeObject();
			Common.flushObject(ts, map);
			list.add(ts);
		}
		TreeUtil treeUtil = new TreeUtil();
		List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
		resFormMap = new MenuFormBean();
		resFormMap.put("treelists", ns);
		return resFormMap;
	}

	@ResponseBody
	@RequestMapping("reslists")
	public List<TreeObject> reslists(Model model) throws Exception {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		List<MenuFormBean> mps = resFormMap.findByWhere();
		List<TreeObject> list = new ArrayList<TreeObject>();
		for (MenuFormBean map : mps) {
			TreeObject ts = new TreeObject();
			Common.flushObject(ts, map);
			list.add(ts);
		}
		TreeUtil treeUtil = new TreeUtil();
		List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0, "　");
		return ns;
	}

	/**
	 * @param model
	 *            存放返回界面的model
	 * @return
	 */
	@RequestMapping("list")
	public String list(Model model) {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/resources/list";
	}

	/**
	 * 跳转到修改界面
	 * 
	 * @param model
	 * @param resourcesId
	 *            修改菜单信息ID
	 * @return
	 */
	@RequestMapping("editUI")
	public String editUI(Model model) {
		String id = getPara("id");
		if(Common.isNotEmpty(id)){
			MenuFormBean formMap=new MenuFormBean();
			model.addAttribute("resources", formMap.findById(id));
		}
		return Common.BACKGROUND_PATH + "/system/resources/edit";
	}

	/**
	 * 跳转到新增界面
	 * 
	 * @return
	 */
	@RequestMapping("addUI")
	public String addUI(Model model) {
		return Common.BACKGROUND_PATH + "/system/resources/add";
	}

	/**
	 * 权限分配页面
	 * 
	 * @author _wsq 2016-03-10
	 * @version 2.0v
	 * @return
	 */
	@RequestMapping("permissions")
	public String permissions(Model model) {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		List<MenuFormBean> mps = resFormMap.findByWhere();
		List<TreeObject> list = new ArrayList<TreeObject>();
		for (MenuFormBean map : mps) {
			TreeObject ts = new TreeObject();
			Common.flushObject(ts, map);
			list.add(ts);
		}
		TreeUtil treeUtil = new TreeUtil();
		List<TreeObject> ns = treeUtil.getChildTreeObjects(list, 0);
		model.addAttribute("permissions", ns);
		return Common.BACKGROUND_PATH + "/system/resources/permissions";
	}

	/**
	 * 添加菜单
	 * 
	 * @param resources
	 * @return Map
	 * @throws Exception
	 */
	@RequestMapping("addEntity")
	@ResponseBody
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="资源管理-新增资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String addEntity() throws Exception {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		if("2".equals(resFormMap.get("menuType"))){
			resFormMap.put("menuDesc", Common.htmltoString(resFormMap.get("menuDesc")+""));
		}
		Object o = resFormMap.get("menuHide");
		if(null==o){
			resFormMap.put("menuHide", "0");
		}
		
		resFormMap.save();
		return "success";
	}

	/**
	 * 更新菜单
	 * 
	 * @param model
	 * @param Map
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	@SystemLog(module="系统管理",methods="资源管理-修改资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(Model model) throws Exception {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		if("2".equals(resFormMap.get("menuType"))){
			resFormMap.put("menuDesc", Common.htmltoString(resFormMap.get("menuDesc")+""));
		}
		Object o = resFormMap.get("menuHide");
		if(null==o){
			resFormMap.put("menuHide", "0");
		}
		resFormMap.update();
		return "success";
	}

	/**
	 * 根据ID删除菜单
	 * 
	 * @param model
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("deleteEntity")
	@SystemLog(module="系统管理",methods="资源管理-删除资源")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity(Model model) throws Exception {
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			MenuFormBean resFormMap = new MenuFormBean();
			resFormMap.deleteById(id);
		};
		return "success";
	}


	@RequestMapping("sortUpdate")
	@ResponseBody
	@Transactional(readOnly=false)//需要事务操作必须加入此注解
	public String sortUpdate(Params params) throws Exception {
		List<String> ids = params.getId();
		List<String> es = params.getRowId();
		List<MenuFormBean> maps = new ArrayList<MenuFormBean>();
		MenuFormBean map = null;
		for (int i = 0; i < ids.size(); i++) {
			 map = new MenuFormBean();
			map.put("id", ids.get(i));
			map.put("level", es.get(i));
			maps.add(map);
		}
		MenuFormBean.mapper().updateSortOrder(maps);
		return "success";
	}

	@ResponseBody
	@RequestMapping("findRes")
	public List<MenuFormBean> findUserRes() {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		List<MenuFormBean> rs = MenuFormBean.mapper().findRes(resFormMap);
		return rs;
	}
//	@ResponseBody
//	@RequestMapping("addUserRes")
//	@Transactional(readOnly=false)//需要事务操作必须加入此注解
//	@SystemLog(module="系统管理",methods="用户管理/组管理-修改权限")//凡需要处理业务逻辑的.都需要记录操作日志
//	public String addUserRes() throws Exception {
//		String userId = "";
//		String u = getPara("userId");
//		String g = getPara("roleId");
//		if (null != u && !Common.isEmpty(u.toString())) {
//			userId = u.toString();
//		} else if (null != g && !Common.isEmpty(g.toString())) {
//			List<UserGroupsFormMap> gs =new UserGroupsFormMap().findByAttribute("roleId", g.toString());
//			for (UserGroupsFormMap ug : gs) {
//				userId += ug.get("userId") + ",";
//			}
//		}
//		userId = Common.trimComma(userId);
//		String[] users = userId.split(",");
//		RoleFuncFormBean resUserFormMap = new RoleFuncFormBean();
//		for (String uid : users) {
//			resUserFormMap.deleteByAttribute("userId", uid);
//			String[] s = getParaValues("resId[]");
//			List<RoleFuncFormBean> resUserFormMaps = new ArrayList<RoleFuncFormBean>();
//			for (String rid : s) {
//			    resUserFormMap = new RoleFuncFormBean();
//				resUserFormMap.put("resId", rid);
//				resUserFormMap.put("userId", uid);
//				resUserFormMaps.add(resUserFormMap);
//			
//			}
//			resUserFormMap.batchSave(resUserFormMaps);
//		}
//		return "success";
//	}

	/**
	 * 验证菜单是否存在
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("isExist")
	@ResponseBody
	public boolean isExist(String name,String resKey) {
		MenuFormBean resFormMap = getFormMap(MenuFormBean.class);
		List<MenuFormBean> r = resFormMap.findByNames();
		if (r.size()==0) {
			return true;
		} else {
			return false;
		}
	}
	
	@ResponseBody
	@RequestMapping("findByButtom")
	public List<ButtonFormBean> findByButtom(){
		return sysMenuMapper.findByWhere(new ButtonFormBean());
	}
	
}