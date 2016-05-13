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

	@ResponseBody
	@RequestMapping("findStationList")
	public PageView findStationList( String pageNow,
			String pageSize) throws Exception {
		StationFormBean staFormMap = getFormMap(StationFormBean.class);
		staFormMap=toFormMap(staFormMap, pageNow, pageSize);
        pageView.setRecords(StationFormBean.mapper().findStationList(staFormMap));
        return pageView;
	}

	////////////////////////////////////////////////////////////////////////////
	
	@RequestMapping("addUI")
	public String addUI(Model model) throws Exception {
		return Common.BACKGROUND_PATH + "/system/equip/StationAdd";
	}

	@ResponseBody
	@RequestMapping("addEntity")
	@SystemLog(module="系统管理",methods="用户管理-新增用户")//凡需要处理业务逻辑的.都需要记录操作日志
	@Transactional(readOnly=false)
	public String addEntity(String txtGroupsSelect){
		try {
			StationFormBean staFormMap = getFormMap(StationFormBean.class);
			staFormMap.put("parentId", "0");
			staFormMap.put("orgId", Common.findAttrValue(SysConsts.STATION_ID));
			staFormMap.put("roleId", txtGroupsSelect.trim());
			staFormMap.put("tel", "123456789");
			staFormMap.put("mail", "123456@qq.com");
			staFormMap.put("delFlag", "N");
			staFormMap.put("userStat", "0");
			staFormMap.put("operCode", Common.findAttrValue(SysConsts.OPER_CODE));
			fillCommValeu(staFormMap);
			staFormMap.save();
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
	@Transactional(readOnly=false)
	@SystemLog(module="系统管理",methods="用户管理-删除用户")//凡需要处理业务逻辑的.都需要记录操作日志
	public String deleteEntity() throws Exception {
		String[] ids = getParaValues("ids");
		for (String id : ids) {
			new StationFormBean().deleteByAttribute("id", id);
		}
		return "success";
	}
	
	@RequestMapping("/export")
	public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fileName = "用户列表";
		StationFormBean stationFormMap = findHasHMap(StationFormBean.class);
		//exportData = 
		// [{"colkey":"sql_info","name":"SQL语句","hide":false},
		// {"colkey":"total_time","name":"总响应时长","hide":false},
		// {"colkey":"avg_time","name":"平均响应时长","hide":false},
		// {"colkey":"record_time","name":"记录时间","hide":false},
		// {"colkey":"call_count","name":"请求次数","hide":false}
		// ]
		String exportData = stationFormMap.getStr("exportData");// 列表头的json字符串

		List<Map<String, Object>> listMap = JsonUtils.parseJSONList(exportData);

		List<StationFormBean> lis = staMapper.findStationList(stationFormMap);
		POIUtils.exportToExcel(response, listMap, lis, fileName);
	}

	@RequestMapping("editUI")
	public String editUI(Model model) throws Exception {
		String id = getPara(SysConsts.STATION_ID);
		if(Common.isNotEmpty(id)){
			model.addAttribute("orgId", new StationFormBean().findById(id));
		}
		return Common.BACKGROUND_PATH + "/system/equip/StationEdit";
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