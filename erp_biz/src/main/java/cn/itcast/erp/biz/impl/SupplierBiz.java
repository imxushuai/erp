package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Supplier;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 供应商业务逻辑类
 *
 * @author Administrator
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

    private ISupplierDao supplierDao;

    public void setSupplierDao(ISupplierDao supplierDao) {
        this.supplierDao = supplierDao;
        super.setBaseDao(this.supplierDao);
    }

    @Override
    public void export(OutputStream os, Supplier t1) {
        // 根据查询条件获取供应商/客户列表
        List<Supplier> supplierList = super.getList(t1, null, null);
        // 创建excel工作簿
        HSSFWorkbook wk = new HSSFWorkbook();
        HSSFSheet sheet = null;
        // 根据查询条件中的类型来创建相应名称的工作表
        if ("1".equals(t1.getType())) {
            sheet = wk.createSheet("供应商");
        }
        if ("2".equals(t1.getType())) {
            sheet = wk.createSheet("客户");
        }

        // 写入表头
        HSSFRow row = sheet.createRow(0);
        // 定义好每一列的标题
        String[] headerNames = {"名称", "地址", "联系人", "电话", "Email"};
        // 指定每一列的宽度
        int[] columnWidths = {4000, 8000, 2000, 3000, 8000};
        HSSFCell cell = null;
        for (int i = 0; i < headerNames.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(headerNames[i]);
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // 写入内容
        int i = 1;
        for (Supplier supplier : supplierList) {
            row = sheet.createRow(i);
            //必须按照hderarNames的顺序来
            row.createCell(0).setCellValue(supplier.getName());//名称
            row.createCell(1).setCellValue(supplier.getAddress());//地址
            row.createCell(2).setCellValue(supplier.getContact());//联系人
            row.createCell(3).setCellValue(supplier.getTele());//联系电话
            row.createCell(4).setCellValue(supplier.getEmail());//邮件地址
            i++;
        }
        try {
            // 写入到输出流中
            wk.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭工作簿
                wk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
