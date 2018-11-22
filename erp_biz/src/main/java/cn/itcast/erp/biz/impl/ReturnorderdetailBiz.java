package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.dao.IReturnorderdetailDao;
import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.exception.ErpException;

import java.util.Date;
import java.util.List;

/**
 * 退货订单明细业务逻辑类
 *
 * @author Administrator
 */
public class ReturnorderdetailBiz extends BaseBiz<Returnorderdetail> implements IReturnorderdetailBiz {

    private IReturnorderdetailDao returnorderdetailDao;
    private IStoredetailDao storedetailDao;
    private IStoreoperDao storeoperDao;
    private IReturnordersDao returnordersDao;

    public void setStoredetailDao(IStoredetailDao storedetailDao) {
        this.storedetailDao = storedetailDao;
    }

    public void setStoreoperDao(IStoreoperDao storeoperDao) {
        this.storeoperDao = storeoperDao;
    }

    public void setReturnordersDao(IReturnordersDao returnordersDao) {
        this.returnordersDao = returnordersDao;
    }

    public void setReturnorderdetailDao(IReturnorderdetailDao returnorderdetailDao) {
        this.returnorderdetailDao = returnorderdetailDao;
        super.setBaseDao(this.returnorderdetailDao);
    }

    @Override
    public void doOutStore(long returnordersId, Long storeuuid, Long empuuid) {
        /******************************
         *    修改退货订单明细数据      *
         ******************************/
        //获取订单明细
        Returnorderdetail returnorderdetail = returnorderdetailDao.get(returnordersId);
        if (returnorderdetail == null) {
            throw new ErpException("退货订单明细不存在");
        }
        //判断订单明细状态是否合法
        if (!returnorderdetail.getState().equals(Returnorderdetail.STATE_NOT_OUT)) {
            throw new ErpException("退货订单已出库，请勿重复出库");
        }
        //设置订单明细状态
        returnorderdetail.setState(Returnorderdetail.STATE_OUT);
        //设置出库仓库
        returnorderdetail.setStoreuuid(storeuuid);
        //设置操作员id
        returnorderdetail.setEnder(empuuid);
        //设置出库时间
        returnorderdetail.setEndtime(new Date());

        /******************************
         *    修改库存明细数据         *
         ******************************/
        //构造查询条件
        Storedetail paramQuery = new Storedetail();
        paramQuery.setStoreuuid(storeuuid);
        paramQuery.setGoodsuuid(returnorderdetail.getGoodsuuid());
        //获取操作数量
        Long num = returnorderdetail.getNum();
        //查询仓库
        List<Storedetail> list = storedetailDao.getList(paramQuery, null, null);
        //判断是否有该商品库存
        if (list == null || list.size() < 1) {//没有库存信息
            throw new ErpException("库存不足");
        }
        Storedetail storedetail = list.get(0);
        Long newNum = storedetail.getNum() - num;
        if (newNum < 0) {
            throw new ErpException("库存不足");
        }
        storedetail.setNum(newNum);
        //保存
        storedetailDao.update(storedetail);

        /******************************
         *    插入库存变更记录         *
         ******************************/
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
        storeoper.setEmpuuid(empuuid);
        storeoper.setOpertime(returnorderdetail.getEndtime());
        storeoper.setStoreuuid(storeuuid);
        storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
        storeoper.setNum(returnorderdetail.getNum());
        storeoper.setType(Storeoper.TYPE_OUT);

        //保存库存变更记录
        storeoperDao.add(storeoper);

        /******************************
         * 查看退货订单对应明细是否全部入库
         ******************************/
        //获取对应的退货订单
        Returnorders returnorders = returnorderdetail.getOrders();
        if (returnorders == null) {
            throw new ErpException("退货订单不存在");
        }
        //校验退货订单状态是否合法
        if (!returnorders.getState().equals(Returnorders.STATE_CHECK)) {
            throw new ErpException("退货订单状态不合法");
        }
        //构造查询条件
        Returnorderdetail returnorderdetail1 = new Returnorderdetail();
        returnorderdetail1.setOrders(returnorders);
        returnorderdetail1.setState(Returnorderdetail.STATE_NOT_OUT);
        //计数未出库的退货订单明细
        long count = returnorderdetailDao.getCount(returnorderdetail1, null, null);
        if(count == 0) {//所有订单明细都已出库
            /*
             * 1.设置操作员
             * 2.设置入库时间
             * 3.设置订单状态为end
             */
            returnorders.setEnder(empuuid);
            returnorders.setEndtime(returnorderdetail.getEndtime());
            returnorders.setState(Returnorders.STATE_END);
        }

        /******************************
         *保存退货订单明细 (自动保存退货订单)
         ******************************/
        returnorderdetailDao.update(returnorderdetail);
    }

