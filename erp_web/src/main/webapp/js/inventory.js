$(function () {
    var url = 'inventory_listByPage';

    if(Request['oper'] == 'doCheck') {
        url += '?t1.state=0';
    }

    //初始化查询条件相关
    $('#search').panel({
        closed:true
    });

    if(Request['oper'] == 'search') {
        $('#search').panel({
            closed:false
        });
    }

    $('#grid').datagrid({
        url: url,
        singleSelect: true,
        pagination: true,
        fitColumns: true,
        columns: [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'goodsName', title: '商品', width: 100},
            {field: 'storeName', title: '仓库', width: 100},
            {field: 'num', title: '数量', width: 100},
            {field: 'type', title: '类型', width: 100,formatter:getType},
            {field: 'createtime', title: '登记日期', width: 100,formatter:formatDate},
            {field: 'checktime', title: '审核日期', width: 100,formatter:formatDate},
            {field: 'createrName', title: '登记人', width: 100},
            {field: 'checkerName', title: '审核人', width: 100},
            {field: 'state', title: '状态', width: 100,formatter:function (value) {
                    switch (value * 1){
                        case 0 : return '未审核';
                        case 1 : return '已审核';
                        default: return '';
                    }
                }},
            {field: 'remark', title: '备注', width: 300},
        ]],
    });

    //点击查询按钮
    $('#btnSearch').bind('click',function(){
        //把表单数据转换成json对象
        var formData = $('#searchForm').serializeJSON();
        $('#grid').datagrid('load',formData);
    });

    //盘盈盘亏登记
    if(Request['oper'] == 'inventory') {
        $('#grid').datagrid({
           toolbar:[
               {
                   text:'盘盈盘亏登记',
                   iconCls:'icon-add',
                   handler:function () {
                       $('#addInventoryDlg').dialog('open');
                   }
               }
           ]
        });
    }
    //初始化盘盈盘亏对话框
    $('#addInventoryDlg').dialog({
        title: '盘盈盘亏登记',
        width: 260,
        height: 260,
        modal: true,
        closed: true,
        buttons:[
            {
                text: '登记',
                iconCls: 'icon-save',
                handler: function () {
                    // 校验表单输入内容是否完善
                    var flag = $('#inventoryForm').form('validate');
                    if(flag) {
                        //序列化表单数据
                        var formData = $('#inventoryForm').serializeJSON();
                        $.ajax({
                            url:'inventory_add',
                            data:formData,
                            dataType:'json',
                            type:'post',
                            success:function (rtn) {
                                $.messager.alert('提示',rtn.message,'info',function () {
                                   if(rtn.success) {
                                       // 清空表单数据
                                       $('#inventoryForm').form('clear');
                                       // 关闭盘盈盘亏登记对话框
                                       $('#addInventoryDlg').dialog('close');
                                       //刷新数据表格
                                       $('#grid').datagrid('reload');
                                   }
                                });
                            }
                        });
                    }
                }
            }
        ]
    });


    //盘盈盘亏审核
    if(Request['oper'] == 'doCheck') {
        $('#grid').datagrid({
            onDblClickRow:function (rowIndex, rowData) {
                //设置数据
                $('#id').val(rowData.uuid);
                $('#inventoryId').html(rowData.uuid);
                $('#inventoryTime').html(formatDate(rowData.createtime));
                $('#goodsname').html(rowData.goodsName);
                $('#storename').html(rowData.storeName);
                $('#num').html(rowData.num);
                $('#inventoryType').html(getType(rowData.type));
                $('#inventoryRemark').html(rowData.remark);
                //打开盘盈盘亏审核对话框
                $('#checkInventoryDlg').dialog('open');
            }
        });
    }
    //初始化盘盈盘亏审核对话框
    $('#checkInventoryDlg').dialog({
        title: '盘盈盘亏登记',
        width: 260,
        height: 260,
        modal: true,
        closed: true,
        buttons:[
            {
                text:'审核',
                iconCls:'icon-search',
                handler:function () {
                    // alert('审核');
                    $.messager.confirm('提示','确定要审核该盘盈盘亏吗?',function (r) {
                        if(r) {
                            $.ajax({
                                url:'inventory_doCheck?id=' + $('#inventoryId').html(),
                                dataType:'json',
                                type:'post',
                                success:function (rtn) {
                                    $.messager.alert('提示',rtn.message,'info',function () {
                                       if(rtn.success) {
                                           //关闭审核对话框
                                           $('#checkInventoryDlg').dialog('close');
                                           //刷新表格数据
                                           $('#grid').datagrid('reload');
                                       }
                                    });
                                }
                            });
                        }
                    });

                }
            }
        ]
    });
});

//格式化日期
function formatDate(value) {
    return new Date(value).Format("yyyy-MM-dd");
}

//获取type
function getType(value) {
    switch (value * 1){
        case 1 : return '盘盈';
        case 2 : return '盘亏';
        default: return '';
    }
}