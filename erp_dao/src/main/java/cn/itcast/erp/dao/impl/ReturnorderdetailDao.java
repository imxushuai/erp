package cn.itcast.erp.dao.impl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import cn.itcast.erp.dao.IReturnorderdetailDao;
import cn.itcast.erp.entity.Returnorderdetail;
/**
 * 退货订单明细数据访问类
 * @author Administrator
 *
 */
public class ReturnorderdetailDao extends BaseDao<Returnorderdetail> implements IReturnorderdetailDao {

	/**
	 * 构建查询条件
	 * @param returnorderdetail1
	 * @param returnorderdetail2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Returnorderdetail returnorderdetail1,Returnorderdetail returnorderdetail2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Returnorderdetail.class);
		if(returnorderdetail1!=null){
			if(null != returnorderdetail1.getGoodsname() && returnorderdetail1.getGoodsname().trim().length()>0){
				dc.add(Restrictions.like("goodsname", returnorderdetail1.getGoodsname(), MatchMode.ANYWHERE));
			}
			if(null != returnorderdetail1.getState() && returnorderdetail1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", returnorderdetail1.getState()));
			}
			if(null != returnorderdetail1.getOrders() && returnorderdetail1.getOrders().getUuid() != null) {
				dc.add(Restrictions.eq("orders",returnorderdetail1.getOrders()));
			}

		}
		return dc;
	}

}
