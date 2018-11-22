package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IDepBiz;
import cn.itcast.erp.dao.IDepDao;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.entity.Dep;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.exception.ErpException;

import java.util.List;

/**
 * 部门业务逻辑类
 * @author Administrator
 *
 */
public class DepBiz extends BaseBiz<Dep> implements IDepBiz {

	private IDepDao depDao;
	public void setDepDao(IDepDao depDao) {
		this.depDao = depDao;
		super.setBaseDao(this.depDao);
	}

	private IEmpDao empDao;
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	@Override
	public void delete(Long uuid) {
		//封装emp和dep对象
		Emp emp = new Emp();
		Dep dep = new Dep();
		dep.setUuid(uuid);
		emp.setDep(dep);
		//查询当前部门下是否存在员工
		List<Emp> list = empDao.getList(emp, null, null);
		//如果没有员工执行删除操作
		if(null == list || list.size() < 1) {
			depDao.delete(uuid);
		}else{
			throw new ErpException("当前部门下有员工存在，不能删除");
		}
	}
}
