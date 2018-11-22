package cn.itcast.erp.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import cn.itcast.erp.util.ViewUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionContext;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;

public class LoginAction {

    private String username;//登陆用户名
    private String pwd;//密码
    private IEmpBiz empBiz;


    public void checkUser() {
        try {
            /*
             * shiro认证
             */
            //1.创建令牌
            UsernamePasswordToken token = new UsernamePasswordToken();
            token.setUsername(username);
            token.setPassword(pwd.toCharArray());
            //2.创建操作主题
            Subject subject = SecurityUtils.getSubject();
            //
            /*
             * 3.执行login，这个方法会调用Realm中的认证方法进行认证
             *          认证成功，继续执行
             *          认证失败，则抛出异常
             */
            subject.login(token);
            //返回成功
            ajaxReturn(true, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            ajaxReturn(false, "登陆失败");
        }
    }

    /**
     * 显示登陆用户名
     */
    public void showName() {
        //从shiro操作主题中获取当前登陆的用户
        Subject subject = SecurityUtils.getSubject();
        Emp emp = (Emp) subject.getPrincipal();
        //session是否会超时，用户是否登陆过了
        if (null != emp) {
            ajaxReturn(true, emp.getName());
        } else {
            ajaxReturn(false, "");
        }
    }

    /**
     * 退出登陆
     */
    public void loginOut() {
        //执行shiro操作主题中的退出
        SecurityUtils.getSubject().logout();
    }

    /**
     * 返回前端操作结果
     *
     * @param success
     * @param message
     */
    public void ajaxReturn(boolean success, String message) {
        //返回前端的JSON数据
        Map<String, Object> rtn = new HashMap<String, Object>();
        rtn.put("success", success);
        rtn.put("message", message);
        //JSON.toJSONString(rtn) => {"success":true,"message":'超级管理员'}
        ViewUtils.write(JSON.toJSONString(rtn));
    }



    public void setEmpBiz(IEmpBiz empBiz) {
        this.empBiz = empBiz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
