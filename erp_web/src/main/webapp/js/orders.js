$(function () {
    var url = 'orders_listByPage';

    var btnText = '';
    var inoutDlgTitle = '';

    //采购相关
    if (Request['oper'] == 'myOrders') {
        if(Request['type'] * 1 == 1) {
            url = 'orders_myListByPage?t1.type=1&t1.state=0';
            document.title = "我的采购订单";
            btnText = '采购申请';
            //显示供应商
            $('#addOrdersSupplier').html('供应商：');
        }
        if(Request['type'] * 1 == 2) {
            url = 'orders_myListByPage?t1.type=2&t1.state=0';
            document.title = "我的销售订单";
            btnText = '销售订单录入';
            //显示客户
            $('#addOrdersSupplier').html('客户：');
        }
    }
    if (Request['oper'] == 'orders') {
        if(Request['type'] * 1 == 1) {
            url += '?t1.type=1';
            document.title = "采购订单查询";
        }
        if(Request['type'] * 1 == 2) {
            url += '?t1.type=2';
            document.title = "销售订单查询";
        }
    }
    if (Request['oper'] == 'doCheck') {
        url += '?t1.type=1&t1.state=0';
        document.title = "采购订单审核";
    }
    if (Request['oper'] == 'doStart') {
        url += '?t1.type=1&t1.state=1';
        document.title = "采购订单确认";
    }
    if (Request['oper'] == 'doInStore') {
        url += '?t1.type=1&t1.state=2';
        document.title = "采购订单入库";
        inoutDlgTitle = '入库';
    }


    //销售出库
    if(Request['oper'] == 'doOutStore') {
        url += '?t1.type=2&t1.state=0';
        document.title = "销售订单出库";
        inoutDlgTitle = '出库';
    }


    $('#grid').datagrid({
        url: url,
        columns: getColumns(),
        singleSelect: true,
        pagination: true,
        fitColumns: true,
        onDblClickRow: function (rowIndex, rowData) {
            // alert('双击' + rowData.uuid);
            //打开订单详情对话框
            $('#ordersDlg').dialog('open');
            //设置数据
            $('#itemgrid').datagrid('loadData', rowData.orderdetailList);
            $("#uuid").html(rowData.uuid);
            $("#supplierName").html(rowData.supplierName);
            $("#state").html(getState(rowData.state));
            $("#creater").html(rowData.createrName);
            $("#checker").html(rowData.checkerName);
            $("#starter").html(rowData.starterName);
            $("#ender").html(rowData.enderName);
            $("#createtime").html(formatDate(rowData.createtime));
            $("#checktime").html(formatDate(rowData.checktime));
            $("#starttime").html(formatDate(rowData.starttime));
            $("#endtime").html(formatDate(rowData.endtime));
        }
    });

    //初始化订单明细表格
    $('#itemgrid').datagrid({
        columns: [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'goodsuuid', title: '商品编号', width: 100},
            {field: 'goodsname', title: '商品名称', width: 100},
            {field: 'price', title: '价格', width: 100},
            {field: 'num', title: '数量', width: 100},
            {field: 'money', title: '金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getDetailState},
        ]],
        singleSelect: true,
        fitColumns: true,

    });

    //初始化出入库对话框
    $('#itemDlg').dialog({
        width:300,
        height:200,
        title:inoutDlgTitle,
        modal:true,
        closed:true,
        buttons: [
            {
                text: inoutDlgTitle,
                iconCls: 'icon-save',
                handler: doInOutStore
            }
        ]
    });


    //如果为审核命令，添加审核按钮
    var toolbar = new Array();
    toolbar.push({
        text:'导出',
        iconCls:'icon-excel',
        handler:doExport
    });
    if (Request['oper'] == 'doCheck') {
        toolbar.push({
            text: '审核',
            iconCls: 'icon-search',
            handler: doCheck
        });
    }
    //如果为确认命令，添加审核按钮
    if (Request['oper'] == 'doStart') {
        toolbar.push({
            text: '确认',
            iconCls: 'icon-ok',
            handler: doStart
        });
    }
    //设置按钮
    $('#ordersDlg').dialog({
        toolbar: toolbar
    });

    //如果为入库操作,给itemgrid添加双击事件
    if (Request['oper'] == 'doInStore' || Request['oper'] == 'doOutStore') {
        $('#itemgrid').datagrid({
            onDblClickRow: function (rowIndex, rowData) {
                //赋值
                $('#itemuuid').val(rowData.uuid);
                $('#goodsuuid').html(rowData.goodsuuid);
                $('#goodsname').html(rowData.goodsname);
                $('#goodsnum').html(rowData.num);
                //打开出入库窗口
                $('#itemDlg').dialog('open');
            }
        });
    }

    //添加采购申请按钮
    if (Request['oper'] == 'myOrders') {
        $('#grid').datagrid({
            toolbar: [
                {
                    text: btnText,
                    iconCls: 'icon-add',
                    handler: function () {
                        // alert('采购申请');
                        $('#addOrdersDlg').dialog('open');
                    }
                }
            ]
        });
    }

    //初始化增加订单窗口
    $('#addOrdersDlg').dialog({
        title: '增加订单',
        width: 700,
        height: 400,
        modal: true,
        closed: true
    });
});

