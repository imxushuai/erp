package cn.itcast.erp.biz.impl;

import cn.itcast.erp.VO.TreeVO;
import cn.itcast.erp.biz.IEmpBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.exception.ErpException;
import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.ArrayList;
import java.util.List;

/**
 * 员工业务逻辑类
 *
 * @author Administrator
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

    private int hashIterations = 2;

    private IEmpDao empDao;

    private IRoleDao roleDao;

    private IMenuDao menuDao;

    public void setMenuDao(IMenuDao menuDao) {
        this.menuDao = menuDao;
    }

    @Override
    public Emp findByUsernameAndPwd(String username, String pwd) {
        //查询前先加密
        pwd = encrypt(pwd, username);
        //System.out.println(pwd);
        return empDao.findByUsernameAndPwd(username, pwd);
    }

    @Override
    public void updatePwd(Long uuid, String oldPwd, String newPwd) {
        //取出员工信息
        Emp emp = empDao.get(uuid);
        //加密旧密码
        String encrypted = encrypt(oldPwd, emp.getUsername());
        //旧密码是否正确的匹配
        if (!encrypted.equals(emp.getPwd())) {
            //抛出 自定义异常
            throw new ErpException("旧密码不正确");
        }
        empDao.updatePwd(uuid, encrypt(newPwd, emp.getUsername()));
    }

    @Override
    public void add(Emp emp) {
        //String pwd = emp.getPwd();
        // source: 原密码
        // salt:   盐 =》扰乱码
        // hashIterations: 散列次数，加密次数
        //Md5Hash md5 = new Md5Hash(pwd, emp.getUsername(), hashIterations);
        //取出加密后的密码
        //设置初始密码
        String newPwd = encrypt(emp.getUsername(), emp.getUsername());
        //System.out.println(newPwd);
        //设置成加密后的密码
        emp.setPwd(newPwd);
        //保存到数据库中
        super.add(emp);
    }

    @Override
    public void updatePwd_reset(Long uuid, String newPwd) {
        //取出员工信息
        Emp emp = empDao.get(uuid);
        empDao.updatePwd(uuid, encrypt(newPwd, emp.getUsername()));
    }

    @Override
    public List<TreeVO> readEmpRoleList(Long uuid) {
        List<TreeVO> treeVOList = new ArrayList<>();
        //获取用户
        Emp emp = empDao.get(uuid);
        //获取该用户对应的角色
        List<Role> empRoleList = emp.getRoleList();
        if (empRoleList == null) {
            emp.setRoleList(new ArrayList<Role>());
        }
        //获取所有的角色
        List<Role> roleList = roleDao.getList(null, null, null);
        TreeVO t1 = null;
        for (Role role : roleList) {
            t1 = new TreeVO();
            t1.setId(role.getUuid().toString());
            t1.setText(role.getName());
            if (empRoleList.contains(role)) {
                t1.setChecked(true);
            }
            treeVOList.add(t1);
        }

        return treeVOList;
    }

    @Override
    public void updateEmpRoleList(Long uuid, String checkeds) {
        Emp emp = empDao.get(uuid);
        //清空该员工对应的角色列表
        emp.setRoleList(new ArrayList<Role>());
        //将checkeds切成角色id数组
        String[] split = checkeds.split(",");
        for (String id : split) {
            //通过id获得角色
            Role role = roleDao.get(Long.valueOf(id));
            //将角色重新设置给角色
            emp.getRoleList().add(role);
        }
    }


    /**
     * 加密
     *
     * @param source 原密码
     * @param salt   盐
     * @return String 加密后的密码
     */
    private String encrypt(String source, String salt) {
        Md5Hash md5 = new Md5Hash(source, salt, hashIterations);
        return md5.toString();
    }

    @Override
    public List<Menu> getMenusByEmpuuid(Long uuid) {
        return empDao.getMenusByEmpuuid(uuid);
    }

    @Override
    public Menu readMenusByEmpuuid(Long uuid) {
        //获取所有的菜单，做模板用的
        Menu root = menuDao.get("0");
        //用户下的菜单集合
        List<Menu> empMenus = empDao.getMenusByEmpuuid(uuid);
        //根菜单
        Menu menu = cloneMenu(root);

        //循环匹配模板
        //一级菜单
        Menu _m1 = null;
        //二级菜单
        Menu _m2 = null;
        for (Menu m1 : root.getMenus()) {
            _m1 = cloneMenu(m1);
            //二级菜单循环
            for (Menu m2 : m1.getMenus()) {
                //用户包含有这个菜单
                if (empMenus.contains(m2)) {
                    //复制菜单
                    _m2 = cloneMenu(m2);
                    //加入到上级菜单下
                    _m1.getMenus().add(_m2);
                }
            }
            //有二级菜单我们才加进来
            if (_m1.getMenus().size() > 0) {
                //把一级菜单加入到根菜单下
                menu.getMenus().add(_m1);
            }
        }
        return menu;
    }

    /**
     * 复制menu
     *
     * @param src 原菜单
     * @return Menu 新菜单
     */
    private Menu cloneMenu(Menu src) {
        Menu menu = new Menu();
        menu.setIcon(src.getIcon());
        menu.setMenuid(src.getMenuid());
        menu.setMenuname(src.getMenuname());
        menu.setUrl(src.getUrl());
        menu.setMenus(new ArrayList<Menu>());
        return menu;
    }


    public void setRoleDao(IRoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setEmpDao(IEmpDao empDao) {
        this.empDao = empDao;
        super.setBaseDao(this.empDao);
    }


}
