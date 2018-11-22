package cn.itcast.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Storeoper;

import java.util.Calendar;

/**
 * 仓库操作记录数据访问类
 *
 * @author Administrator
 */
public class StoreoperDao extends BaseDao<Storeoper> implements IStoreoperDao {

    /**
     * 构建查询条件
     *
     * @param storeoper1
     * @param storeoper2
     * @param param
     * @return
     */
    public DetachedCriteria getDetachedCriteria(Storeoper storeoper1, Storeoper storeoper2, Object param) {
        DetachedCriteria dc = DetachedCriteria.forClass(Storeoper.class);
        if (storeoper1 != null) {
            //根据类型查询
            if (null != storeoper1.getType() && storeoper1.getType().trim().length() > 0) {
                dc.add(Restrictions.eq("type", storeoper1.getType()));
            }
            //根据商品查询
            if (null != storeoper1.getGoodsuuid()) {
                dc.add(Restrictions.eq("goodsuuid", storeoper1.getGoodsuuid()));
            }
            //根据仓库查询
            if (null != storeoper1.getStoreuuid()) {
                dc.add(Restrictions.eq("storeuuid", storeoper1.getStoreuuid()));
            }
            //根据员工查询
            if (null != storeoper1.getEmpuuid()) {
                dc.add(Restrictions.eq("empuuid", storeoper1.getEmpuuid()));
            }
            //操作时间
            if (null != storeoper1.getOpertime()) {
                //处理时间
                Calendar car = Calendar.getInstance();
                car.setTime(storeoper1.getOpertime());
                car.set(Calendar.HOUR, 0);
                car.set(Calendar.MINUTE, 0);
                car.set(Calendar.SECOND, 0);
                car.set(Calendar.MILLISECOND,0);
                dc.add(Restrictions.ge("opertime", car.getTime()));
            }
        }
        if (storeoper2 != null) {
            if (storeoper2.getOpertime() != null) {
                //处理时间
                Calendar car = Calendar.getInstance();
                car.setTime(storeoper2.getOpertime());
                car.set(Calendar.HOUR, 23);
                car.set(Calendar.MINUTE, 59);
                car.set(Calendar.SECOND, 59);
                car.set(Calendar.MILLISECOND,999);
                dc.add(Restrictions.le("opertime", car.getTime()));
            }
        }
        return dc;
    }

}
