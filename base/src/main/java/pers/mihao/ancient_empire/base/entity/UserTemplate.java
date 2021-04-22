package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public class UserTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 版本
     */
//    @TableId
    private Integer version;

    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板描述
     */
    private String templateDesc;

    /**
     * 攻击产生的经验
     */
    private Integer attachExperience;

    /**
     * 反击产生的经验
     */
    private Integer counterattackExperience;

    /**
     * 杀死敌军产生的经验
     */
    private Integer killExperience;

    /**
     * 反杀产生的经验
     */
    private Integer antikillExperience;

    /**
     * 攻击的动画
     */
    private String attachAnimation;

    /**
     * 单位死亡动画
     */
    private String deadAnimation;

    /**
     * 召唤坟墓的动画
     */
    private String summonAnimation;

    /**
     * 升级动画
     */
    private String levelupAnimation;

    /**
     * 方块的宽度
     */
    private Integer squareWidth;

    /**
     * 方块的高度
     */
    private Integer squareHeight;

    /**
     * 攻击模式(1 固定 2 随机)
     */
    private Integer attachModel;

    /**
     * 衍生物Id
     */
    private Integer derivativeId;

    /**
     * 单位的最大等级
     */
    private Integer unitMaxLevel;

    /**
     * 可以晋升的最大数量
     */
    private Integer promotionMaxNum;

    /**
     * 晋升的最小等级
     */
    private Integer promotionLevel;

    /**
     * 晋升的模式(0不开启 1 用户选择升级 2 随机转职 3 固定)
     */
    private Integer promotionMode;

    /**
     * 是否共享
     */
    private Integer shared;

    /**
     * 模板状态 -1 废弃 0 草稿 1正常
     */
    private Integer status;

    /**
     * 引用总start
     */
    @TableField(exist = false)
    private Integer countStart;

    /**
     * 引用次数
     */
    @TableField(exist = false)
    private Integer linkNum;

    /**
     * 模板绑定的单位
     */
    @TableField(exist = false)
    private List<UnitMes> bindUintList;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    public String getTemplateDesc() {
        return templateDesc;
    }

    public void setTemplateDesc(String templateDesc) {
        this.templateDesc = templateDesc;
    }
    public Integer getAttachExperience() {
        return attachExperience;
    }

    public void setAttachExperience(Integer attachExperience) {
        this.attachExperience = attachExperience;
    }
    public Integer getCounterattackExperience() {
        return counterattackExperience;
    }

    public void setCounterattackExperience(Integer counterattackExperience) {
        this.counterattackExperience = counterattackExperience;
    }
    public Integer getKillExperience() {
        return killExperience;
    }

    public void setKillExperience(Integer killExperience) {
        this.killExperience = killExperience;
    }
    public Integer getAntikillExperience() {
        return antikillExperience;
    }

    public void setAntikillExperience(Integer antikillExperience) {
        this.antikillExperience = antikillExperience;
    }
    public String getAttachAnimation() {
        return attachAnimation;
    }

    public Integer getDerivativeId() {
        return derivativeId;
    }

    public void setDerivativeId(Integer derivativeId) {
        this.derivativeId = derivativeId;
    }

    public String getDeadAnimation() {
        return deadAnimation;
    }

    public void setDeadAnimation(String deadAnimation) {
        this.deadAnimation = deadAnimation;
    }

    public void setAttachAnimation(String attachAnimation) {
        this.attachAnimation = attachAnimation;
    }
    public String getSummonAnimation() {
        return summonAnimation;
    }

    public void setSummonAnimation(String summonAnimation) {
        this.summonAnimation = summonAnimation;
    }
    public String getLevelupAnimation() {
        return levelupAnimation;
    }

    public void setLevelupAnimation(String levelupAnimation) {
        this.levelupAnimation = levelupAnimation;
    }
    public Integer getSquareWidth() {
        return squareWidth;
    }

    public void setSquareWidth(Integer squareWidth) {
        this.squareWidth = squareWidth;
    }
    public Integer getSquareHeight() {
        return squareHeight;
    }

    public void setSquareHeight(Integer squareHeight) {
        this.squareHeight = squareHeight;
    }
    public Integer getAttachModel() {
        return attachModel;
    }

    public void setAttachModel(Integer attachModel) {
        this.attachModel = attachModel;
    }
    public Integer getUnitMaxLevel() {
        return unitMaxLevel;
    }

    public void setUnitMaxLevel(Integer unitMaxLevel) {
        this.unitMaxLevel = unitMaxLevel;
    }
    public Integer getPromotionMaxNum() {
        return promotionMaxNum;
    }

    public void setPromotionMaxNum(Integer promotionMaxNum) {
        this.promotionMaxNum = promotionMaxNum;
    }
    public Integer getPromotionLevel() {
        return promotionLevel;
    }

    public void setPromotionLevel(Integer promotionLevel) {
        this.promotionLevel = promotionLevel;
    }
    public Integer getPromotionMode() {
        return promotionMode;
    }

    public void setPromotionMode(Integer promotionMode) {
        this.promotionMode = promotionMode;
    }

    public Integer getCountStart() {
        return countStart;
    }

    public void setCountStart(Integer countStart) {
        this.countStart = countStart;
    }

    public Integer getLinkNum() {
        return linkNum;
    }

    public void setLinkNum(Integer linkNum) {
        this.linkNum = linkNum;
    }

    public Integer getShared() {
        return shared;
    }

    public void setShared(Integer shared) {
        this.shared = shared;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UnitMes> getBindUintList() {
        return bindUintList;
    }

    public void setBindUintList(List<UnitMes> bindUintList) {
        this.bindUintList = bindUintList;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "UserTemplate{" +
        "id=" + id +
        ", userId=" + userId +
        ", templateName=" + templateName +
        ", templateDesc=" + templateDesc +
        ", attachExperience=" + attachExperience +
        ", counterattackExperience=" + counterattackExperience +
        ", killExperience=" + killExperience +
        ", antikillExperience=" + antikillExperience +
        ", attachAnimation=" + attachAnimation +
        ", summonAnimation=" + summonAnimation +
        ", levelupAnimation=" + levelupAnimation +
        ", squareWidth=" + squareWidth +
        ", squareHeight=" + squareHeight +
        ", attachModel=" + attachModel +
        ", unitMaxLevel=" + unitMaxLevel +
        ", promotionMaxNum=" + promotionMaxNum +
        ", promotionLevel=" + promotionLevel +
        ", promotionMode=" + promotionMode +
        "}";
    }
}
