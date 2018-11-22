package cn.itcast.erp.VO;

import java.util.List;

/**
 * tree控件 实体
 * Author xushuai
 * Description
 */
public class TreeVO {

    private String id;
    private String text;
    private Boolean checked;
    private List<TreeVO> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public List<TreeVO> getChildren() {
        return children;
    }

    public void setChildren(List<TreeVO> children) {
        this.children = children;
    }
}

