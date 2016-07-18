<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>	
<html lang="en"
	class="app js no-touch no-android chrome no-firefox no-iemobile no-ie no-ie10 no-ie11 no-ios no-ios7 ipad">
<head>
<title>Notebook | Web Application</title>
<meta name="description"
	content="app, web app, responsive, admin dashboard, admin, flat, flat ui, ui kit, off screen nav">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/notebook/notebook_files/font.css" type="text/css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/notebook/notebook_files/app.v1.css" type="text/css">
<!--[if lt IE 9]> <script src="js/ie/html5shiv.js"></script> <script src="js/ie/respond.min.js"></script> <script src="js/ie/excanvas.js"></script> <![endif]-->
</head>
<body class="" style="">
	<section class="vbox">
		<header class="bg-dark dk header navbar navbar-fixed-top-xs">
			<div class="navbar-header aside-md">
				<a class="btn btn-link visible-xs"
					data-toggle="class:nav-off-screen,open" data-target="#nav,html">
					<i class="fa fa-bars"></i>
				</a> <a href="#" class="navbar-brand"
					data-toggle="fullscreen"><img src="./profile_files/logo.png"
					class="m-r-sm">Notebook</a> <a class="btn btn-link visible-xs"
					data-toggle="dropdown" data-target=".nav-user"> <i
					class="fa fa-cog"></i>
				</a>
			</div>
			<ul class="nav navbar-nav hidden-xs">
				<li class="dropdown"><a href="#"
					class="dropdown-toggle dker" data-toggle="dropdown"> <i
						class="fa fa-building-o"></i> <span class="font-bold">Activity</span>
				</a>
					<section
						class="dropdown-menu aside-xl on animated fadeInLeft no-borders lt">
						<div class="wrapper lter m-t-n-xs">
							<a href="#" class="thumb pull-left m-r"> <img
								src="./profile_files/avatar.jpg" class="img-circle">
							</a>
							<div class="clear">
								<a href="#"><span class="text-white font-bold">@Mike
										Mcalidek</span></a> <small class="block">Art Director</small> <a
									href="#" class="btn btn-xs btn-success m-t-xs">Upgrade</a>
							</div>
						</div>
						<div class="row m-l-none m-r-none m-b-n-xs text-center">
							<div class="col-xs-4">
								<div class="padder-v">
									<span class="m-b-xs h4 block text-white">245</span> <small
										class="text-muted">Followers</small>
								</div>
							</div>
							<div class="col-xs-4 dk">
								<div class="padder-v">
									<span class="m-b-xs h4 block text-white">55</span> <small
										class="text-muted">Likes</small>
								</div>
							</div>
							<div class="col-xs-4">
								<div class="padder-v">
									<span class="m-b-xs h4 block text-white">2,035</span> <small
										class="text-muted">Photos</small>
								</div>
							</div>
						</div>
					</section></li>
				<li>
					<div class="m-t m-l">
						<a href="price.html"
							class="dropdown-toggle btn btn-xs btn-primary" title="Upgrade"><i
							class="fa fa-long-arrow-up"></i></a>
					</div>
				</li>
			</ul>
			<ul class="nav navbar-nav navbar-right m-n hidden-xs nav-user">
				<li class="hidden-xs">
					<section class="dropdown-menu aside-xl">
						<section class="panel bg-white">
							<div class="list-group list-group-alt animated fadeInRight">
								<a href="#" class="media list-group-item"
									style="display: block;"><span
									class="pull-left thumb-sm text-center"><i
										class="fa fa-envelope-o fa-2x text-success"></i></span><span
									class="media-body block m-b-none">Sophi sent you a email<br>
									<small class="text-muted">1 minutes ago</small></span></a> <a
									href="#" class="media list-group-item"> <span
									class="pull-left thumb-sm"> <img
										src="./profile_files/avatar.jpg" alt="John said"
										class="img-circle">
								</span> <span class="media-body block m-b-none"> Use awesome
										animate.css<br> <small class="text-muted">10
											minutes ago</small>
								</span>
								</a> <a href="#" class="media list-group-item"> <span
									class="media-body block m-b-none"> 1.0 initial released<br>
										<small class="text-muted">1 hour ago</small>
								</span>
								</a>
							</div>
							<footer class="panel-footer text-sm">
								<a href="#" class="pull-right"><i
									class="fa fa-cog"></i></a> <a href="#notes"
									data-toggle="class:show animated fadeInRight">See all the
									notifications</a>
							</footer>
						</section>
					</section></li>
				<li class="dropdown hidden-xs"><a href="#"
					class="dropdown-toggle dker" data-toggle="dropdown"><i
						class="fa fa-fw fa-search"></i></a>
					<section class="dropdown-menu aside-xl animated fadeInUp">
						<section class="panel bg-white">
							<form role="search"> 
								<div class="form-group wrapper m-b-none">
									<div class="input-group">
										<input type="text" class="form-control" placeholder="Search">
										<span class="input-group-btn">
											<button type="submit" class="btn btn-info btn-icon">
												<i class="fa fa-search"></i>
											</button>
										</span>
									</div>
								</div>
							</form>
						</section>
					</section></li>
				<li class="dropdown"><a href="#"
					class="dropdown-toggle" data-toggle="dropdown"> <span
						class="thumb-sm avatar pull-left"> <img
							src="./profile_files/avatar.jpg">
					</span> John.Smit <b class="caret"></b>
				</a>
					<ul class="dropdown-menu animated fadeInRight">
						<span class="arrow top"></span>
						<li><a href="#">Settings</a></li>
						<li><a href="">Profile</a></li>
						<li><a href="#"> <span
								class="badge bg-danger pull-right">3</span> Notifications
						</a></li>
						<li><a href="docs.html">Help</a></li>
						<li class="divider"></li>
						<li><a href="modal.lockme.html" data-toggle="ajaxModal">Logout</a>
						</li>
					</ul></li>
			</ul>
		</header>
		<section>
			<section class="hbox stretch">
				<!-- /.aside -->
				<section id="content">
					<section class="vbox">
						<header class="header bg-white b-b b-light">
							<p class="h3 m-t-xs m-b-xs">${stationName}</p>
							<small>&nbsp;&nbsp;编号-${orgCode}</small>
						</header>
						<section class="scrollable">
							<section class="hbox stretch">
								<aside class="aside-lg bg-light lter b-r">
									<section class="vbox">
										<section class="scrollable">
											<div class="wrapper">
												<div class="clearfix m-b">
													<div class="clear">
														<div class="h3 m-t-xs m-b-xs"><i class="fa fa-hand-o-right">&nbsp;</i>登录名</div>
														<small class="text-muted"><i
															class="fa fa-map-marker"></i> China, CN</small>
													</div>
												</div>
												<div class="panel wrapper panel-success">
													<div class="row">
														<div class="col-xs-4">
															<a href="#"> <span
																class="m-b-xs h4 block">245</span> <small
																class="text-muted">报警数</small>
															</a>
														</div>
													</div>
												</div>
												<div>
													<small class="text-uc text-xs text-muted">站级信息</small>
													<p>详细</p>
													<small class="text-uc text-xs text-muted">info</small>
													<p>Lorem ipsum dolor sit amet, consectetur adipiscing
														elit. Morbi id neque quam. Aliquam sollicitudin venenatis
														ipsum ac feugiat.</p>
													<div class="line"></div>
													<small class="text-uc text-xs text-muted">connection</small>
												</div>
											</div>
										</section>
									</section>
								</aside>
								<aside class="bg-white">
									<section class="vbox">
										<header class="header bg-light bg-gradient">
											<ul class="nav nav-tabs nav-white">
												<li class="active"><a href="#activity"
													data-toggle="tab">详细信息</a></li>
												<li class="events"><a href="#events"
													data-toggle="tab">未处理报警信息</a></li>
											</ul>
										</header>
										<section class="scrollable">
											<div class="tab-content">
												<div class="tab-pane events" id="events">
													<section class="panel panel-default">
													<c:forEach items="${unsolvedAlarmData}" var="key" varStatus="ind">
														<table class="table table-striped m-b-none">
													   		<c:if test="${ind.index eq 0}">
													   			<thead>
																	<tr>
																		<th >报警时间</th>
																		<th >报警级别</th>
																		<th >报警设备</th>
																		<th >报警内容</th>
																		<th >处理操作</th>
																	</tr>
																</thead>
													   		</c:if>
															<tbody >
																<tr >
																	<td style="display:none">${key.id}</td>
																	<td style="width: 20%">${key.alarmTime}</td>
																	<td style="width: 10%">${key.alarmLevel}</td>
																	<td style="width: 15%">${key.deviceName}</td>
																	<td style="width: 25%">${key.alarmContent}</td>
																	<td style="width: 10%"><input type="button" value="确认"/></td>
																</tr>
															</tbody>
														</table>  
													</c:forEach>
													</section>
												</div>
												<div class="tab-pane active" id="activity">
													<div class="col-sm-6">
														<section class="panel panel-default">
															<c:forEach items="${equipData}" var="key" varStatus="ind">
																<c:choose> 
																	<c:when test="${key.deviceType == '01'}">   
																		<table class="table table-striped m-b-none">
																			<c:if test="${ind.index eq 0}">
																				<thead>
																					<tr>
																						<th >设备名称</th>
																						<th >压力</th>
																						<th >液位</th>
																						<th >LNG差压</th>
																						<th >LNG高度</th>
																						<th >LNG重量</th>
																					</tr>
																				</thead>
																			</c:if>
																			<tbody >
																				<tr >
																					<td style="width: 25%">${key.deviceName}</td>
																					<td style="width: 15%">${key.storagePressure}</td>
																					<td style="width: 15%">${key.lngLiquidPosition}</td>
																					<td style="width: 15%">${key.differentialPressure}</td>
																					<td style="width: 15%">${key.lngHeight}</td>
																					<td style="width: 15%">${key.lngWeight}</td>
																				</tr>
																			</tbody>
																		</table>  
																  	</c:when> 
																</c:choose>
															</c:forEach>
														</section>
													</div>
													<div class="col-sm-6">
														<c:set value="01" var="tempKey" />
														<section class="panel panel-default">
															<c:forEach items="${equipData}" var="key" varStatus="ind">
																<c:choose> 
																	<c:when test="${key.deviceType == '02'}">   
																		<table class="table table-striped m-b-none">
																		<c:if test="${tempKey eq '01'}">
																			<thead>
																				<tr >
																					<th >设备名称</th>
																					<th >泵前压力</th>
																					<th >泵后压力</th>
																					<th >泵池温度</th>
																					<th >变频器频率</th>
																					<th >变频器电流</th>
																				</tr>
																			</thead>
																		</c:if>
																			<tbody>
																				<tr >
																					<td style="width: 20%">${key.deviceName}</td>
																					<td style="width: 15%">${key.beforePressure}</td>
																					<td style="width: 15%">${key.affterPressure}</td>
																					<td style="width: 15%">${key.pumpTemp}</td>
																					<td style="width: 18%">${key.converterFrequency}</td>
																					<td style="width: 17%">${key.converterCurrent}</td>
																				</tr>
																			</tbody>
																		</table> 
																		<c:set value="02" var="tempKey"/> 
																  	</c:when> 
																</c:choose>
															</c:forEach>
														</section>
													</div>
												</div>
											</div>
										</section>
									</section>
								</aside>
							</section>
						</section>
					</section>
					<a href="#" class="hide nav-off-screen-block"
						data-toggle="class:nav-off-screen, open" data-target="#nav,html"></a>
				</section>
				<aside class="bg-light lter b-l aside-md hide" id="notes">
					<div class="wrapper">Notification</div>
				</aside>
			</section>
		</section>
	</section>
	<!-- Bootstrap -->
	<!-- App -->
	<script src="${pageContext.request.contextPath}/notebook/notebook_files/app.v1.js"></script>
	<script src="${pageContext.request.contextPath}/notebook/notebook_files/app.plugin.js"></script>
</body>
</html>