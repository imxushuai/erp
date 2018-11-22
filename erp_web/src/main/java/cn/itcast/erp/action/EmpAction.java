package cn.itcast.erp.action;

import cn.itcast.erp.VO.TreeVO;
import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.util.ViewUtils;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 员工Action
 *
 * @author Administrator
 */
public class EmpAction extends BaseAction<Emp> {

    private IEmpBiz empBiz;

    private String oldPwd;//旧密码
    private String newPwd;//新密码

    /** 被选中的角色id */
    private String checkeds;


    /**
     * 修改密码调用的方法
     */
    public void updatePwd() {
        Emp loginUser = getLoginUser();
        //session是否会超时，用户是否登陆过了
        if (null == loginUser) {
            ajaxReturn(false, "亲，您还没有登陆");
            return;
        }
        try {
            empBiz.updatePwd(loginUser.getUuid(), oldPwd, newPwd);
            ajaxReturn(true, "修改密码成功");
        } catch (Exception e) {
            e.printStackTrace();
            ajaxReturn(false, "修改密码失败");
        }
    }

    /**
     * 重置密码调用的方法
     */
    public void updatePwd_reset() {

        try {
            empBiz.updatePwd_reset(getId(), newPwd);
            ajaxReturn(true, "重置密码成功");
        } catch (Exception e) {
            e.printStackTrace();
            ajaxReturn(false, "重置密码失败");
        }
    }

    /**
     * 获取员工对应的角色信息
     */
    public void readEmpRoleList() {
        List<TreeVO> treeVOList = empBiz.readEmpRoleList(getId());
        ViewUtils.write(JSON.toJSONString(treeVOList));
    }

    /**
     * 更新员工对应的角色信息
     */
    public void updateEmpRoleList() {
        try {
            empBiz.updateEmpRoleList(getId(), checkeds);
            ajaxReturn(true, "角色更新成功");
        }catch (Exception e){
            ajaxReturn(false, "角色更新失败");
            e.printStackTrace();
        }
    }

    /**
     * 获取用户的菜单权限
     */
    public void getMenusByEmpuuid(){
        if(null != getLoginUser()){
            List<Menu> menuList = empBiz.getMenusByEmpuuid(getLoginUser().getUuid());
            ViewUtils.write(JSON.toJSONString(menuList));
        }
    }




    public String getCheckeds() {
        return checkeds;
    }

    public void setCheckeds(String checkeds) {
        this.checkeds = checkeds;
    }

    public void setEmpBiz(IEmpBiz empBiz) {
        this.empBiz = empBiz;
        super.setBaseBiz(this.empBiz);
    }


    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

}
