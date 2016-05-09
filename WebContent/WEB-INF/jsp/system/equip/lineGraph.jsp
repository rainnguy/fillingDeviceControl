<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<header class="panel-heading">
<!-- 引入 ECharts 文件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/echarts/echarts-all.js"></script>
</header>
<body>
<!-- <div class="m-b-md"> -->
<!-- 	<form class="form-inline" role="form" id="searchForm" name="searchForm"> -->
<!-- 		<div class="form-group"> -->
<!-- 			<label class="control-label"> -->
<!-- 				<span class="h4 font-thin v-middle">站点:</span> -->
<!-- 			</label> -->
<!-- 			<input class="input-medium ui-autocomplete-input" id="orgName" name="GraphDataMap.orgName"> -->
<!-- 			<label class="control-label"> -->
<!-- 				<span class="h4 font-thin v-middle">年:</span> -->
<!-- 			</label> -->
<!-- 			<input class="input-medium ui-autocomplete-input" id="year" name="GraphDataMap.year"> -->
<!-- 			<label class="control-label"> -->
<!-- 				<span class="h4 font-thin v-middle">月:</span> -->
<!-- 			</label> -->
<!-- 			<input class="input-medium ui-autocomplete-input" id="month" name="GraphDataMap.month"> -->
<!-- 			<label class="control-label"> -->
<!-- 				<span class="h4 font-thin v-middle">日:</span> -->
<!-- 			</label> -->
<!-- 			<input class="input-medium ui-autocomplete-input" id="day" name="GraphDataMap.day">  -->
<!-- 		</div> -->
<!-- 		<a href="javascript:void(0)" class="btn btn-default" id="search">查询</a> -->
<!-- 	</form> -->
<!-- </div> -->

<input type="hidden" name="EchartsBean.id" value="${graphLegend}" id="graphLegend">
<input type="hidden" name="EchartsBean.id" value="${graphAxis}" id="graphAxis">
<input type="hidden" name="EchartsBean.id" value="${graphSeries}" id="graphSeries">
<div class="table-responsive">
	<div id="paging" class="pagclass"></div>
</div>

<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
<div id="main" style="width: 1000px; height: 650px;"></div>
<script type="text/javascript"  src="${pageContext.request.contextPath}/js/system/equip/lineGraph.js"></script>
</body>
