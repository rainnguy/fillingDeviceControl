var grid = null;
$(function() {
		grid = lyGrid({
			pagId : 'paging',
			l_column : [ {
				colkey : "orgCode",
				name : "站点编号"
			}, {
				colkey : "orgName",
				name : "站点名称"
			}, {
				colkey : "alarmTime",
				name : "报警时间",
				renderData : function(rowindex, data, rowdata, column) {
					return new Date(data).format("yyyy-MM-dd hh:mm:ss");
				}
			}, {
				colkey : "alarmLevel",
				name : "报警级别"
			}, {
				colkey : "deviceName",
				name : "报警设备",
			}, {
				colkey : "alarmContent",
				name : "报警内容",
			}, {
				colkey : "statusName",
				name : "处理状态"
			}, {
				colkey : "handlerCode",
				name : "处理人ID",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "handlerName",
				name : "处理人",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "handleTime",
				name : "处理时间",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return new Date(data).format("yyyy-MM-dd hh:mm:ss");
					}
				}
			} ],
		jsonUrl : rootPath + '/gasAlarm/findHistoryAlarmData.sxml',
		// checkbox : true,
		serNumber : true
	});
	$("#search").click("click", function() {// 绑定查询按扭
		var searchParams = $("#alarmForm").serializeJson();// 初始化传参数
		grid.setOptions({
			data : searchParams
		});
	});
});
