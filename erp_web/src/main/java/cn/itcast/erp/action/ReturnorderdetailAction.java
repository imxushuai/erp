package cn.itcast.erp.action;

import cn.itcast.erp.biz.IReturnorderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.exception.ErpException;

/**
 * 退货订单明细Action
 *
 * @author Administrator
 */
public class ReturnorderdetailAction extends BaseAction<Returnorderdetail> {

    private IReturnorderdetailBiz returnorderdetailBiz;

    public void setReturnorderdetailBiz(IReturnorderdetailBiz returnorderdetailBiz) {
        this.returnorderdetailBiz = returnorderdetailBiz;
        super.setBaseBiz(this.returnorderdetailBiz);
    }

    //仓库id
    private Long storeuuid;

    public Long getStoreuuid() {
        return storeuuid;
    }

    public void setStoreuuid(Long storeuuid) {
        this.storeuuid = storeuuid;
    }

    /**
     * 出库
     */
    public void doOutStore() {
        Emp loginUser = getLoginUser();
        if (null == loginUser) {
            ajaxReturn(false, "您还没有登陆或登录已过期");
            return;
        }
        try {
            returnorderdetailBiz.doOutStore(getId(), storeuuid, loginUser.getUuid());
            ajaxReturn(true, "退货订单出库成功");
        } catch (Exception e) {
            if (e instanceof ErpException) {
                ajaxReturn(false, e.getMessage());
                return;
            }
            e.printStackTrace();
            ajaxReturn(false, "退货订单出库失败");
        }
    }

    /**
     * 入库
     */
    public void doInStore() {
        Emp loginUser = getLoginUser();
        if (null == loginUser) {
            ajaxReturn(false, "您还没有登陆或登录已过期");
            return;
        }
        try {
            returnorderdetailBiz.doInStore(getId(), storeuuid, loginUser.getUuid());
            ajaxReturn(true, "退货订单入库成功");
        } catch (Exception e) {
            if (e instanceof ErpException) {
                ajaxReturn(false, e.getMessage());
                return;
            }
            e.printStackTrace();
            ajaxReturn(false, "退货订单入库失败");
        }
    }
}
