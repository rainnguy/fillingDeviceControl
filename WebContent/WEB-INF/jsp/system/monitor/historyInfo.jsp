<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>	
<script type="text/javascript" src="${pageContext.request.contextPath}/js/system/monitor/historyInfo.js"></script>
	<div class="m-b-md">
		<form class="form-inline" role="form" id="searchForm" name="searchForm">
			<div class="form-group">
				<label class="control-label"> <span
					class="h4 font-thin v-middle">站点名称:</span></label>
				<input class="input-medium ui-autocomplete-input" id="orgName"
					name="DeviceInfoBean.orgName">
				<label class="control-label"> <span
					class="h4 font-thin v-middle">设备名称:</span></label>
				<input class="input-medium ui-autocomplete-input" id="deviceName"
					name="DeviceInfoBean.deviceName">
			</div>
			<a href="javascript:void(0)" class="btn btn-default" id="search">查询</a>
			<a href="javascript:grid.exportData('/monitor/historyInfo.sxml')" class="btn btn-info" id="export">导出excel</a>
		</form>
	</div>
	<header class="panel-heading">
	<div class="doc-buttons">
		<c:forEach items="${res}" var="key">
			${key.menuDesc}
		</c:forEach>
	</div>
	</header>
	<div class="table-responsive">
		<div id="paging" class="pagclass"></div>
	</div>
	
	<div class="table-responsive">
		<div id="paging2" class="pagclass"></div>
	</div>
