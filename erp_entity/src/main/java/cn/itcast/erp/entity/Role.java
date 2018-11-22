package cn.itcast.erp.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * 角色实体类
 *
 * @author Administrator *
 */
public class Role {
    private Long uuid;//编号
    private String name;//名称

    @JSONField(serialize = false)
    private List<Menu> menuList;

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
