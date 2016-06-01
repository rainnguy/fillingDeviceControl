<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>	
<script type="text/javascript" src="${pageContext.request.contextPath}/js/system/equip/historyAlarm.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/date/jquery.datetimepicker.css"/>
<script src="${pageContext.request.contextPath}/js/jquery/jquery.datetimepicker.full.min.js"></script>
<style type="text/css">
.input::-ms-input-placeholder {
	text-align: center;
}
.input::-webkit-input-placeholder {
	text-align: center;
}
</style>
<script type="text/javascript">
$('#startDate').datetimepicker();
$('#endDate').datetimepicker();
</script>
	<div class="m-b-md">
		<form class="form-inline" role="form" id="alarmForm" name="searchForm">
			<div class="form-group">
				<label class="control-label"> <span
					class="h4 font-thin v-middle">站点名称:</span></label>
				<input class="input-medium ui-autocomplete-input" id="orgName" name="alarmInfoMap.orgName">
			</div>
			<div class="form-group">
				<input class="input-medium ui-autocomplete-input" id="startDate" placeholder="开始时间" name="alarmInfoMap.startDate" >
				<input class="input-medium ui-autocomplete-input" id="endDate" placeholder="结束时间" name="alarmInfoMap.endDate">
			</div>
			<a href="javascript:void(0)" class="btn btn-default" id="search">查询</a>
			<a href="javascript:grid.exportData('/gasAlarm/export.sxml')" class="btn btn-info" id="export">导出excel</a>
		</form>
	</div>
	<header class="panel-heading">
	<div class="doc-buttons">
	<input type="hidden" value="${orgType}" name="alarmInfoMap.orgType" id="orgType">
		<c:forEach items="${res}" var="key">
			${key.menuDesc}
		</c:forEach>
	</div>
	</header>
	<div class="table-responsive">
		<div id="paging" class="pagclass"></div>
	</div>
