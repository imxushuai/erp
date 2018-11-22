package cn.itcast.erp.biz;

import cn.itcast.erp.entity.Orders;

import java.io.OutputStream;

/**
 * 订单业务逻辑层接口
 *
 * @author Administrator
 */
public interface IOrdersBiz extends IBaseBiz<Orders> {

    /**
     * 审核订单
     *
     * @param id   订单编号
     * @param uuid 审核员id
     */
    void doCheck(long id, Long uuid);

    /**
     * 确认订单
     *
     * @param id   订单标号
     * @param uuid 确认员id
     */
    void doStart(long id, Long uuid);

    /**
     * 导出指定订单
     *
     * @param os   输出流
     * @param uuid 订单id
     */
    void export(OutputStream os, Long uuid);
}

