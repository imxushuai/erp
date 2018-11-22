package cn.itcast.erp.job;

import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.entity.Storealert;

import javax.mail.MessagingException;
import java.util.List;

/**
 * 后台定时检测库存
 * 当库存预警时,发送邮件
 *
 * Author xushuai
 * Description
 */
public class MailJob {
    /**
     * 商品库存业务
     */
    private IStoredetailBiz storedetailBiz;

    public void sendStorealertMail(){
        //查询是否存在库存预警
        List<Storealert> storealertList = storedetailBiz.getStorealertList();
        if(storealertList.size() > 0){
            try {
                //调用 业务发送预警邮件
                storedetailBiz.sendStorealertMail();
                System.out.println("发送邮件");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
        this.storedetailBiz = storedetailBiz;
    }

}

