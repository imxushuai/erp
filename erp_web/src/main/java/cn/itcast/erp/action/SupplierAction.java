package cn.itcast.erp.action;
import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.entity.Supplier;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 供应商Action 
 * @author Administrator
 *
 */
public class SupplierAction extends BaseAction<Supplier> {

	private ISupplierBiz supplierBiz;

	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}

	//自动补全参数
	private String q;

	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}

	@Override
	public void list() {
		if(null == getT1()) {
			setT1(new Supplier());
		}
		getT1().setName(q);
		super.list();
	}

	/**
	 * 导出excel表格
	 */
	public void export() {
		String filename = "";
		//根据类型生成文件名
		if("1".equals(getT1().getType())) {
			filename = "供应商.xls";
		}
		if("2".equals(getT1().getType())) {
			filename = "客户.xls";
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" +
					new String(filename.getBytes(),"ISO-8859-1"));//中文名称进行转码
			//调用导出业务
			supplierBiz.export(response.getOutputStream(), getT1());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
