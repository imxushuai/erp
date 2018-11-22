package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;

import javax.mail.MessagingException;
import java.util.List;

/**
 * 仓库库存业务逻辑层接口
 * @author Administrator
 *
 */
public interface IStoredetailBiz extends IBaseBiz<Storedetail>{

    /**
     * 获取库存预警列表(库存小于需要出库的数量)
     *
     * @return List<Storealert> 库存预警列表
     */
    List<Storealert> getStorealertList();

    /**
     * 发送库存预警邮件
     *
     */
    void sendStorealertMail() throws MessagingException;
}

