package cn.itcast.erp.biz;

import cn.itcast.erp.entity.Returnorders;

/**
 * 退货订单业务逻辑层接口
 *
 * @author Administrator
 */
public interface IReturnordersBiz extends IBaseBiz<Returnorders> {

    /**
     * 退货订单审核
     *
     * @param id   退货订单id
     * @param uuid 操作员id
     */
    void doCheck(long id, Long uuid);
}

