package cn.itcast.erp.action;
import cn.itcast.erp.biz.IStoreBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Store;

/**
 * 仓库Action 
 * @author Administrator
 *
 */
public class StoreAction extends BaseAction<Store> {

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}

	/**
	 * 获取当前用户所管理的仓库
	 */
	public void myList() {
		if(null == getT1()) {
			setT1(new Store());
		}
		//取出当前登录用户
		Emp loginUser = getLoginUser();
		if(null == loginUser){
			ajaxReturn(false, "您还没有登陆或登录已过期");
			return;
		}
		getT1().setEmpuuid(loginUser.getUuid());
		super.list();
	}

}
