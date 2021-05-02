package pers.mihao.ancient_empire.base.vo;

import pers.mihao.ancient_empire.base.entity.UnitMes;

/**
 * @Author mh32736
 * @Date 2021/4/29 20:14
 */
public class UnitMesVO extends UnitMes {

    /**
     * 下载次数
     */
    private String downLoadCount;

    /**
     * 星星总数
     */
    private String startCount;

    /**
     * 最新版本
     */
    private Integer maxVersion;

    private String createUserName;

    public String getDownLoadCount() {
        return downLoadCount;
    }

    public void setDownLoadCount(String downLoadCount) {
        this.downLoadCount = downLoadCount;
    }

    public String getStartCount() {
        return startCount;
    }

    public void setStartCount(String startCount) {
        this.startCount = startCount;
    }

    public Integer getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Integer maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
