var date = '';
$(function() {
	// 基于准备好的dom，初始化echarts实例
	var myChart = echarts.init(document.getElementById('main'));
	var xname = '';
	if (date != null && date != "") {
		xname = '小时';
	} else {
		xname = '日';
	}
	// 指定图表的配置项和数据
	var option = {
		title : {
			text : '液位数据图'
		},
		tooltip : {
			show : true,
			trigger : 'axis'
		},
		legend : {
			data : []
		},
		grid : {
			left : '3%',
			right : '4%',
			bottom : '3%',
			containLabel : true
		},
		toolbox : {
			feature : {
				saveAsImage : {}
			}
		},
		xAxis : {
			type : 'category',
			boundaryGap : false,
			name : xname,
			axisLabel : {
				rotate : 30
			},
			data : []
		},
		yAxis : {
			type : 'value',
			name : '液位值'
		},
		series : []
	};

	// 采用ajax异步请求数据
	$.ajax({
		type : 'post',
		url : rootPath + '/gasReport/findGraphData.sxml',
		dataType : 'json',
		success : function(result) {
			if (result) {
				// 将返回的category和series对象赋值给options对象内的category和series
				option.xAxis.data = result.axis;
				option.legend.data = result.legend;
				var series_arr = result.series;
				for (var i = 0; i < series_arr.length; i++) {
					option.series[i] = result.series[i];
				}
				// 使用刚指定的配置项和数据显示图表。
				myChart.setOption(option);
			}
		},
		error : function(errMsg) {
			console.error("加载数据失败");
		}
	});

});
