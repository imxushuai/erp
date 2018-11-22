package cn.itcast.erp.biz;

import cn.itcast.erp.entity.Returnorderdetail;

/**
 * 退货订单明细业务逻辑层接口
 *
 * @author Administrator
 */
public interface IReturnorderdetailBiz extends IBaseBiz<Returnorderdetail> {

    /**
     * 退货订单出库操作
     *
     * @param returnordersId 退货订单明细
     * @param storeuuid      仓库id
     * @param empuuid        操作员id
     */
    void doOutStore(long returnordersId, Long storeuuid, Long empuuid);

    /**
     * 退货订单入库操作
     *
     * @param returnordersId 退货订单明细
     * @param storeuuid      仓库id
     * @param empuuid        操作员id
     */
    void doInStore(long returnordersId, Long storeuuid, Long empuuid);
}

