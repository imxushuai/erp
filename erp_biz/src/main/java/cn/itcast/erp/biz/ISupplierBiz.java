package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Supplier;

import java.io.OutputStream;

/**
 * 供应商业务逻辑层接口
 * @author Administrator
 *
 */
public interface ISupplierBiz extends IBaseBiz<Supplier>{

    void export(OutputStream os, Supplier t1);

}

