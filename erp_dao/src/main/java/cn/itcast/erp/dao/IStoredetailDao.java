package cn.itcast.erp.dao;

import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;

import java.util.List;

/**
 * 仓库库存数据访问接口
 * @author Administrator
 *
 */
public interface IStoredetailDao extends IBaseDao<Storedetail>{

    /**
     * 获取库存预警列表(库存小于需要出库的数量)
     *
     * @return List<Storealert> 库存预警列表
     */
    List<Storealert> getStorealertList();

}