    @Override
    public void doInStore(long returnordersId, Long storeuuid, Long empuuid) {
        /******************************
         *    修改退货订单明细数据      *
         ******************************/
        //获取订单明细
        Returnorderdetail returnorderdetail = returnorderdetailDao.get(returnordersId);
        if (returnorderdetail == null) {
            throw new ErpException("退货订单明细不存在");
        }
        //判断订单明细状态是否合法
        if (!returnorderdetail.getState().equals(Returnorderdetail.STATE_NOT_IN)) {
            throw new ErpException("退货订单已入库库，请勿重复入库库");
        }
        //设置订单明细状态
        returnorderdetail.setState(Returnorderdetail.STATE_IN);
        //设置出库仓库
        returnorderdetail.setStoreuuid(storeuuid);
        //设置操作员id
        returnorderdetail.setEnder(empuuid);
        //设置出库时间
        returnorderdetail.setEndtime(new Date());

        /******************************
         *    修改库存明细数据         *
         ******************************/
        //构造查询条件
        Storedetail paramQuery = new Storedetail();
        paramQuery.setStoreuuid(storeuuid);
        paramQuery.setGoodsuuid(returnorderdetail.getGoodsuuid());
        //获取操作数量
        Long num = returnorderdetail.getNum();
        //查询仓库
        List<Storedetail> list = storedetailDao.getList(paramQuery, null, null);
        //判断是否有该商品库存
        if (list == null || list.size() < 1) {//没有库存信息
            throw new ErpException("仓库没有该商品库存信息");
        }
        Storedetail storedetail = list.get(0);
        Long newNum = storedetail.getNum() + num;
        storedetail.setNum(newNum);
        //保存
        storedetailDao.update(storedetail);

        /******************************
         *    插入库存变更记录         *
         ******************************/
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
        storeoper.setEmpuuid(empuuid);
        storeoper.setOpertime(returnorderdetail.getEndtime());
        storeoper.setStoreuuid(storeuuid);
        storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
        storeoper.setNum(returnorderdetail.getNum());
        storeoper.setType(Storeoper.TYPE_IN);

        //保存库存变更记录
        storeoperDao.add(storeoper);

        /******************************
         * 查看退货订单对应明细是否全部入库
         ******************************/
        //获取对应的退货订单
        Returnorders returnorders = returnorderdetail.getOrders();
        if (returnorders == null) {
            throw new ErpException("退货订单不存在");
        }
        //校验退货订单状态是否合法
        if (!returnorders.getState().equals(Returnorders.STATE_CHECK)) {
            throw new ErpException("退货订单状态不合法");
        }
        //构造查询条件
        Returnorderdetail returnorderdetail1 = new Returnorderdetail();
        returnorderdetail1.setOrders(returnorders);
        returnorderdetail1.setState(Returnorderdetail.STATE_NOT_IN);
        //计数未出库的退货订单明细
        long count = returnorderdetailDao.getCount(returnorderdetail1, null, null);
        if(count == 0) {//所有订单明细都已出库
            /*
             * 1.设置操作员
             * 2.设置入库时间
             * 3.设置订单状态为end
             */
            returnorders.setEnder(empuuid);
            returnorders.setEndtime(returnorderdetail.getEndtime());
            returnorders.setState(Returnorders.STATE_END);
        }

        /******************************
         *保存退货订单明细 (自动保存退货订单)
         ******************************/
        returnorderdetailDao.update(returnorderdetail);
    }
}
