package pers.mihao.ancient_empire.base.vo;

import java.io.Serializable;
import pers.mihao.ancient_empire.base.entity.UserMap;

/**
 * 地图的基础信息
 *
 * @author mihao
 * @version 1.0
 * @date 2020\9\20 0020 11:18
 */
public class BaseMapInfoVO extends UserMap {

    private String mapId;

    private Integer startCount;

    private Integer downLoadCount;

    private String createUserName;

    private String templateName;

    private Integer maxVersion;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
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

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(Integer maxVersion) {
        this.maxVersion = maxVersion;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
}
