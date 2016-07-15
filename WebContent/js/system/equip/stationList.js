var pageii = null;
var grid = null;
$(function() {
	grid = lyGrid({
		pagId : 'paging',
		l_column : [ {
			colkey : "id",
			name : "id",
			width : "50px",
			hide : true
		}, {
			colkey : "orgCode",
			name : "站点编号"
		}, {
			colkey : "orgName",
			name : "站点名称"
		}, {
			colkey : "orgType",
			name : "站点类型",
		}, {
			colkey : "orgTel",
			name : "联系电话"
		}, {
			colkey : "orgAddr",
			name : "地址"
		} ],
		jsonUrl : rootPath + '/staManage/findStationList.sxml',
		checkbox : true,
		serNumber : true
	});
	$("#search").click("click", function() {// 绑定查询按扭
		var searchParams = $("#searchForm").serializeJson();// 初始化传参数
		grid.setOptions({
			data : searchParams
		});
	});
	$("#addStation").click("click", function() {
		addStation();
	});
	$("#editStation").click("click", function() {
		editStation();
	});
	$("#delStation").click("click", function() {
		delStation();
	});
});

	function editStation() {
		var cbox = grid.getSelectedCheckbox();
		if (cbox.length > 1 || cbox == "") {
			layer.msg("只能选中一个");
			return;
		}
		pageii = layer.open({
			title : "编辑",
			type : 2,
			area : [ "600px", "80%" ],
			content : rootPath + '/staManage/editUI.sxml?orgId=' + cbox
		});
	}
	
	function addStation() {
		pageii = layer.open({
			title : "新增",
			type : 2,
			area : [ "600px", "80%" ],
			content : rootPath + '/staManage/addUI.sxml'
		});
	}
	function delStation() {
		var cbox = grid.getSelectedCheckbox();
		if (cbox == "") {
			layer.msg("请选择删除项！！");
			return;
		}
		layer.confirm('是否删除？', function(index) {
			var url = rootPath + '/staManage/deleteEntity.sxml';
			var s = CommnUtil.ajax(url, {
				ids : cbox.join(",")
			}, "json");
			if (s == "success") {
				layer.msg('删除成功');
				grid.loadData();
			} else {
				layer.msg('删除失败');
			}
		});
	}
	