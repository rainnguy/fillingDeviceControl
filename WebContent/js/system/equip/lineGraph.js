//var yearMonth = $("#year").val() + $("#month").val();
//var date = $("#year").val() + $("#month").val() + $("#day").val();
var date='';
var graphLegend = $("#graphLegend").val();
var graphAxis = $("#graphAxis").val();
var graphSeries = $("#graphSeries").val();

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
		series : [{
            name:'储罐#1',
            type:'line',
//	            stack: '总量',
            data:[120, 202, 101, 134, 90, 230, 210,120, 202, 101, 134, 90, 230, 210,120, 202, 101, 134, 90, 230, 210,120, 202, 101, 134, 90, 230, 210,99,23,66]
        },
        {
            name:'储罐#2',
            type:'line',
//	            stack: '总量',
            data:[220, 182, 191, 234, 290,216, 25,220, 182, 191, 234, 290,216, 25,220, 182, 191, 234, 290,216, 25,220, 182, 191, 234, 290,216, 25,41,54,65]
        }]
	};

	// 设定要显示横坐标的值
	graphAxis = graphAxis.substring(1);
	graphAxis = graphAxis.substring(0, graphAxis.length - 1);
	graphAxis = graphAxis.replace(" ", "");
	var axisList = graphAxis.split(",");
	var resultAxis = new Array();
	for (var i = 0; i < axisList.length; i++) {
		resultAxis[i] = axisList[i];
	}
	option.xAxis.data = resultAxis;
//	option.xAxis.data = graphAxis;

	// 设定要显示的legend值
	graphLegend = graphLegend.substring(1);
	graphLegend = graphLegend.substring(0, graphLegend.length - 1);
	graphLegend = graphLegend.replace(" ", "");
	var legendList = graphLegend.split(",");
	var resultLegend = new Array();
	for (var i = 0; i < legendList.length; i++) {
		resultLegend[i] = legendList[i];
	}
	option.legend.data = resultLegend;
//	option.legend.data = graphLegend;

	// 设定所有纵坐标的值
//	alert(graphSeries.length);
//	for (var i = 0; i < graphSeries.length; i++) {
//		option.series[i] = graphSeries[i];
//	}
	
	// 使用刚指定的配置项和数据显示图表。
	// myChart.hideLoading();

	// 使用刚指定的配置项和数据显示图表。
	myChart.setOption(option);
	jsonUrl: rootPath + '/gasEquip/findGraphData.sxml'
});
