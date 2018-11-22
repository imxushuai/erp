package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.exception.ErpException;

import java.util.Date;
import java.util.List;

/**
 * 订单明细业务逻辑类
 *
 * @author Administrator
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

    /**
     * 入库操作
     */
    private final String TYPE_IN_STORE = "0";
    /**
     * 出库操作
     */
    private final String TYPE_OUT_STORE = "1";

    private IOrderdetailDao orderdetailDao;

    public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
        this.orderdetailDao = orderdetailDao;
        super.setBaseDao(this.orderdetailDao);
    }

    private IStoredetailDao storedetailDao;
    private IStoreoperDao storeoperDao;
    private IOrdersDao ordersDao;

    public void setOrdersDao(IOrdersDao ordersDao) {
        this.ordersDao = ordersDao;
    }

    public void setStoreoperDao(IStoreoperDao storeoperDao) {
        this.storeoperDao = storeoperDao;
    }

    public void setStoredetailDao(IStoredetailDao storedetailDao) {
        this.storedetailDao = storedetailDao;
    }

    @Override
    public void doInStore(Long uuid, Long storeuuid, Long empuuid) {
        //获取订单详情
        Orderdetail orderdetail = orderdetailDao.get(uuid);
        if (orderdetail == null) {
            throw new ErpException("订单详情不存在");
        }
        //判断订单明细状态是否合法
        if (!orderdetail.getState().equals(Orderdetail.STATE_NOT_IN)) {
            throw new ErpException("订单详情状态不合法");
        }
        //修改入库相关信息
        orderdetail.setState(Orderdetail.STATE_IN);
        orderdetail.setStoreuuid(storeuuid);
        orderdetail.setEndtime(new Date());
        orderdetail.setEnder(empuuid);

        //修改库存表信息
        updateStore(orderdetail, this.TYPE_IN_STORE);

        //插入库存变更记录
        insertStoreoper(orderdetail, this.TYPE_IN_STORE);

        //查看订单明细对应的订单是否还存在未入库信息
        checkOrdersByOrderdetail(orderdetail,this.TYPE_IN_STORE);

        //保存订单详情信息
        orderdetailDao.update(orderdetail);
    }

    @Override
    public void doOutStore(Long uuid, Long storeuuid, Long empuuid) {
        //获取订单明细
        Orderdetail orderdetail = orderdetailDao.get(uuid);
        if (orderdetail == null) {
            throw new ErpException("订单详情不存在");
        }
        //判断订单明细状态是否合法
        if (!orderdetail.getState().equals(Orderdetail.STATE_NOT_OUT)) {
            throw new ErpException("订单详情状态不合法");
        }
        //修改出库信息
        orderdetail.setState(Orderdetail.STATE_OUT);
        orderdetail.setStoreuuid(storeuuid);
        orderdetail.setEndtime(new Date());
        orderdetail.setEnder(empuuid);

        //修改库存表信息
        updateStore(orderdetail, this.TYPE_OUT_STORE);

        //插入库存表更记录
        insertStoreoper(orderdetail, this.TYPE_OUT_STORE);

        //查看订单明细对应的订单是否还存在未入库信息
        checkOrdersByOrderdetail(orderdetail,this.TYPE_OUT_STORE);

        //保存订单详情信息
        orderdetailDao.update(orderdetail);

    }

    /**
     * 校验订单明细对应订单
     *
     * @param orderdetail 订单详情
     * @param type        操作类型
     */
    private void checkOrdersByOrderdetail(Orderdetail orderdetail, String type) {
        //获取订单明细对应的订单
        Orders orders = orderdetail.getOrders();
        if (orders == null) {
            throw new ErpException("订单不存在");
        }
        if(type.equals(this.TYPE_IN_STORE)) {//为入库操作
            //判断订单状态是否合法
            if (!orders.getState().equals(Orders.STATE_START)) {
                throw new ErpException("订单状态不合法");
            }
        } else if(type.equals((this.TYPE_OUT_STORE))) {//为出库操作
            if (!orders.getState().equals(Orders.STATE_NOT_OUT)) {
                throw new ErpException("订单已出库，请勿重复出库");
            }
        } else {
            throw new ErpException("未知操作类型");
        }
        //构造查询条件
        Orderdetail param = new Orderdetail();
        param.setOrders(orders);
        if(type.equals(this.TYPE_IN_STORE)) {//入库操作
            param.setState(Orderdetail.STATE_NOT_IN);
        } else if(type.equals((this.TYPE_OUT_STORE))) {//为出库操作
            param.setState(Orderdetail.STATE_NOT_OUT);
        }
        //调用getCount(),获取未入库的明细记录数
        long count = orderdetailDao.getCount(param, null, null);
        if (count == 0) {//没有未入库的订单明细
            /*
             * 1.设置操作员
             * 2.设置入库时间
             * 3.设置订单状态为end
             */
            orders.setEnder(orderdetail.getEnder());
            orders.setEndtime(orderdetail.getEndtime());
            if(type.equals(this.TYPE_IN_STORE)) {//入库操作
                orders.setState(Orders.STATE_END);
            } else if(type.equals((this.TYPE_OUT_STORE))) {//为出库操作
                orders.setState(Orders.STATE_OUT);
            }

        }
        //保存订单,可以不执行更新操作，更新Orderdetail时，会自动更新orders
        //ordersDao.update(orders);

    }

    //我的checkOrdersByOrderdetail思路
    /*Orders orders = ordersDao.get(orderdetail.getOrdersuuid());
        if(orders == null) {
            throw new ErpException("订单不存在");
        }
        //判断订单状态是否合法
        if(!orders.getState().equals(Orders.STATE_START)) {
            throw new ErpException("订单状态不合法");
        }
        //获取订单中的订单明细列表
        List<Orderdetail> orderdetailList = orders.getOrderdetailList();
        //遍历订单明细,查看是否还有未入库的订单
        for(Orderdetail o : orderdetailList) {
            if(o.getState().equals(Orderdetail.STATE_NOT_IN)) {//当前订单状态为未入库
                //直接结束此方法
                return;
            }
        }
        //循环完毕，所有订单详情状态均为已入库，执行相关操作
        *//*
     * 1.设置操作员
     * 2.设置入库时间
     * 3.设置订单状态为end
     *//*
        orders.setEnder(orderdetail.getEnder());
        orders.setEndtime(orderdetail.getEndtime());
        orders.setState(Orders.STATE_END);

        //保存订单
        ordersDao.update(orders);*/

    /**
     * 插入库存变更记录
     *
     * @param orderdetail 订单详情
     * @param type        操作类型
     */
    private void insertStoreoper(Orderdetail orderdetail, String type) {
        //构造库存变更记录
        Storeoper storeoper = new Storeoper();
        /*
         * 1.设置操作员id
         * 2.设置操作日期(注意：操作日期应为订单详情中的入库日期)
         * 3.设置仓库编号
         * 4.设置商品编号
         * 5.设置操作数量
         * 6.设置操作类型
         */
        storeoper.setEmpuuid(orderdetail.getEnder());
        storeoper.setOpertime(orderdetail.getEndtime());
        storeoper.setStoreuuid(orderdetail.getStoreuuid());
        storeoper.setGoodsuuid(orderdetail.getGoodsuuid());
        storeoper.setNum(orderdetail.getNum());
        storeoper.setType(type);

        //保存库存变更记录
        storeoperDao.add(storeoper);
    }

    /**
     * 修改库存表信息
     *
     * @param orderdetail 订单详情对象
     * @param type        操作类型。1：入库  2：出库
     */
    private void updateStore(Orderdetail orderdetail, String type) {
        //构造查询条件
        Storedetail storedetail = new Storedetail();
        storedetail.setGoodsuuid(orderdetail.getGoodsuuid());
        storedetail.setStoreuuid(orderdetail.getStoreuuid());
        //查询仓库
        List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
        Storedetail _storedetail;
        Long num = orderdetail.getNum();
        //判断是否已有该商品库存
        if (list != null && list.size() > 0) {//已有库存
            _storedetail = list.get(0);
            if (type.equals(this.TYPE_IN_STORE)) {//当前操作类型为入库操作
                //数量累加
                _storedetail.setNum(_storedetail.getNum() + num);
            } else if (type.equals(this.TYPE_OUT_STORE)) {//当前操作为出库操作
                Long newNum = _storedetail.getNum() - num;
                if (newNum < 0) {//出库后，库存为负
                    //库存不足
                    throw new ErpException("库存不足");
                }
                //库存充足,将出库后剩余的库存量保存
                _storedetail.setNum(newNum);
            } else {
                throw new ErpException("未知订单明细操作类型");
            }
        } else {//没有库存
            //构造新的库存详情
            _storedetail = storedetail;
            if (type.equals(this.TYPE_IN_STORE)) {//当前操作类型为入库操作
                _storedetail.setNum(num);
            } else if (type.equals(this.TYPE_OUT_STORE)) {//当前操作为出库操作
                throw new ErpException("没有该商品的库存信息");
            } else {
                throw new ErpException("未知订单明细操作类型");
            }
        }
        //保存入库信息
        storedetailDao.saveOrUpdate(_storedetail);
    }
}
