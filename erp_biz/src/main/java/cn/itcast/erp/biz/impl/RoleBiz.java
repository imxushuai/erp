package cn.itcast.erp.biz.impl;
import cn.itcast.erp.VO.TreeVO;
import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色业务逻辑类
 * @author Administrator
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {

	private IRoleDao roleDao;
	private IMenuDao menuDao;

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		super.setBaseDao(this.roleDao);
	}

	@Override
	public List<TreeVO> readRoleMenuList(Long uuid) {
		List<TreeVO> treeVOList = new ArrayList<>();
		//获取对应角色对应的权限列表
		List<Menu> roleMenuList = roleDao.get(uuid).getMenuList();
		//查出主节点
		Menu root = menuDao.get("0");

		//存放一级菜单
		TreeVO t1 = null;
		//存放二级菜单
		TreeVO t2 = null;
		for(Menu m1 : root.getMenus()) {
			t1 = new TreeVO();
			t1.setId(m1.getMenuid());
			t1.setText(m1.getMenuname());
			if(t1.getChildren() == null) {
				t1.setChildren(new ArrayList<TreeVO>());
			}
			//设置其二级菜单
			for(Menu m2 : m1.getMenus()) {
				t2 = new TreeVO();
				t2.setId(m2.getMenuid());
				t2.setText(m2.getMenuname());
				//如果该角色包含这权限,设置为选中
				if(roleMenuList.contains(m2)) {
					t2.setChecked(true);
				}
				//t2为t1的子节点
				t1.getChildren().add(t2);
			}
			//将t1保存到结果集中
			treeVOList.add(t1);
		}

		return treeVOList;
	}

	@Override
	public void updateRoleMenuList(Long uuid, String checkeds) {
		Role role = roleDao.get(uuid);
		//清空角色下全部权限
		role.setMenuList(new ArrayList<Menu>());
		//获取菜单中id数组
		String[] split = checkeds.split(",");
		Menu menu = null;
		for(String id : split) {
			menu = menuDao.get(id);
			//将权限重新设置给角色
			role.getMenuList().add(menu);
		}
	}
}
