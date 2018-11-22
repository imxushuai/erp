//记录当前编辑行
var editIndex = -1;

$(function () {

    //初始化datagrid
   $('#ordersgrid').datagrid({
        columns:[[
            {field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{
                        disabled:true}
                    }
                },
            {field:'goodsname',title:'商品名称',width:100,editor:{type:'combobox',options:{
                        url:'goods_list',
                        valueField:'name',
                        textField:'name',
                        onSelect:function (goods) {
                            //获取商品编号编辑器，并赋值
                            var goodsuuidEditor = getEditor('goodsuuid');
                            $(goodsuuidEditor.target).val(goods.uuid);
                            //获取商品价格编辑器，并赋值
                            var priceEditor = getEditor('price');
                            if(Request['type'] * 1 == 1) {
                                $(priceEditor.target).val(goods.inprice);
                            }
                            if(Request['type'] * 1 == 2) {
                                $(priceEditor.target).val(goods.outprice);
                            }

                            //新增录入时，默认选中数量框
                            var numEditor = getEditor('num');
                            $(numEditor.target).select();

                            //绑定键盘监听事件
                            bindGridEditor();
                            //重新计算金额和合计
                            cal();
                            sum();

                        }
                    }
                }},
            {field:'price',title:'价格',width:100,editor:{
                    type:'numberbox',
                    options:{precision:2,disabled:true}
                    }
                },
            {field:'num',title:'数量',width:100,editor:{type:'numberbox'}},
            {field:'money',title:'金额',width:100,editor:{
                    type:'numberbox',
                    options:{precision:2,disabled:true}
                    }
                },
            {field:'-',title:'操作',width:70,align:'center',formatter:function (value,row,rowIndex) {
                        if(row.num != '合计') {
                            return '<a href=\"javascript:void(0)\" onclick=\"deleteRow(' + rowIndex+ ')\">删除';
                        }
                    }
                },
        ]],
       singleSelect: true,
       rownumbers:true,
       //显示页脚行
       showFooter:true,
       toolbar:[
           {
               text: '新增',
               iconCls:'icon-add',
               handler:function () {
                   // alert('新增');
                   //新增前，先关闭正在编辑的行
                   if(editIndex > -1) {
                       $('#ordersgrid').datagrid('endEdit',editIndex);
                   }
                   $('#ordersgrid').datagrid('appendRow',{'num':'0','money':0});
                   //获取追加的行索引
                   var rows = $('#ordersgrid').datagrid('getRows');
                   //进入编辑模式
                   $('#ordersgrid').datagrid('beginEdit',rows.length - 1);
                   //记录当前行的索引
                   editIndex = rows.length - 1;
               }
           },'-',{
               text: '提交',
               iconCls:'icon-save',
               handler:function () {
                   // alert('提交');
                   //关闭当前编辑行
                   if(editIndex > -1) {
                       $('#ordersgrid').datagrid('endEdit',editIndex);
                   }
                   //序列化表单数据
                   var formData = $('#orderForm').serializeJSON();
                   //获取订单明细数据
                   var rows = $('#ordersgrid').datagrid('getRows');
                   if(rows.length < 1) {
                       return;
                   }
                   //将rows转换为json串,并赋值到formData中
                   formData.json = JSON.stringify(rows);
                   
                   //提交
                   $.ajax({
                       url:'orders_add?t.type=' + Request['type'],
                       type:'post',
                       dataType:'json',
                       data:formData,
                       success:function (rtn) {
                           $.messager.alert('提示',rtn.message,'info',function () {
                               if(rtn.success) {
                                   //清空数据
                                   $('supplier').combogrid('clear');
                                   $('#ordersgrid').datagrid('loadData',{
                                       total:0,rows:[],
                                       footer:[{num: '合计', money: 0}]
                                   });

                                   //关闭增加订单窗口
                                   $('#addOrdersDlg').dialog('close');
                                   //刷新数据表格
                                   $('#ordersgrid').datagrid('reload');
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
               $('#ordersgrid').datagrid('endEdit',editIndex);
           }
           //开启编辑状态
           $('#ordersgrid').datagrid('beginEdit',rowIndex);
           editIndex = rowIndex;

           //绑定键盘监听事件
           bindGridEditor();
       }
   });

   //初始化页脚行
    $('#ordersgrid').datagrid('reloadFooter',[
        {num: '合计', money: 0},
    ]);

    //初始化下拉数据表格
    $('#supplier').combogrid({
        panelWidth : 700,
        //id字段
        idField : 'uuid',
        //选择后下拉框中显示的字段
        textField : 'name',
        url : 'supplier_list?t1.type=' + Request['type'],
        columns:[[
            {field:'uuid',title:'编号',width:100},
            {field:'name',title:'名称',width:100},
            {field:'address',title:'联系地址',width:100},
            {field:'contact',title:'联系人',width:100},
            {field:'tele',title:'联系电话',width:100},
            {field:'email',title:'邮件地址',width:100},
        ]],
        mode:'remote',
        required:true
    });


});

//获取当前行的指定编辑器
function getEditor(_field) {
    return  $('#ordersgrid').datagrid('getEditor',{
        index: editIndex,
        field: _field
    });
}

//计算金额
function cal() {
    //获取数量编辑器
    var numEditor = getEditor('num');
    //取出值
    var num = $(numEditor.target).val();

    //获取价格编辑器,并获取值
    var priceEditor = getEditor('price');
    var price = $(priceEditor.target).val();
    //计算金额,并保留两位小数
    var money = num * price;
    money = money.toFixed(2);

    //获取金额编辑器,并赋值
    var moneyEditor = getEditor('money');
    $(moneyEditor.target).val(money);

    //将money设置会表格
    $('#ordersgrid').datagrid('getRows')[editIndex].money = money;
}

//绑定键盘输入事件
function bindGridEditor() {
    //获取数量编辑器
    var numEditor = getEditor('num');
    $(numEditor.target).bind('keyup',function () {
        cal();
        sum();
    });
}

//计算合计
function sum() {
    //获取所有行
    var rows = $('#ordersgrid').datagrid('getRows');
    var total = 0;
    //循环累计
    $.each(rows, function (i, row) {
        total += parseFloat(row.money);
    });
    total = total.toFixed(2);
    //初始化页脚行
    $('#ordersgrid').datagrid('reloadFooter',[
        {num: '合计', money: total},
    ]);
}

function deleteRow(rowIndex) {
    //关闭当前编辑行
    $('#ordersgrid').datagrid('endEdit',editIndex);

    //删除行
    $('#ordersgrid').datagrid('deleteRow',rowIndex);
    //获取删除行过后的数据
    var data = $('#ordersgrid').datagrid('getData');
    //重新加载数据
    $('#ordersgrid').datagrid('loadData',data);
    //重新计算合计
    sum();
}
