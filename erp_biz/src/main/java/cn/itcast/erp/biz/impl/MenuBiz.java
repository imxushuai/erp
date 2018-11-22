package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;

import java.util.List;

/**
 * 菜单业务逻辑类
 * @author Administrator
 *
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	private IMenuDao menuDao;

	private IEmpDao empDao;

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		super.setBaseDao(this.menuDao);
	}

}
