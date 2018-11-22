package cn.itcast.erp.action;
import cn.itcast.erp.biz.IInventoryBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Inventory;
import cn.itcast.erp.exception.ErpException;

/**
 * 盘盈盘亏Action 
 * @author Administrator
 *
 */
public class InventoryAction extends BaseAction<Inventory> {

	private IInventoryBiz inventoryBiz;

	public void setInventoryBiz(IInventoryBiz inventoryBiz) {
		this.inventoryBiz = inventoryBiz;
		super.setBaseBiz(this.inventoryBiz);
	}

	@Override
	public void add() {
		//获取当前登录用户
		Emp loginUser = getLoginUser();
		if (loginUser == null) {
			ajaxReturn(false, "您还没有登录或登录已过期");
			return;
		}
		Inventory inventory = getT();
		inventory.setCreater(loginUser.getUuid());
		//调用add保存
		try {
			inventoryBiz.add(inventory);
			ajaxReturn(true, "盘盈盘亏登记成功");
		} catch (Exception e) {
			if (e instanceof ErpException) {
				ajaxReturn(false, e.getMessage());
				return;
			}
			e.printStackTrace();
			ajaxReturn(false, "盘盈盘亏登记失败");
		}
	}

	/**
	 * 审核盘盈盘亏
	 */
	public void doCheck() {
		//获取当前登录用户
		Emp loginUser = getLoginUser();
		if (loginUser == null) {
			ajaxReturn(false, "您还没有登录或登录已过期");
			return;
		}
		try {
			//执行审核操作
			inventoryBiz.doCheck(getId(), loginUser.getUuid());
			ajaxReturn(true, "审核成功");
		} catch (Exception e) {
			if (e instanceof ErpException) {
				ajaxReturn(false, e.getMessage());
				return;
			}
			e.printStackTrace();
			ajaxReturn(false, "审核失败");
		}

	}

}