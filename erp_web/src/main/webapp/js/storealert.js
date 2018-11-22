$(function () {
    $('#grid').datagrid({
       url:'storedetail_storealertList',
        columns:[[
            {field:'uuid',title:'编号',width:100},
            {field:'name',title:'商品名称',width:100},
            {field:'storenum',title:'库存',width:100},
            {field:'outnum',title:'代发货数量',width:100},
        ]],
        singleSelect:true,
        toolbar:[
            {
                text:'发送库存预警邮件',
                iconCls:'icon-add',
                handler:function () {
                    //调用服务器发送邮件
                    $.ajax({
                        url:'storedetail_sendStorealertMail',
                        dataType:'json',
                        type:'post',
                        success:function (rtn) {
                            //提示信息
                            $.messager.alert('提示', rtn.message, 'info');
                        }
                    });
                }
            }
        ]
    });
});