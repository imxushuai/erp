package cn.itcast.erp.action;

import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 订单Action
 *
 * @author Administrator
 */
public class OrdersAction extends BaseAction<Orders> {

    private IOrdersBiz ordersBiz;

    public void setOrdersBiz(IOrdersBiz ordersBiz) {
        this.ordersBiz = ordersBiz;
        super.setBaseBiz(this.ordersBiz);
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
        List<Orderdetail> orderdetailList = JSON.parseArray(json, Orderdetail.class);
        Orders orders = getT();
        //设置订单创建者
        orders.setCreater(loginUser.getUuid());
        orders.setOrderdetailList(orderdetailList);
        try {
            ordersBiz.add(orders);
            ajaxReturn(true, "订单创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            ajaxReturn(false, "订单创建失败");
        }
    }

    /**
     * 审核订单
     */
    public void doCheck() {
        //获取登录用户
        Emp loginUser = getLoginUser();
        if (loginUser == null) {
            ajaxReturn(false, "您还没有登录或登录已过期");
            return;
        }
        try {
            ordersBiz.doCheck(getId(), loginUser.getUuid());
            ajaxReturn(true, "订单审核成功");
        } catch (Exception e) {
            if (!(e instanceof ErpException)) {
                ajaxReturn(false, "订单审核失败");
                e.printStackTrace();
            }
            ajaxReturn(false, e.getMessage());
        }
    }

    /**
     * 确认订单
     */
    public void doStart() {
        //获取登录用户
        Emp loginUser = getLoginUser();
        if (loginUser == null) {
            ajaxReturn(false, "您还没有登录或登录已过期");
            return;
        }
        try {
            ordersBiz.doStart(getId(), loginUser.getUuid());
            ajaxReturn(true, "订单确认成功");
        } catch (Exception e) {
            if (!(e instanceof ErpException)) {
                ajaxReturn(false, "订单确认失败");
                e.printStackTrace();
            }
            ajaxReturn(false, e.getMessage());
        }
    }

    /**
     * 我的采购订单
     */
    public void myListByPage() {
        if (null == getT1()) {
            setT1(new Orders());
        }
        Emp loginUser = getLoginUser();
        getT1().setCreater(loginUser.getUuid());
        super.listByPage();
    }

    /**
     * 导出指定订单
     */
    public void export() {
        String filename = "Orders_" + getId() + ".xls";
        //响应对象
        HttpServletResponse response = ServletActionContext.getResponse();
        try {
            //设置输出流,实现下载文件
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    new String(filename.getBytes(),"ISO-8859-1"));

            ordersBiz.export(response.getOutputStream(), getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
