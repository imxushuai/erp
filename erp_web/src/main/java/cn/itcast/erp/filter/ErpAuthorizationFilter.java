package cn.itcast.erp.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义授权过滤器
 * Author xushuai
 * Description
 */
public class ErpAuthorizationFilter extends AuthorizationFilter {

    /**
     * 是否有访问权限
     *
     * @param servletRequest
	 * @param servletResponse
	 * @param mappedValue perms[]中的权限信息,可能为多个
     * @return boolean
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object mappedValue)
            throws Exception {
        //获取操作主题
        Subject subject = getSubject(servletRequest,servletResponse);
        //获取配置文件中的权限列表
        String[] permsList = (String[]) mappedValue;

        boolean isPermitted = true;
        if(permsList == null || permsList.length == 0) {
            return isPermitted;
        }
        //检查其权限
        if(permsList != null && permsList.length > 0) {
            for(String perms : permsList) {
                //只要满足一个权限信息,就返回true
                if(subject.isPermitted(perms)) {
                    return isPermitted;
                }
            }
        }

        return false;
    }
}

