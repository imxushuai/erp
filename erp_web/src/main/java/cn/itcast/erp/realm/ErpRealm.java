package cn.itcast.erp.realm;

import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

/**
 * erp系统认证
 * Author xushuai
 * Description
 */
public class ErpRealm extends AuthorizingRealm {

    private IEmpBiz empBiz;

    /**
     * 认证方法
     *
     * @param authenticationToken 需要被认证的令牌
     * @return org.apache.shiro.authc.AuthenticationInfo 认证结果
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        //我们这里使用的是username和password进行认证
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //调用业务层进行校验
        Emp user = empBiz.findByUsernameAndPwd(token.getUsername(), new String(token.getPassword()));
        //判断user是否存在
        if(user == null) {//用户不存在
            //认证失败
            return null;
        }
        /*
         * 参数1：将user带回，用于后续操作
         * 参数2：需要认证的密码，授权码
         * 参数3：当前realm的名称
         */
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, new String(token.getPassword()), getName());

        return info;
    }


    /**
     * 授权方法
     *
     * @param principalCollection
     * @return org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取当前登录用户的菜单权限
        Emp loginUser = (Emp) principalCollection.getPrimaryPrincipal();
        List<Menu> menuLis = empBiz.getMenusByEmpuuid(loginUser.getUuid());
        //加入到授权信息中
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for(Menu m : menuLis) {
            //将权限放入授权信息中
            info.addStringPermission(m.getMenuname());
        }

        return info;
    }


    public void setEmpBiz(IEmpBiz empBiz) {
        this.empBiz = empBiz;
    }

}

