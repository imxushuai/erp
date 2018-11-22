package cn.itcast.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import cn.itcast.erp.dao.IInventoryDao;
import cn.itcast.erp.entity.Inventory;

import java.util.Calendar;

/**
 * 盘盈盘亏数据访问类
 *
 * @author Administrator
 */
public class InventoryDao extends BaseDao<Inventory> implements IInventoryDao {

    /**
     * 构建查询条件
     *
     * @param dep1
     * @param dep2
     * @param param
     * @return
     */
    public DetachedCriteria getDetachedCriteria(Inventory inventory1, Inventory inventory2, Object param) {
        DetachedCriteria dc = DetachedCriteria.forClass(Inventory.class);
        if (inventory1 != null) {
            if (null != inventory1.getType() && inventory1.getType().trim().length() > 0) {
                dc.add(Restrictions.eq("type", inventory1.getType()));
            }
            if (null != inventory1.getState() && inventory1.getState().trim().length() > 0) {
                dc.add(Restrictions.eq("state", inventory1.getState()));
            }
            if (null != inventory1.getRemark() && inventory1.getRemark().trim().length() > 0) {
                dc.add(Restrictions.eq("remark", inventory1.getRemark()));
            }
            //按登记时间范围查询
            if (null != inventory1.getCreatetime()) {
                //处理时间
                Calendar car = Calendar.getInstance();
                car.setTime(inventory1.getCreatetime());
                car.set(Calendar.HOUR, 0);
                car.set(Calendar.MINUTE, 0);
                car.set(Calendar.SECOND, 0);
                car.set(Calendar.MILLISECOND, 0);
                //封装查询条件
                dc.add(Restrictions.ge("createtime", car.getTime()));
            }
            //按审核时间范围查询
            if (null != inventory1.getChecktime()) {
                //处理时间
                Calendar car = Calendar.getInstance();
                car.setTime(inventory1.getChecktime());
                car.set(Calendar.HOUR, 0);
                car.set(Calendar.MINUTE, 0);
                car.set(Calendar.SECOND, 0);
                car.set(Calendar.MILLISECOND, 0);
                //封装查询条件
                dc.add(Restrictions.ge("checktime", car.getTime()));
            }
        }
        if (null != inventory2) {
            if (null != inventory2.getCreatetime()) {
                //处理时间
                Calendar car = Calendar.getInstance();
                car.setTime(inventory2.getCreatetime());
                car.set(Calendar.HOUR, 23);
                car.set(Calendar.MINUTE, 59);
                car.set(Calendar.SECOND, 59);
                car.set(Calendar.MILLISECOND, 999);
                //封装查询条件
                dc.add(Restrictions.le("createtime", car.getTime()));
            }
            if (null != inventory2.getChecktime()) {
                //处理时间
                Calendar car = Calendar.getInstance();
                car.setTime(inventory2.getChecktime());
                car.set(Calendar.HOUR, 23);
                car.set(Calendar.MINUTE, 59);
                car.set(Calendar.SECOND, 59);
                car.set(Calendar.MILLISECOND, 999);
                //封装查询条件
                dc.add(Restrictions.ge("checktime", car.getTime()));
            }
        }
        return dc;
    }

}
