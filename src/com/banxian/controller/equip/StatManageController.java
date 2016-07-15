package com.banxian.controller.equip;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.banxian.annotation.SystemLog;
import com.banxian.controller.index.BaseController;
import com.banxian.entity.StationFormBean;
import com.banxian.exception.SystemException;
import com.banxian.mapper.equip.StationMapper;
import com.banxian.plugin.PageView;
import com.banxian.util.Common;
import com.banxian.util.DateUtil;
import com.banxian.util.JsonUtils;
import com.banxian.util.POIUtils;
import com.banxian.util.SysConsts;

/**
 * 
 * @author _wsq 2016-03-10
 * @version 2.0v
 */
@Controller
@RequestMapping("/staManage/")
public class StatManageController extends BaseController {

	@Inject
	private StationMapper staMapper;
	
	@RequestMapping("list")
	public String listUI(Model model) throws Exception {
		model.addAttribute("res", findByRes());
		return Common.BACKGROUND_PATH + "/system/equip/stationList";
	}

	/**
	 * 显示站点信息
	 * 
	 * @param pageNow
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findStationList")
	public PageView findStationList( String pageNow,
			String pageSize) throws Exception {
		StationFormBean staFormMap = getFormMap(StationFormBean.class);
		// 用户权限
		staFormMap.put(SysConsts.ROLE_KEY, Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		staFormMap.put(SysConsts.ORG_CODE, Common.findAttrValue(SysConsts.ORG_CODE));
				
		staFormMap=toFormMap(staFormMap, pageNow, pageSize);
        pageView.setRecords(StationFormBean.mapper().findStationList(staFormMap));
        return pageView;
	}

	/**
	 * 导出站点管理信息
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/export")
	public void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fileName = "站点信息_" + DateUtil.getCurrDate4();
		StationFormBean stationFormMap = findHasHMap(StationFormBean.class);
		// 用户权限
		stationFormMap.put(SysConsts.ROLE_KEY, Common.findAttrValue(SysConsts.ROLE_KEY));
		// 用户所属站的编号
		stationFormMap.put(SysConsts.ORG_CODE, Common.findAttrValue(SysConsts.ORG_CODE));
				
		String exportData = stationFormMap.getStr("exportData");// 列表头的json字符串

		List<Map<String, Object>> listMap = JsonUtils.parseJSONList(exportData);

		List<StationFormBean> list = staMapper.findStationList(stationFormMap);
		POIUtils.exportToExcel(response, listMap, list, fileName);
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	@RequestMapping("addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/equip/add";
	}

	@ResponseBody
	@RequestMapping("addEntity")
	@SystemLog(module="系统管理",methods="用户管理-新增用户")//凡需要处理业务逻辑的.都需要记录操作日志
	@Transactional(readOnly=false)
	public String addEntity(String txtGroupsSelect){
		try {
			StationFormBean staFormMap = getFormMap(StationFormBean.class);
			staFormMap.put("orgCode", this.getMaxStationCode());
			fillCommValeu(staFormMap);
			staFormMap.save();
		} catch (Exception e) {
			throw new SystemException("添加账号异常");
		}
		return "success";
	}
	
	public int getMaxStationCode(){
		int stationCode = staMapper.getOrgMaxCode();
		
		return stationCode;
	}
	
	public void fillCommValeu(Map<String, Object> targetMap){
		targetMap.put("operCode", Common.findAttrValue(SysConsts.OPER_CODE));
		targetMap.put("updateTime", DateUtil.getCurrDate());
		
	}

	@ResponseBody
	@RequestMapping("deleteEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="系统管理",methods="站点管理-删除站点")
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			new StationFormBean().deleteByAttribute("id", id);
		}
		return "success";
	}
	
	@RequestMapping("editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara(SysConsts.STATION_ID);
		if(Common.isNotEmpty(id)){
			model.addAttribute("station", new StationFormBean().findById(id));
		}
		return Common.BACKGROUND_PATH + "/system/equip/edit";
	}

	@ResponseBody
	@RequestMapping("editEntity")
	@Transactional(readOnly=false)
	@SystemLog(module="系统管理",methods="用户管理-修改用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String editEntity(String txtGroupsSelect) throws Exception {
		StationFormBean staFormMap = getFormMap(StationFormBean.class);
		staFormMap.put("roleId", txtGroupsSelect.trim());
		staFormMap.update();
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
		StationFormBean station = new StationFormBean();
		station.put("orgName", name);
		station=station.findbyFrist();
		if (station == null) {
			return true;
		} else {
			return false;
		}
	}
}