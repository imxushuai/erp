package cn.itcast.erp.dao.impl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.entity.Returnorders;
/**
 * 退货订单数据访问类
 * @author Administrator
 *
 */
public class ReturnordersDao extends BaseDao<Returnorders> implements IReturnordersDao {

	/**
	 * 构建查询条件
	 * @param returnorders1
	 * @param returnorders2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Returnorders returnorders1,Returnorders returnorders2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Returnorders.class);
		if(returnorders1!=null){
			if(null != returnorders1.getType() && returnorders1.getType().trim().length()>0){
				dc.add(Restrictions.eq("type", returnorders1.getType()));
			}
			if(null != returnorders1.getState() && returnorders1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", returnorders1.getState()));
			}
			if(null != returnorders1.getCreater()) {
				dc.add(Restrictions.eq("creater",returnorders1.getCreater()));
			}

		}
		return dc;
	}

}
