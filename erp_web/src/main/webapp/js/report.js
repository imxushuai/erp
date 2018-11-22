$(function () {
    $('#grid').datagrid({
        url: 'report_ordersReport',
        columns: [[
            {field: 'name', title: '商品类型', width: 300},
            {field: 'y', title: '销售额', width: 300},
        ]],
        singleSelect: true,
        onLoadSuccess:function (data) {
            // alert(JSON.stringify(data.rows));
            showChart(data.rows);
        }
    });

    //点击查询按钮
    $('#btnSearch').bind('click', function () {
        //把表单数据转换成json对象
        var formData = $('#searchForm').serializeJSON();
        if (formData.endDate != '') {
            formData.endDate += '23:59:59';
        }

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
        //透明度
        ctx.globalAlpha = 0.08;
        //字体
        ctx.font = '20px Microsoft Yahei';
        ctx.translate(50, 50);
        //旋转
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
                text: '销售统计图',
                x:'center'
            },
            //提示框属性
            tooltip : {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            //保存图片到本地
            toolbox: {
                feature: {
                    dataView : {show: true, readOnly: false},
                    saveAsImage: {}
                },
                right:50,
                top:50
            },
            //图例属性
            legend: {
                orient: 'vertical',
                // 图例距离左方距离
                left: 50,
                // 图例距离上方距离
                top: 50,
                //设置图例值
                data: (function(){
                    var res = [];
                    for(var i=0;i<data.length;i++){
                        res.push(data[i].name);
                    }
                    return res;
                })()
            },
            //数据
            series : [
                {
                    name: '商品类型',
                    type: 'pie',
                    radius : '55%',
                    center: ['50%', '60%'],
                    //单击时，弹出该商品类型区域
                    selectedMode: 'single',
                    //显示的提示框
                    label: {
                        normal: {
                            formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
                            backgroundColor: '#eee',
                            borderColor: '#aaa',
                            borderWidth: 1,
                            borderRadius: 4,
                            // shadowBlur:3,
                            // shadowOffsetX: 2,
                            // shadowOffsetY: 2,
                            // shadowColor: '#999',
                            // padding: [0, 7],
                            rich: {
                                a: {
                                    color: '#999',
                                    lineHeight: 22,
                                    align: 'center'
                                },
                                hr: {
                                    borderColor: '#aaa',
                                    width: '100%',
                                    borderWidth: 0.5,
                                    height: 0
                                },
                                b: {
                                    fontSize: 16,
                                    lineHeight: 33
                                },
                                per: {
                                    color: '#eee',
                                    backgroundColor: '#334455',
                                    padding: [2, 4],
                                    borderRadius: 2
                                }
                            }
                        }
                    },
                    // 设置数据
                    data:function(){
                        var res = [];
                        for(var i=0;i<data.length;i++){
                            res.push({
                                name:data[i].name,
                                value:data[i].y
                            });
                        }
                        return res;
                    }()

                },
            ],itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        });

}