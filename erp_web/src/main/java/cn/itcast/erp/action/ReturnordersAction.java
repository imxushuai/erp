package cn.itcast.erp.action;

import cn.itcast.erp.biz.IReturnordersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Returnorderdetail;
import cn.itcast.erp.entity.Returnorders;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.List;

/**
 * 退货订单Action
 *
 * @author Administrator
 */
public class ReturnordersAction extends BaseAction<Returnorders> {

    private IReturnordersBiz returnordersBiz;

    public void setReturnordersBiz(IReturnordersBiz returnordersBiz) {
        this.returnordersBiz = returnordersBiz;
        super.setBaseBiz(this.returnordersBiz);
    }

    //接收formData中的json数据
    private String json;

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public void add() {
        //获取当前登录用户
        Emp loginUser = getLoginUser();
        if (loginUser == null) {
            ajaxReturn(false, "您还没有登录或登录已过期");
            return;
        }
        //将接收到的json串转换为订单详情列表
        List<Returnorderdetail> orderdetailList = JSON.parseArray(json, Returnorderdetail.class);
        Returnorders returnorders = getT();
        //设置订单创建者
        returnorders.setCreater(loginUser.getUuid());
        returnorders.setReturnorderdetailList(orderdetailList);
        try {
            //执行添加操作
            returnordersBiz.add(returnorders);
            ajaxReturn(true,"采购退订登记成功");
        } catch (Exception e) {
            if (e instanceof ErpException) {
                ajaxReturn(false, e.getMessage());
            }
            e.printStackTrace();
            ajaxReturn(false,"采购退订登记失败");
        }

    }

    /**
     * 审核退货订单
     */
    public void doCheck() {
        //获取当前登录用户
        Emp loginUser = getLoginUser();
        if (loginUser == null) {
            ajaxReturn(false, "您还没有登录或登录已过期");
            return;
        }
        try {
            //执行添加操作
            returnordersBiz.doCheck(getId(), loginUser.getUuid());
            ajaxReturn(true,"采购退订审核成功");
        } catch (Exception e) {
            if (e instanceof ErpException) {
                ajaxReturn(false, e.getMessage());
            }
            e.printStackTrace();
            ajaxReturn(false,"采购退订审核失败");
        }
    }

    /**
     * 我的采购订单
     */
    public void myListByPage() {
        if (null == getT1()) {
            setT1(new Returnorders());
        }
        Emp loginUser = getLoginUser();
        getT1().setCreater(loginUser.getUuid());
        super.listByPage();
    }
}
