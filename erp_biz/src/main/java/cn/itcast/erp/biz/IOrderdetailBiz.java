package cn.itcast.erp.biz;

import cn.itcast.erp.entity.Orderdetail;

/**
 * 订单明细业务逻辑层接口
 *
 * @author Administrator
 */
public interface IOrderdetailBiz extends IBaseBiz<Orderdetail> {

    /**
     * 入库
     *
     * @param uuid      订单详情编号
     * @param storeuuid 仓库编号
     * @param empuuid   操作员编号
     */
    void doInStore(Long uuid, Long storeuuid, Long empuuid);

    /**
     * 出库
     *
     * @param uuid      订单详情编号
     * @param storeuuid 仓库编号
     * @param empuuid   操作员编号
     */
    void doOutStore(Long uuid, Long storeuuid, Long empuuid);
}

