package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IInventoryBiz;
import cn.itcast.erp.dao.*;
import cn.itcast.erp.entity.Inventory;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.entity.Storeoper;
import cn.itcast.erp.exception.ErpException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 盘盈盘亏业务逻辑类
 *
 * @author Administrator
 */
public class InventoryBiz extends BaseBiz<Inventory> implements IInventoryBiz {

    private IInventoryDao inventoryDao;
    private IEmpDao empDao;
    private IGoodsDao goodsDao;
    private IStoreDao storeDao;
    private IStoredetailDao storedetailDao;
    private IStoreoperDao storeoperDao;

    public void setStoreoperDao(IStoreoperDao storeoperDao) {
        this.storeoperDao = storeoperDao;
    }

    public void setStoredetailDao(IStoredetailDao storedetailDao) {
        this.storedetailDao = storedetailDao;
    }

    public void setEmpDao(IEmpDao empDao) {
        this.empDao = empDao;
    }

    public void setGoodsDao(IGoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    public void setStoreDao(IStoreDao storeDao) {
        this.storeDao = storeDao;
    }

    public void setInventoryDao(IInventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
        super.setBaseDao(this.inventoryDao);
    }

    @Override
    public void add(Inventory inventory) {
        //补全数据
        //1.设置创建时间
        inventory.setCreatetime(new Date());
        //2.设置状态
        inventory.setState(Inventory.STATE_CREATE);

        //如果为盘亏操作,校验盘亏数量是否大于库存
        Long num = inventory.getNum();
        if (inventory.getType().equals(Inventory.INVENTORY_LOSS)) {
            //构造查询条件
            Storedetail storedetail1 = new Storedetail();
            storedetail1.setGoodsuuid(inventory.getGoodsuuid());
            storedetail1.setStoreuuid(inventory.getStoreuuid());
            //查询
            List<Storedetail> list = storedetailDao.getList(storedetail1, null, null);
            if (list == null || list.size() < 1) {
                throw new ErpException("仓库中没有该商品信息");
            }
            Storedetail storedetail = list.get(0);
            //判断数量是否充足
            if (storedetail.getNum() - num < 0) {//不足
                throw new ErpException("登记失败,盘亏数量已超过仓库中该商品的剩余数量");
            }
        }

        //保存
        super.add(inventory);
    }

    @Override
    public List<Inventory> getListByPage(Inventory t1, Inventory t2, Object param, int firstResult, int maxResults) {
        List<Inventory> inventoryList = super.getListByPage(t1, t2, param, firstResult, maxResults);
        Map<Long, String> empNameMap = new HashMap<>();
        Map<Long, String> goodsNameMap = new HashMap<>();
        Map<Long, String> storeNameMap = new HashMap<>();
        for (Inventory inventory : inventoryList) {
            //设置审核员和登记员名称
            inventory.setCreaterName(getEmpName(inventory.getCreater(), empNameMap, empDao));
            inventory.setCheckerName(getEmpName(inventory.getChecker(), empNameMap, empDao));
            //设置商品名称
            inventory.setGoodsName(getGoodsName(inventory.getGoodsuuid(), goodsNameMap, goodsDao));
            //设置仓库名称
            inventory.setStoreName(getStoreName(inventory.getStoreuuid(), storeNameMap, storeDao));
        }

        return inventoryList;
    }

    @Override
    public void doCheck(long id, Long empId) {
        /******************************
         *      修改盘盈盘亏信息       *
         ******************************/
        //查询出盘盈盘亏记录
        Inventory inventory = inventoryDao.get(id);
        if (inventory == null) {
            throw new ErpException("盘盈盘亏记录不存在");
        }
        //校验状态
        if (!inventory.getState().equals(Inventory.STATE_CREATE)) {
            throw new ErpException("盘盈盘亏记录状态不合法");
        }
        //设置审核员
        inventory.setChecker(empId);
        //设置审核时间
        inventory.setChecktime(new Date());
        //设置状态
        inventory.setState(Inventory.STATE_CHECK);

        //获取操作数量
        Long num = inventory.getNum();
        //获取类型
        String type = inventory.getType();

        /******************************
         *      修改库存明细信息       *
         ******************************/
        //构造查询条件
        Storedetail storedetail1 = new Storedetail();
        storedetail1.setGoodsuuid(inventory.getGoodsuuid());
        storedetail1.setStoreuuid(inventory.getStoreuuid());
        //查询
        List<Storedetail> list = storedetailDao.getList(storedetail1, null, null);
        if (inventory.getType().equals(Inventory.INVENTORY_PROFIT)) {//盘盈
            //判断是否查询出此商品信息
            if (list == null || list.size() < 1) {
                throw new ErpException("仓库中无该商品的信息");
            } else {
                Storedetail storedetail = list.get(0);
                //数量累加
                storedetail.setNum(storedetail.getNum() + num);
                //保存
                storedetailDao.update(storedetail);
            }
        } else {//盘亏
            //判断是否查询出此商品信息
            if (list == null || list.size() < 1) {
                throw new ErpException("仓库中没有该商品信息");
            }
            //获取该库存明细
            Storedetail storedetail = list.get(0);
            //判断数量是否充足
            if (storedetail.getNum() - num < 0) {//不足
                throw new ErpException("审核失败,盘亏数量已超过仓库中该商品的剩余数量");
            }
            //充足则减去库存后进行保存
            storedetail.setNum(storedetail.getNum() - num);
            storedetailDao.update(storedetail);
        }

        /******************************
         *      查询库存变动记录       *
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
        storeoper.setEmpuuid(empId);
        storeoper.setOpertime(inventory.getChecktime());
        storeoper.setStoreuuid(inventory.getStoreuuid());
        storeoper.setGoodsuuid(inventory.getGoodsuuid());
        storeoper.setNum(inventory.getNum());
        if(inventory.getType().equals(Inventory.INVENTORY_PROFIT)) {
            storeoper.setType(Storeoper.TYPE_INVENTORY_PROFIT);
        } else if(inventory.getType().equals(Inventory.INVENTORY_LOSS)) {
            storeoper.setType(Storeoper.TYPE_INVENTORY_LOSS);
        } else {
            throw new ErpException("盘盈盘亏插入库存变更记录操作类型不合法");
        }

        //保存库存变更记录
        storeoperDao.add(storeoper);
    }
}
