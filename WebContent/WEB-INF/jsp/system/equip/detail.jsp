<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<script src="${pageContext.request.contextPath}/js/jquery/jquery-1.8.3.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/system/equip/detail.js"></script>
<header class="panel-heading">
	<div class="doc-buttons">
		<input type="hidden" value="${orgType}" name="DetailInfoMap.orgName"
			id="orgName">
		<c:forEach items="${res}" var="key">
			${key.menuDesc}
		</c:forEach>
	</div>
</header>
<div class="table-responsive">
	<div id="paging" class="pagclass"></div>
</div>
