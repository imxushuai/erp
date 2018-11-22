$(function () {
    $('#grid').datagrid({
        url: 'role_list',
        columns: [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'name', title: '名称', width: 100}
        ]],
        singleSelect: true,
        onClickRow: function (rowIndex, rowData) {
            $('#tree').tree({
                url: 'role_readRoleMenuList?id=' + rowData.uuid,
                //动画效果
                animate: true,
                //多选
                checkbox: true
            });
        }
    });

    //保存按钮,添加单击事件
    $('#btnSave').bind('click', function () {
        // alert(JSON.stringify($('#tree').tree('getChecked')));
        //得到勾选的全部节点
        var nodes = $('#tree').tree('getChecked');
        //拼接每个节点
        var checkArr = new Array();
        $.each(nodes, function (i, node) {
            checkArr.push(node.id);
        });
        //将数据转换成以逗号分隔的字符串
        var checkeds = checkArr.join(',');
        //封装数据
        var formData = {};
        //获取选中的角色
        var row = $('#grid').datagrid('getSelected');
        //将id赋值给要提交的数据中
        formData.id = row.uuid;
        //将选中的权限id设置到要提交的数据中
        formData.checkeds = checkeds;

        //发送更新请求
        $.ajax({
            url: 'role_updateRoleMenuList',
            data: formData,
            dataType:'json',
            type:'post',
            success:function (rtn) {
                $.messager.alert('提示', rtn.message, 'info');
            }
        });

    });
});