package cn.itcast.erp.action;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.entity.Store;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.util.ViewUtils;
import com.alibaba.fastjson.JSON;

import javax.mail.MessagingException;
import java.util.List;

/**
 * 仓库库存Action
 *
 * @author Administrator
 */
public class StoredetailAction extends BaseAction<Storedetail> {

    private IStoredetailBiz storedetailBiz;

    public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
        this.storedetailBiz = storedetailBiz;
        super.setBaseBiz(this.storedetailBiz);
    }

    /**
     * 库存预警
     */
    public void storealertList() {
        List<Storealert> storealertList = storedetailBiz.getStorealertList();
        ViewUtils.write(JSON.toJSONString(storealertList));
    }

    /**
     * 发送库存预警邮件
     */
    public void sendStorealertMail() {
        try {
            storedetailBiz.sendStorealertMail();
            ajaxReturn(true, "邮件发送成功");
        } catch (Exception e) {
            if (e instanceof MessagingException) {
                ajaxReturn(false, "邮件创建失败");
                e.printStackTrace();
                return;
            }
            ajaxReturn(false, "邮件发送失败");
            e.printStackTrace();
        }
    }
}
