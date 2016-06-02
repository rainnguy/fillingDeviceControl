<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/date/jquery.datetimepicker.css"/>
<script src="${pageContext.request.contextPath}/js/jquery/jquery.datetimepicker.full.min.js"></script>

<script type="text/javascript">
$('#yearMonth').datetimepicker({
	lang:"ch",           //语言选择中文
    format:"Y-m-d",      //格式化日期
    timepicker:false    //关闭时间选项
});
$('#date').datetimepicker({
	lang:"ch",           //语言选择中文
    format:"Y-m-d",      //格式化日期
    timepicker:false    //关闭时间选项
});
</script>

<header class="panel-heading">
<!-- 引入 ECharts 文件 -->
<script type="text/javascript" src="${pageContext.request.contextPath}/echarts/echarts-all.js"></script>
</header>
<body>
<div class="m-b-md">
	<form class="form-inline" role="form" id="searchForm" name="searchForm">
 		<div class="form-group">
				<label class="control-label"> <span
					class="h4 font-thin v-middle">站点:</span></label>
				<select id="orgName"  class="input-large" name="graphDataMap.orgNum">
					<c:forEach items="${orgValue}" var="map">
						<option value="${map.key}">${map.value}</option>
					</c:forEach>
				</select>
		</div>
		<div class="form-group">
			<input class="input-medium ui-autocomplete-input" id="yearMonth" placeholder="选择月份" name="graphDataMap.yearMonth" >
			<input class="input-medium ui-autocomplete-input" id="date" placeholder="选择日期" name="graphDataMap.date">
		</div>
		<a href="javascript:void(0)" class="btn btn-default" id="search">查询</a>
	</form>
</div>

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
