package pers.mihao.ancient_empire.base.bo;

import java.io.Serializable;

/**
 * 这是一个方块基础类 所有的元素都继承自改类
 */
public abstract class Square implements Serializable {

    private static final long serialVersionUID = 1L;
    // 方块的ID
    protected Integer id;
    // 方块的名字
    protected String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
