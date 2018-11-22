package cn.itcast.erp.biz;

import cn.itcast.erp.entity.Inventory;

/**
 * 盘盈盘亏业务逻辑层接口
 *
 * @author Administrator
 */
public interface IInventoryBiz extends IBaseBiz<Inventory> {

    /**
     * 盘盈盘亏审核
     *
     * @param id    盘盈盘亏id
     * @param empId 操作员id
     */
    void doCheck(long id, Long empId);
}

