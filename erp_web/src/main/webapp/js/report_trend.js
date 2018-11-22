$(function () {
    $('#grid').datagrid({
        url: 'report_trendReport',
        columns: [[
            {field: 'name', title: '月份', width: 200},
            {field: 'y', title: '销售额', width: 200},
        ]],
        singleSelect: true,
        onLoadSuccess:function (data) {
            //alert(JSON.stringify(data.rows));
            showChart(data.rows);
        }
    });

    //点击查询按钮
    $('#btnSearch').bind('click', function () {
        //把表单数据转换成json对象
        var formData = $('#searchForm').serializeJSON();
        $('#grid').datagrid('load', formData);
    });

});

function showChart(data) {
    /*
            制作水印
         */
    var waterMarkText = '蓝云ERP';

    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext('2d');
    canvas.width = canvas.height = 100;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.globalAlpha = 0.08;
    ctx.font = '20px Microsoft Yahei';
    ctx.translate(50, 50);
    ctx.rotate(-Math.PI / 4);
    ctx.fillText(waterMarkText, 0, 0);

    var myChart = echarts.init(document.getElementById("charts"));
    //生成图表数据
    myChart.setOption({
        //显示水印
        backgroundColor: {
            type: 'pattern',
            image: canvas,
            repeat: 'repeat'
        },
        //标题属性
        title : {
            text: '销售趋势分析',
            x:'center'
        },
        tooltip : {
            trigger: 'axis',
            axisPointer: {
                type: 'cross',
                label: {
                    backgroundColor: '#6a7985'
                }
            }
        },
        toolbox: {
            feature: {
                dataView : {show: true, readOnly: false},
                magicType : {show: true, type: ['line', 'bar']},
                restore : {show: true},
                saveAsImage: {}
            },
            right:50,
        },
        xAxis: {
            type: 'category',
            data: (function () {
                var res = [];
                for(var i=0;i<data.length;i++) {
                    res.push(data[i].name);
                }
                return res;
            })()
        },
        yAxis: {
            type: 'value'
        },
        series: [
            {
            data: function () {
                var res = [];
                for(var i=0;i<data.length;i++) {
                    res.push(data[i].y);
                }
                return res;
            }(),
            type: 'line',
            label: {
                normal: {
                    position: 'top',
                    show: true
                }
            },
        }]
    });

}