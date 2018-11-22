package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单业务逻辑类
 *
 * @author Administrator
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

    private IOrdersDao ordersDao;

    public void setOrdersDao(IOrdersDao ordersDao) {
        this.ordersDao = ordersDao;
        super.setBaseDao(this.ordersDao);
    }

    private IEmpDao empDao;
    private ISupplierDao supplierDao;

    public void setEmpDao(IEmpDao empDao) {
        this.empDao = empDao;
    }

    public void setSupplierDao(ISupplierDao supplierDao) {
        this.supplierDao = supplierDao;
    }

    @Override
    public void add(Orders orders) {
        /*
         * 补全数据
         */
        //1.设置订单状态
        orders.setState(Orders.STATE_CREATE);
        //2.设置订单创建时间
        orders.setCreatetime(new Date());
        //3.计算合计金额
        double total = 0;
        for (Orderdetail orderdetail : orders.getOrderdetailList()) {
            total += orderdetail.getMoney();
            //设置订单明细状态
            orderdetail.setState(Orderdetail.STATE_NOT_IN);
            //订单详情关联订单
            orderdetail.setOrders(orders);
        }
        orders.setTotalmoney(total);
        //4.设置订单类型
        //orders.setType(Orders.TYPE_IN);

        //保存订单
        ordersDao.add(orders);
    }

    @Override
    public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult, int maxResults) {
        List<Orders> ordersList = super.getListByPage(t1, t2, param, firstResult, maxResults);
        //缓存员工编号对应的员工名称
        Map<Long, String> empNameMap = new HashMap<>();
        //缓存供应商编号对应的供应商名称
        Map<Long, String> supplierNameMap = new HashMap<>();
        for (Orders orders : ordersList) {
            //设置对应员工名称
            orders.setCreaterName(getEmpName(orders.getCreater(), empNameMap, empDao));
            orders.setCheckerName(getEmpName(orders.getChecker(), empNameMap, empDao));
            orders.setStarterName(getEmpName(orders.getStarter(), empNameMap, empDao));
            orders.setEnderName(getEmpName(orders.getEnder(), empNameMap, empDao));

            //设置对应供应商名称
            orders.setSupplierName(getSupplierName(orders.getSupplieruuid(), supplierNameMap, supplierDao));

        }

        return ordersList;
    }


    @Override
    public void doCheck(long id, Long uuid) {
        doExecute(id, uuid, Orders.STATE_CREATE, Orders.STATE_CHECK, "当前订单状态不是未审核状态");
    }

    @Override
    public void doStart(long id, Long uuid) {
        doExecute(id, uuid, Orders.STATE_CHECK, Orders.STATE_START, "当前订单状态不是已审核状态");
    }

    @Override
    public void export(OutputStream os, Long uuid) {
        //创建一个工作簿
        HSSFWorkbook wb = new HSSFWorkbook();
        //获取订单
        Orders orders = ordersDao.get(uuid);
        List<Orderdetail> detailList = orders.getOrderdetailList();
        String sheetName = "";
        if (Orders.TYPE_IN.equals(orders.getType())) {
            sheetName = "采 购 单";
        }
        if (Orders.TYPE_OUT.equals(orders.getType())) {
            sheetName = "销 售 单";
        }
        //创建一个工作表
        HSSFSheet sheet = wb.createSheet(sheetName);
        //创建一行,行的索引是从0开始
        HSSFRow row = sheet.createRow(0);
        //创建内容体的单元格的样式
        HSSFCellStyle style_content = wb.createCellStyle();
        style_content.setBorderBottom(BorderStyle.THIN);//下边框
        style_content.setBorderTop(BorderStyle.THIN);//上边框
        style_content.setBorderLeft(BorderStyle.THIN);//左边框
        style_content.setBorderRight(BorderStyle.THIN);//右边框
        //对齐方式：水平居中
        style_content.setAlignment(HorizontalAlignment.CENTER);
        //垂直居中
        style_content.setVerticalAlignment(VerticalAlignment.CENTER);
        //创建内容样式的字体
        HSSFFont font_content = wb.createFont();
        //设置字体名称，相当选中哪种字符
        font_content.setFontName("宋体");
        //设置字体的大小
        font_content.setFontHeightInPoints((short) 11);
        style_content.setFont(font_content);

        //设置日期格式
        HSSFCellStyle style_date = wb.createCellStyle();
        //把 style_content里样式复制到date_style
        style_date.cloneStyleFrom(style_content);
        DataFormat df = wb.createDataFormat();
        style_date.setDataFormat(df.getFormat("yyyy-MM-dd HH:mm:ss"));

        //标题样式
        HSSFCellStyle style_title = wb.createCellStyle();
        style_title.setAlignment(HorizontalAlignment.CENTER);
        style_title.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont style_font = wb.createFont();
        style_font.setFontName("黑体");
        style_font.setFontHeightInPoints((short) 18);
        //加粗
        style_font.setBold(true);
        style_title.setFont(style_font);


        //合并单元格
        //标题：采购单
        /* 合并范围
         * 参数1：开始行
         * 参数1：结束行
         * 参数1：开始列
         * 参数1：结束列
         */
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        //供应商
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));
        //订单明细
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 3));

        //创建矩阵 11行，4列
        int rowCount = detailList.size() + 9;
        for (int i = 2; i <= rowCount; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 4; j++) {
                //设置单元格的样式
                row.createCell(j).setCellStyle(style_content);
            }
            //row.setHeight((short)500);
        }
        //必须先有创建的行和单元格
        //创建标题单元格
        HSSFCell titleCell = sheet.createRow(0).createCell(0);
        //设置标题样式
        titleCell.setCellStyle(style_title);
        titleCell.setCellValue(sheetName);

        sheet.getRow(2).getCell(0).setCellValue("供应商");
        sheet.getRow(3).getCell(0).setCellValue("下单日期");
        sheet.getRow(4).getCell(0).setCellValue("审核日期");
        sheet.getRow(5).getCell(0).setCellValue("采购日期");
        sheet.getRow(6).getCell(0).setCellValue("入库日期");
        sheet.getRow(3).getCell(2).setCellValue("经办人");
        sheet.getRow(4).getCell(2).setCellValue("经办人");
        sheet.getRow(5).getCell(2).setCellValue("经办人");
        sheet.getRow(6).getCell(2).setCellValue("经办人");

        sheet.getRow(7).getCell(0).setCellValue("订单明细");

        sheet.getRow(8).getCell(0).setCellValue("商品名称");
        sheet.getRow(8).getCell(1).setCellValue("数量");
        sheet.getRow(8).getCell(2).setCellValue("价格");
        sheet.getRow(8).getCell(3).setCellValue("金额");

        //设置行高
        //标题的行高
        sheet.getRow(0).setHeight((short) 1000);
        //内容体的行高
        for (int i = 2; i <= rowCount; i++) {
            sheet.getRow(i).setHeight((short) 500);
        }
        //设置列宽
        for (int i = 0; i < 4; i++) {
            sheet.setColumnWidth(i, 6000);
        }

        //缓存供应商编号与员工的名称, key=供应商的编号，value=供应商的名称
        Map<Long, String> supplierNameMap = new HashMap<Long, String>();
        //设置供应商
        sheet.getRow(2).getCell(1).setCellValue(getSupplierName(orders.getSupplieruuid(), supplierNameMap, supplierDao));

        //订单详情, 设置日期
        sheet.getRow(3).getCell(1).setCellStyle(style_date);
        sheet.getRow(4).getCell(1).setCellStyle(style_date);
        sheet.getRow(5).getCell(1).setCellStyle(style_date);
        sheet.getRow(6).getCell(1).setCellStyle(style_date);
        if (null != orders.getCreatetime()) {
            sheet.getRow(3).getCell(1).setCellValue(orders.getCreatetime());
        }
        if (null != orders.getChecktime()) {
            sheet.getRow(4).getCell(1).setCellValue(orders.getChecktime());
        }
        if (null != orders.getStarttime()) {
            sheet.getRow(5).getCell(1).setCellValue(orders.getStarttime());
        }
        if (null != orders.getEndtime()) {
            sheet.getRow(6).getCell(1).setCellValue(orders.getEndtime());
        }


        //缓存员工编号与员工的名称, key=员工的编号，value=员工的名称
        Map<Long, String> empNameMap = new HashMap<Long, String>();
        //设置经办人
        sheet.getRow(3).getCell(3).setCellValue(getEmpName(orders.getCreater(), empNameMap, empDao));
        sheet.getRow(4).getCell(3).setCellValue(getEmpName(orders.getChecker(), empNameMap, empDao));
        sheet.getRow(5).getCell(3).setCellValue(getEmpName(orders.getStarter(), empNameMap, empDao));
        sheet.getRow(6).getCell(3).setCellValue(getEmpName(orders.getEnder(), empNameMap, empDao));

        //设置明细内容
        int index = 0;
        Orderdetail od = null;
        for (int i = 9; i < rowCount; i++) {
            od = detailList.get(index);
            row = sheet.getRow(i);
            row.getCell(0).setCellValue(od.getGoodsname());
            row.getCell(1).setCellValue(od.getNum());
            row.getCell(2).setCellValue(od.getPrice());
            row.getCell(3).setCellValue(od.getMoney());
            index++;
        }
        //设置合计
        sheet.getRow(rowCount).getCell(0).setCellValue("合计");
        sheet.getRow(rowCount).getCell(3).setCellValue(orders.getTotalmoney());

        //写到输出流里去
        try {
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 通用订单操作
     *
     * @param orderId       订单编号
     * @param empId         操作员id
     * @param orderState    订单合法状态：当订单为此值是，才能进行合法操作
     * @param newOrderState 订单操作状态：修改订单状态为此状态
     * @param message       订单非法提示信息
     */
    private void doExecute(Long orderId, Long empId, String orderState, String newOrderState, String message) {
        Orders orders = ordersDao.get(orderId);
        //校验订单状态是否合法
        if (orders == null) {
            throw new ErpException("订单不存在");
        }
        if (!orders.getState().equals(orderState)) {
            throw new ErpException(message);
        }
        //进行确认操作
        orders.setState(newOrderState);
        //设置时间和员工ID
        if (newOrderState.equals(Orders.STATE_CHECK)) {//审核订单操作
            orders.setChecktime(new Date());
            orders.setChecker(empId);
        } else if (newOrderState.equals(Orders.STATE_START)) {//确认订单操作
            orders.setStarttime(new Date());
            orders.setStarter(empId);
        } else {
            throw new ErpException("订单操作指令异常");
        }

        //保存
        ordersDao.update(orders);
    }
}
