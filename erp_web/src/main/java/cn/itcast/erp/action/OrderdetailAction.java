package cn.itcast.erp.action;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.exception.ErpException;

/**
 * 订单明细Action 
 * @author Administrator
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {

	private IOrderdetailBiz orderdetailBiz;

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}

	/** 仓库id */
	private Long storeuuid;

	public Long getStoreuuid() {
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	/**
	 * 入库
	 */
	public void doInStore() {
		Emp loginUser = getLoginUser();
		if(null == loginUser){
			ajaxReturn(false, "您还没有登陆或登录已过期");
			return;
		}
		try {
		    orderdetailBiz.doInStore(getId(),storeuuid,loginUser.getUuid());
		    ajaxReturn(true,"入库成功");
		}catch (Exception e){
			if(e instanceof ErpException) {
				ajaxReturn(false, e.getMessage());
				return;
			}
			e.printStackTrace();
			ajaxReturn(false, "入库失败");
		}

	}
	/**
	 * 出库
	 */
	public void doOutStore() {
		Emp loginUser = getLoginUser();
		if(null == loginUser){
			ajaxReturn(false, "您还没有登陆或登录已过期");
			return;
		}
		try {
			//执行出库
		    orderdetailBiz.doOutStore(getId(),storeuuid,loginUser.getUuid());
		    ajaxReturn(true,"出库成功");
		}catch (Exception e){
			if(e instanceof ErpException) {
				ajaxReturn(false, e.getMessage());
				return;
			}
			e.printStackTrace();
			ajaxReturn(false, "出库失败");
		}

	}

}