//获取订单状态
function getState(value) {
    //采购订单
    if(Request['type'] * 1 == 1) {
        switch (value * 1) {
            case 0 :
                return '未审核';
            case 1 :
                return '已审核';
            case 2 :
                return '已确认';
            case 3 :
                return '已入库';
            default :
                return '';
        }
    }
    //销售订单
    if(Request['type'] * 1 == 2) {
        switch (value * 1) {
            case 0 :
                return '未出库';
            case 1 :
                return '已出库';
            default :
                return '';
        }
    }
}

//格式化日期
function formatDate(value) {
    return new Date(value).Format("yyyy-MM-dd");
}

//获取订单详情状态
function getDetailState(value) {
    if(Request['type'] * 1 == 1) {
        switch (value * 1) {
            case 0 :
                return '未入库';
            case 1 :
                return '已入库';
            default :
                return '';
        }
    }
    if(Request['type'] * 1 == 2) {
        switch (value * 1) {
            case 0 :
                return '未出库';
            case 1 :
                return '已出库';
            default :
                return '';
        }
    }
}

//审核订单
function doCheck() {
    doExecute('doCheck', '审核');
}

//确认订单
function doStart() {
    doExecute('doStart', '确认');
}

function doInOutStore() {
    var message = '';
    var url = '';
    if(Request['type'] * 1 == 1) {
        message = '确认要入库该商品?';
        url = 'orderdetail_doInStore';
    }
    if(Request['type'] * 1 == 2) {
        message = '确认要出库该商品?';
        url = 'orderdetail_doOutStore';
    }

    // alert('入库');
    var formData = $('#itemForm').serializeJSON();
    if (formData.storeuuid == '') {
        $.messager.alert('提示', '请选择仓库', 'error');
        return;
    }
    $.messager.confirm('提示', message, function (r) {
        if (r) {
            //发送入库请求
            $.ajax({
                url: url,
                data: formData,
                dataType: 'json',
                type: 'post',
                success: function (rtn) {
                    $.messager.alert('提示', rtn.message, 'info', function () {
                        if (rtn.success) {
                            //关闭入库对话框
                            $('#itemDlg').dialog('close');

                            //设置明细状态
                            var row = $('#itemgrid').datagrid('getSelected');
                            row.state = '1';
                            //刷新数据到数据表格
                            var data = $('#itemgrid').datagrid('getData');
                            $('#itemgrid').datagrid('loadData', data);

                            //当全部订单明细状态都为已入库，执行操作
                            var flag = true;
                            $.each(data.rows, function (i, row) {
                                if (row.state * 1 == 0) {//当前订单状态为未入库状态
                                    //修改标识
                                    flag = false;
                                    //退出循环
                                    return false;
                                }
                            });
                            //判断标识
                            if (flag) {//所有订单明细均为已入库状态
                                //关闭订单详情对话框
                                $('#ordersDlg').dialog('close');
                                //刷新数据表格
                                $('#grid').datagrid('reload');
                            }
                        }
                    });
                }
            });
        }
    });
}

/**
 * 通用订单操作
 */
function doExecute(execute, text) {
    $.messager.confirm('提示', '确定' + text + '该订单吗?', function (r) {
        if (r) {
            $.ajax({
                url: 'orders_' + execute + '?id=' + $('#uuid').html(),
                type: 'post',
                dataType: 'json',
                success: function (rtn) {
                    $.messager.alert('提示', rtn.message, 'info', function () {
                        if (rtn.success) {
                            //关闭窗口
                            $('#ordersDlg').dialog('close');
                            //刷新数据表格
                            $('#grid').datagrid('reload');
                        }
                    });
                }
            });
        }
    });
}

//根据订单类型,获取columns属性
function getColumns() {
    if(Request['type'] * 1 == 1) {
        return [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'createtime', title: '生成日期', width: 100, formatter: formatDate},
            {field: 'checktime', title: '审核日期', width: 100, formatter: formatDate},
            {field: 'starttime', title: '确认日期', width: 100, formatter: formatDate},
            {field: 'endtime', title: '入库或出库日期', width: 100, formatter: formatDate},
            {field: 'createrName', title: '下单员', width: 100},
            {field: 'checkerName', title: '审核员', width: 100},
            {field: 'starterName', title: '采购员', width: 100},
            {field: 'enderName', title: '库管员', width: 100},
            {field: 'supplierName', title: '供应商', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getState},
            {field: 'waybillsn', title: '运单号', width: 100},
        ]];
    }
    if(Request['type'] * 1 == 2) {
        return [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'createtime', title: '生成日期', width: 100, formatter: formatDate},
            {field: 'endtime', title: '出库日期', width: 100, formatter: formatDate},
            {field: 'createrName', title: '下单员', width: 100},
            {field: 'enderName', title: '库管员', width: 100},
            {field: 'supplierName', title: '客户', width: 100},
            {field: 'totalmoney', title: '合计金额', width: 100},
            {field: 'state', title: '状态', width: 100, formatter: getState},
            {field: 'waybillsn', title: '运单号', width: 100},
        ]];
    }
}

//导出
function doExport() {
    $.download('orders_export',{'id':$('#uuid').html()});
}