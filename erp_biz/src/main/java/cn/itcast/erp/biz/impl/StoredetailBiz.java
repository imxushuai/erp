package cn.itcast.erp.biz.impl;

import cn.itcast.erp.util.MailUtil;
import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库库存业务逻辑类
 *
 * @author Administrator
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

    private IStoredetailDao storedetailDao;
    private IStoreDao storeDao;
    private IGoodsDao goodsDao;

    public void setStoreDao(IStoreDao storeDao) {
        this.storeDao = storeDao;
    }

    public void setGoodsDao(IGoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    public void setStoredetailDao(IStoredetailDao storedetailDao) {
        this.storedetailDao = storedetailDao;
        super.setBaseDao(this.storedetailDao);
    }

    /** 发送邮件相关参数 */
    private MailUtil mailUtil;
    private String to;
    private String subject;
    private String text;

    @Override
    public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult, int maxResults) {
        List<Storedetail> storedetailList = super.getListByPage(t1, t2, param, firstResult, maxResults);
        //创建商品名称和仓库名称缓存
        Map<Long, String> goodsNameMap = new HashMap<>();
        Map<Long, String> storeNameMap = new HashMap<>();
        //遍历仓库明细
        for (Storedetail sd : storedetailList) {
            //将获取到的商品名称和仓库名称设置给仓库明细
            sd.setGoodsName(getGoodsName(sd.getGoodsuuid(), goodsNameMap, goodsDao));
            sd.setStoreName(getStoreName(sd.getStoreuuid(), storeNameMap, storeDao));
        }

        return storedetailList;
    }

    @Override
    public List<Storealert> getStorealertList() {
        return storedetailDao.getStorealertList();
    }

    @Override
    public void sendStorealertMail() throws MessagingException {
        // 查看是否有库存预警的商品
        List<Storealert> list = storedetailDao.getStorealertList();
        int count = list == null ? 0 : list.size();
        if(count > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            // 发送邮件
            mailUtil.sendMail(to , subject.replace("[time]",
                    sdf.format(new Date())),
                    text.replace("[count]",String.valueOf(count)));
        }
    }


    public void setMailUtil(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }
}
