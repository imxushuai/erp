package cn.itcast.erp.action;
import cn.itcast.erp.VO.TreeVO;
import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.util.ViewUtils;
import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;

	private String checkeds;



	/**
	 * 读取角色菜单
	 */
	public void readRoleMenuList() {
		List<TreeVO> treeVOList = roleBiz.readRoleMenuList(getId());
		ViewUtils.write(JSON.toJSONString(treeVOList));
	}

	/**
	 * 更新角色权限
	 */
	public void updateRoleMenuList() {
		try {
			roleBiz.updateRoleMenuList(getId(), checkeds);
			ajaxReturn(true, "权限更新成功");
		}catch (Exception e){
			ajaxReturn(false, "权限更新失败");
			e.printStackTrace();
		}
	}




	public String getCheckeds() {
		return checkeds;
	}

	public void setCheckeds(String checkeds) {
		this.checkeds = checkeds;
	}

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}

}
