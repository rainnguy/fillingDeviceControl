<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!-- /.aside -->
<link rel="stylesheet" href="./notebook/widgets/app.v1.css" type="text/css">
<link rel="stylesheet" href="./notebook/widgets/font.css" type="text/css">

<section id="content">
	<section class="vbox">
		<header class="header bg-white b-b b-light">
			<p>
				Widgets <small>(colorful)</small>
			</p>
		</header>
		<section class="scrollable wrapper">
			<div class="col-lg-10">
				<div class="row">
					<c:forEach var="key" items="${stationFormMap}" varStatus="s">
						<div class="col-sm-6">
						<section class="panel panel-default">
							<header class="panel-heading bg-danger lt no-border">
								<div class="clearfix">
									<div class="clear">
										<div class="h3 m-t-xs m-b-xs text-white">名称:&nbsp;${key.orgName}</div>
										<div><small class="badge bg-success">编号 -- ${key.orgCode}</small></div>
									</div>
								</div>
							</header>
							<div class="list-group no-radius alt">
								<a class="list-group-item" href="#">
									<span class="badge bg-success"></span>联系电话:&nbsp;${key.orgTel}
								</a>
								<a class="list-group-item" href="/GasMonitor/gasEquip/detail.sxml">
									<span class="badge bg-success">${key.totalNum}</span>未处理的报警数:
								</a>
								<a class="list-group-item" href="/GasMonitor/gasEquip/detail.sxml">详细信息</a> 
							</div>
						</section>
					</div>
					</c:forEach>
				</div>
			</div>
		</section>
	</section>
	<a href="#" class="hide nav-off-screen-block"
		data-toggle="class:nav-off-screen, open" data-target="#nav,html"></a>
</section>