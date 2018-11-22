$(function () {
    var url = "returnorders_listByPage?t1.type=1";

    if(Request['oper'] == 'myOrders') {
        url = 'returnorders_myListByPage?t1.type=1&t1.state=0';
    }

    if(Request['oper'] == 'doCheck') {
        url += '&t1.state=0';
    }

    if(Request['oper'] == 'doOutStore') {
        url += '&t1.state=1';
    }


    //初始化新增退货订单对话框
    //初始化增加订单窗口
    $('#addOrdersDlg').dialog({
        title: '增加订单',
        width: 700,
        height: 400,
        modal: true,
        closed: true
    });

    $('#grid').datagrid({
        url:url,
        columns:[[
            {field:'uuid',title:'编号',width:100},
            {field:'createtime',title:'生成日期',width:100,formatter:formatDate},
            {field:'checktime',title:'检查日期',width:100,formatter:formatDate},
            {field:'endtime',title:'结束日期',width:100,formatter:formatDate},
            {field:'type',title:'订单类型',width:100,formatter:function (value) {
                    switch (value * 1){
                        case 1 : return '采购退货订单';
                        case 2 : return '销售退货订单';
                        default: return  '';
                    }
                }},
            {field:'createrName',title:'下单员',width:100},
            {field:'checkerName',title:'审核员',width:100},
            {field:'enderName',title:'库管员',width:100},
            {field:'supplierName',title:'供应商',width:100},
            {field:'totalmoney',title:'合计金额',width:100,precision:'2'},
            {field:'state',title:'订单状态',width:100,formatter:getState},
            {field:'waybillsn',title:'运单号',width:100},
        ]],
        singleSelect: true,
        pagination: true,

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

    //采购退货登记
    if(Request['oper'] == 'myOrders') {
        $('#grid').datagrid({
            toolbar: [{
                text: '采购退货登记',
                iconCls: 'icon-edit',
                handler: function () {
                    // alert('退货');
                    $('#addOrdersDlg').dialog('open');
                }
            }]
        });
    }

    //采购退货订单审核相关
    if(Request['oper'] == 'doCheck') {
        $('#ordersDlg').dialog({
            toolbar: [{
                text: '审核',
                iconCls: 'icon-search',
                handler: doCheck
            }]
        });
    }
    $('#grid').datagrid({
        onDblClickRow: function (rowIndex, rowData) {
            // alert('双击' + rowData.uuid);
            //打开订单详情对话框
            $('#ordersDlg').dialog('open');
            //设置数据
            $('#itemgrid').datagrid('loadData', rowData.returnorderdetailList);
            $("#uuid").html(rowData.uuid);
            $("#supplierName").html(rowData.supplierName);
            $("#state").html(getState(rowData.state));
            $("#creater").html(rowData.createrName);
            $("#checker").html(rowData.checkerName);
            $("#ender").html(rowData.enderName);
            $("#createtime").html(formatDate(rowData.createtime));
            $("#checktime").html(formatDate(rowData.checktime));
            $("#endtime").html(formatDate(rowData.endtime));
        }
    });

    //采购退货订单出库相关
    if(Request['oper'] == 'doOutStore') {
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
    //初始化出入库对话框
    $('#itemDlg').dialog({
        width:300,
        height:200,
        title:'出库',
        modal:true,
        closed:true,
        buttons: [
            {
                text: '出库',
                iconCls: 'icon-save',
                handler: doInOutStore
            }
        ]
    });


});

//格式化日期
function formatDate(value) {
    return new Date(value).Format("yyyy-MM-dd");
}

//订单审核
function doCheck() {
    $.messager.confirm('提示', '确定审核该订单吗?', function (r) {
       if(r) {
           $.ajax({
               url:'returnorders_doCheck?id=' + $('#uuid').html(),
               type:'post',
               dataType:'json',
               success:function (rtn) {
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

//订单出库
function doInOutStore() {
    //序列化表单数据
    var formData = $('#itemForm').serializeJSON();
    if (formData.storeuuid == '') {
        $.messager.alert('提示', '请选择仓库', 'error');
        return;
    }
    $.messager.confirm('提示', '确定出库该订单吗?', function (r) {
        if(r) {
            $.ajax({
                url:'returnorderdetail_doOutStore',
                data:formData,
                dataType:'json',
                type:'post',
                success:function (rtn) {
                    $.messager.alert('提示', rtn.message, 'info', function () {
                       if(rtn.success) {
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

//获取订单详情状态
function getDetailState(value) {
    switch (value * 1) {
        case 0 :
            return '未出库';
        case 1 :
            return '已出库';
        default :
            return '';
    }
}

//获取退货订单状态
//获取订单状态
function getState(value) {
    switch (value * 1){
        case 0 : return '未审核';
        case 1 : return '已审核';
        case 2 : return '已出库';
        default: return  '';
    }
}