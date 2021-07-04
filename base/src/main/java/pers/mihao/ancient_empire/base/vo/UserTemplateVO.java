package pers.mihao.ancient_empire.base.vo;

import java.util.List;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;

/**
 * @Author mihao
 * @Date 2021/5/6 13:35
 */
public class UserTemplateVO extends UserTemplate {


    private Integer startCount;

    /**
     * 引用次数
     */

    private Integer downLoadCount;

    /**
     * 模板绑定的单位
     */
    private List<UnitMes> bindUintList;

    /**
     * 最新版本
     */
    private Integer maxVersion;

    private String createUserName;

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getStartCount() {
        return startCount;
    }

    public void setStartCount(Integer startCount) {
        this.startCount = startCount;
    }

    public Integer getDownLoadCount() {
        return downLoadCount;
    }

    public void setDownLoadCount(Integer downLoadCount) {
        this.downLoadCount = downLoadCount;
    }

    public List<UnitMes> getBindUintList() {
        return bindUintList;
    }

    public void setBindUintList(List<UnitMes> bindUintList) {
        this.bindUintList = bindUintList;
    }

    public Integer getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Integer maxVersion) {
        this.maxVersion = maxVersion;
    }
}
