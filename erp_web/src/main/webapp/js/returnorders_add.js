//记录当前编辑行
var editIndex = -1;
$(function () {
    $('#returnordersgrid').datagrid({
        columns: [[
            {field: 'goodsuuid', title: '商品编号', width: 100,editor:{type:'numberbox',options:{disabled:true}}},
            {field: 'goodsname', title: '商品名称', width: 100,editor:{
                    type:'combobox',
                    options:{
                        url:'goods_list',
                        valueField:'name',
                        textField:'name',
                        onSelect:function (goods) {
                            //设置商品编号
                            var goodsuuidEditor = getEditor('goodsuuid');
                            $(goodsuuidEditor.target).val(goods.uuid);

                            //设置商品价格
                            var priceEditor = getEditor('price');
                            $(priceEditor.target).val(goods.inprice);

                            //新增录入时，默认选中数量框
                            var numEditor = getEditor('num');
                            $(numEditor.target).select();

                            bindGridEditor();
                        }
                    },
                }
            },
            {field: 'price', title: '价格', width: 100,editor:{
                    type:'numberbox',options:{precision:2,disabled:true}
                }},
            {field: 'num', title: '数量', width: 100,editor:'numberbox'},
            {field: 'money', title: '金额', width: 100,editor:{
                    type:'numberbox',options:{precision:2,disabled:true}
                }},
            {field:'-',title:'操作',width:70,align:'center',formatter:function (value,row,rowIndex) {
                    if(row.num != '合计') {
                        return '<a href=\"javascript:void(0)\" onclick=\"deleteRow(' + rowIndex+ ')\">删除';
                    }
                }
            },
        ]],
        singleSelect:true,
        rownumbers:true,
        //显示页脚行
        showFooter:true,
        toolbar:[
            {
                text:'新增',
                iconCls:'icon-add',
                handler:function () {
                    //alert('新增');
                    if(editIndex > -1) {
                        $('#returnordersgrid').datagrid('endEdit', editIndex);
                    }
                    //开启编辑模式
                    $('#returnordersgrid').datagrid('appendRow',{
                        price:0,
                        num:0,
                        money:0
                    });
                    //获取最后一行,开启编辑状态
                    var rows = $('#returnordersgrid').datagrid('getRows');
                    editIndex = rows.length - 1;
                    $('#returnordersgrid').datagrid('beginEdit', editIndex);
                }
            },'-',{
                text:'提交',
                iconCls:'icon-save',
                handler:function () {
                    // alert('提交');
                    if(editIndex > -1) {
                        $('#returnordersgrid').datagrid('endEdit',editIndex);
                    }
                    //获取订单明细数据
                    var rows = $('#returnordersgrid').datagrid('getRows');
                    if(rows.length < 1) {
                        return;
                    }
                    //序列化表单数据
                    var formData = $('#orderForm').serializeJSON();
                    //将rows转换为json串,并赋值到formData中
                    formData.json = JSON.stringify(rows);

                    $.ajax({
                        url:'returnorders_add?t.type=1',
                        data:formData,
                        dataType:'json',
                        type:'post',
                        success:function (rtn) {
                            $.messager.alert('提示',rtn.message,'info',function () {
                                if(rtn.success) {
                                    //清空数据
                                    $('supplier').combogrid('clear');
                                    $('#returnordersgrid').datagrid('loadData',{
                                        total:0,rows:[],
                                        footer:[{num: '合计', money: 0}]
                                    });

                                    //关闭增加订单窗口
                                    $('#addOrdersDlg').dialog('close');
                                    //刷新数据表格
                                    $('#returnordersgrid').datagrid('reload');
                                }
                            });
                        }
                    });

                }
            }
        ],
        onClickRow:function (rowIndex, rowData) {
            // alert('单击');
            if(editIndex > -1) {
                $('#returnordersgrid').datagrid('endEdit',editIndex);
            }
            //开启编辑状态
            $('#returnordersgrid').datagrid('beginEdit',rowIndex);
            editIndex = rowIndex;

            //绑定键盘监听事件
            bindGridEditor();
        }
    });

    //初始化下拉数据表格
    $('#supplier').combogrid({
        panelWidth: 700,
        //id字段
        idField: 'uuid',
        //选择后下拉框中显示的字段
        textField: 'name',
        url: 'supplier_list?t1.type=1',
        columns: [[
            {field: 'uuid', title: '编号', width: 100},
            {field: 'name', title: '名称', width: 100},
            {field: 'address', title: '联系地址', width: 100},
            {field: 'contact', title: '联系人', width: 100},
            {field: 'tele', title: '联系电话', width: 100},
            {field: 'email', title: '邮件地址', width: 100},
        ]],
        mode: 'remote',
        required:true
    });

    //初始化页脚
    $('#returnordersgrid').datagrid('reloadFooter',[
        {num: '合计', money: 0},
    ]);
});

//获取当前行的指定编辑器
function getEditor(_field) {
    return  $('#returnordersgrid').datagrid('getEditor',{
        index: editIndex,
        field: _field
    });
}

//数量输入框,键盘输入时间
function bindGridEditor() {
    //获取数量编辑器
    var numEditor = getEditor('num');
    $(numEditor.target).bind('keyup',function () {
        cal();
        sum();
    });
}

//删除指定行
function deleteRow(rowIndex) {
    //关闭当前编辑行
    $('#returnordersgrid').datagrid('endEdit',editIndex);

    //删除行
    $('#returnordersgrid').datagrid('deleteRow',rowIndex);
    //获取删除行过后的数据
    var data = $('#returnordersgrid').datagrid('getData');
    //重新加载数据
    $('#returnordersgrid').datagrid('loadData',data);
    //重新计算合计
    sum();
}

//计算金额
function cal() {
    //获取数量和价格
    var numEditor = getEditor('num');
    var num = $(numEditor.target).val();
    var priceEditor = getEditor('price');
    var price = $(priceEditor.target).val();

    //计算金额
    var money = num * price;
    money = money.toFixed(2);

    //将值赋给money字段
    var moneyEditor = getEditor('money');
    $(moneyEditor.target).val(money);

    //将money设置会表格
    $('#returnordersgrid').datagrid('getRows')[editIndex].money = money;
}

//计算总计
function sum() {
    //获取所有行
    var rows = $('#returnordersgrid').datagrid('getRows');
    var total = 0;
    //循环累计
    $.each(rows, function (i, row) {
        total += parseFloat(row.money);
    });
    total = total.toFixed(2);
    //初始化页脚行
    $('#returnordersgrid').datagrid('reloadFooter',[
        {num: '合计', money: total},
    ]);
}