package cn.itcast.erp.biz;

import java.util.List;

import cn.itcast.erp.VO.TreeVO;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;

/**
 * 员工业务逻辑层接口
 *
 * @author Administrator
 */
public interface IEmpBiz extends IBaseBiz<Emp> {

    /**
     * 用户登陆
     *
     * @param username 用户名
     * @param pwd      密码
     * @return Emp 员工
     */
    Emp findByUsernameAndPwd(String username, String pwd);

    /**
     * 修改密码
     *
     * @param uuid   员工id
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     */
    void updatePwd(Long uuid, String oldPwd, String newPwd);

    /**
     * 重置密码
     *
     * @param uuid   员工id
     * @param newPwd 新密码
     */
    void updatePwd_reset(Long uuid, String newPwd);

    /**
     * 读取员工对应角色
     *
     * @param uuid 员工id
     * @return List<TreeVO>
     */
    List<TreeVO> readEmpRoleList(Long uuid);

    /**
     * 更新员工对应的角色
     *
     * @param uuid     员工id
     * @param checkeds 选中的角色id，以逗号分隔
     */
    void updateEmpRoleList(Long uuid, String checkeds);

    /**
     * 获取指定员工的菜单权限(仅包含二级菜单)
     *
     * @param uuid 员工id
     * @return java.util.List<cn.itcast.erp.entity.Menu>
     */
    List<Menu> getMenusByEmpuuid(Long uuid);

    /**
     * 获取员工对应的权限菜单(包含根菜单、一级、二级菜单)
     *
     * @param uuid 员工id
     * @return cn.itcast.erp.entity.Menu
     */
    Menu readMenusByEmpuuid(Long uuid);
}

