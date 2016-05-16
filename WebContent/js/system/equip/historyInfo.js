var grid = null;
var orgType = $("#orgType").val();
//alert("test = " + orgType);

$(function() {
	if (orgType == '3001') {
		grid = lyGrid({
			pagId : 'paging',
			l_column : [ {
				colkey : "orgCode",
				name : "站点编号"
			}, {
				colkey : "orgName",
				name : "站点名称"
			}, {
				colkey : "currTime",
				name : "时间",
				renderData : function(rowindex, data, rowdata, column) {
//					return new Date(data).format("yyyy-MM-dd hh:mm:ss");
					return data;
				}
			}, {
				colkey : "deviceName",
				name : "设备名称"
			}, {
				colkey : "storagePressure",
				name : "储罐压力",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "lngLiquidPosition",
				name : "LNG液位",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "differentialPressure",
				name : "LNG差压",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "lngHeight",
				name : "LNG高度",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "lngWeight",
				name : "LNG重量",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "cbtBeforePressure",
				name : "汽化器后压力",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "cbtAffterTemp",
				name : "汽化器后温度",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "flowmeterTemp",
				name : "流量计温度",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "flowmeterInstFlow",
				name : "流量计瞬时流量",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "flowmeterPressure",
				name : "流量计压力",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "flowmeterTotalFlow",
				name : "流量计总流量",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "meterPressure",
				name : "仪表风压力"
			}, {
				colkey : "ambientTemp",
				name : "环境温度"
			} ],
			jsonUrl : rootPath + '/gasEquip/findHistoryData.sxml',
			// checkbox : true,
			serNumber : true
		});
	} else {
		grid = lyGrid({
			pagId : 'paging',
			l_column : [ {
				colkey : "orgCode",
				name : "站点编号",
			}, {
				colkey : "orgName",
				name : "站点名称"
			}, {
				colkey : "currTime",
				name : "时间",
				renderData : function(rowindex, data, rowdata, column) {
//					return new Date(data).format("yyyy-MM-dd hh:mm:ss");
					return data;
				}
			}, {
				colkey : "deviceName",
				name : "设备名称"
			}, {
				colkey : "storagePressure",
				name : "储罐压力",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "lngLiquidPosition",
				name : "LNG液位",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "differentialPressure",
				name : "LNG差压",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "lngHeight",
				name : "LNG高度",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "lngWeight",
				name : "LNG重量",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "beforePressure",
				name : "泵前压力",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "affterPressure",
				name : "泵后压力",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "pumpTemp",
				name : "泵池温度",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "converterFrequency",
				name : "变频器频率",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "converterCurrent",
				name : "变频器电流",
				renderData : function(rowindex, data, rowdata, colkeyn) {
					if (data == "") {
						return "-";
					} else {
						return data;
					}
				}
			}, {
				colkey : "meterPressure",
				name : "仪表风压力"
			}, {
				colkey : "ambientTemp",
				name : "环境温度"
			} ],
			jsonUrl : rootPath + '/gasEquip/findHistoryData.sxml',
			// checkbox : true,
			serNumber : true
		});
	}
	$("#search").click("click", function() {// 绑定查询按扭
		var searchParams = $("#searchForm").serializeJson();// 初始化传参数
		grid.setOptions({
			data : searchParams
		});
	});
});
