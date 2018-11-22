package cn.itcast.erp.dao;

import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;

import java.util.List;

/**
 * 员工数据访问接口
 * @author Administrator
 *
 */
public interface IEmpDao extends IBaseDao<Emp>{

	/**
	 * 用户登陆
	 * @param username
	 * @param pwd
	 * @return
	 */
	Emp findByUsernameAndPwd(String username, String pwd);
	
	void updatePwd(Long uuid, String newPwd);

	List<Menu> getMenusByEmpuuid(Long uuid);
}
