$(function () {
    $('#grid').datagrid({
        url:'storedetail_listByPage',
        columns:[[
            {field:'uuid',title:'编号',width:100},
            {field:'storeName',title:'仓库',width:100},
            {field:'goodsName',title:'商品',width:100},
            {field:'num',title:'数量',width:100},
        ]],
        singleSelect: true,
        pagination: true,
        onDblClickRow:function (rowIndex, rowData) {
            $('#storedetailDlg').dialog('open');
            //设置表格值
            $('#goodsname').html(rowData.goodsName);
            $('#storename').html(rowData.storeName);
            $('#storedetaigrid').datagrid('reload',{
                't1.goodsuuid':rowData.goodsuuid,
                't1.storeuuid': rowData.storeuuid
            });
        }
    });

    //点击查询按钮
    $('#btnSearch').bind('click',function(){
        //把表单数据转换成json对象
        var formData = $('#searchForm').serializeJSON();
        $('#grid').datagrid('load',formData);
    });

    //初始化库存表动记录表格
    $('#storedetaigrid').datagrid({
        columns: [[
            {field:'uuid',title:'编号',width:100},
            {field:'empName',title:'操作员',width:100},
            {field:'opertime',title:'操作日期',width:200,formatter:formatDate},
            {field:'num',title:'数量',width:100},
            {field:'type',title:'操作类型',width:100,formatter:function (value) {
                    switch (value * 1){
                        case 0 : return '入库';
                        case 1 : return '出库';
                        default: return '';
                    }
                }}
        ]],
        singleSelect: true,
        fitColumns: true,
    });
});