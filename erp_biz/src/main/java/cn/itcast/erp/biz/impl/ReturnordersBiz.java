package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IReturnordersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.exception.ErpException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 退货订单业务逻辑类
 * @author Administrator
 *
 */
public class ReturnordersBiz extends BaseBiz<Returnorders> implements IReturnordersBiz {

	private IReturnordersDao returnordersDao;
	
	public void setReturnordersDao(IReturnordersDao returnordersDao) {
		this.returnordersDao = returnordersDao;
		super.setBaseDao(this.returnordersDao);
	}

	private IEmpDao empDao;
	private ISupplierDao supplierDao;

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	@Override
	public void add(Returnorders returnorders) {
		//补全数据
		//1.设置退货订单状态
		returnorders.setState(Returnorders.STATE_CREATE);
		//2.设置退货订单创建时间
		returnorders.setCreatetime(new Date());
		//3.计算总金额
		double total = 0;
		for(Returnorderdetail returnorderdetail : returnorders.getReturnorderdetailList()) {
			total += returnorderdetail.getMoney();
			//设置退货订单状态
			returnorderdetail.setState(Returnorderdetail.STATE_NOT_OUT);
			//设置关联的退货订单
			returnorderdetail.setOrders(returnorders);
		}
		//设置总金额
		returnorders.setTotalmoney(total);
		//设置退货订单类型
		//returnorders.setType(Returnorders.TYPE_PURCHASE);

		//保存订单
		returnordersDao.add(returnorders);
	}

	@Override
	public List<Returnorders> getListByPage(Returnorders t1, Returnorders t2, Object param, int firstResult, int maxResults) {
		List<Returnorders> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		//缓存员工编号对应的员工名称
		Map<Long, String> empNameMap = new HashMap<>();
		//缓存供应商编号对应的供应商名称
		Map<Long, String> supplierNameMap = new HashMap<>();
		for (Returnorders orders : list) {
			//设置对应员工名称
			orders.setCreaterName(getEmpName(orders.getCreater(), empNameMap, empDao));
			orders.setCheckerName(getEmpName(orders.getChecker(), empNameMap, empDao));
			orders.setEnderName(getEmpName(orders.getEnder(), empNameMap, empDao));

			//设置对应供应商名称
			orders.setSupplierName(getSupplierName(orders.getSupplieruuid(), supplierNameMap, supplierDao));

		}

		return list;
	}

	@Override
	public void doCheck(long id, Long uuid) {
		//获取退货订单
		Returnorders returnorders = returnordersDao.get(id);
		if(returnorders == null) {
			throw new ErpException("退货订单不存在");
		}
		//修改订单状态
		returnorders.setState(Returnorders.STATE_CHECK);
		//修改订单审核员
		returnorders.setChecker(uuid);
		//修改订单审核时间
		returnorders.setChecktime(new Date());

		//保存退货订单
		returnordersDao.update(returnorders);
	}
}
