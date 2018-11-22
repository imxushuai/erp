package cn.itcast.erp.biz;

import cn.itcast.erp.VO.TreeVO;
import cn.itcast.erp.entity.Role;

import java.util.List;

/**
 * 角色业务逻辑层接口
 *
 * @author Administrator
 */
public interface IRoleBiz extends IBaseBiz<Role> {

    /**
     * 读取角色菜单
     *
     * @return List<TreeVO>
     */
    List<TreeVO> readRoleMenuList(Long uuid);

    /**
     * 更新权限到对应角色
     *
     * @param uuid     角色id
     * @param checkeds 选中状态的菜单id字符串，以逗号分隔
     */
    void updateRoleMenuList(Long uuid, String checkeds);

}

