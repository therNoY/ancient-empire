package pers.mihao.ancient_empire.base.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public class UserTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

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
     * 初始资金
     */
    private Integer initMoney;

    /**
     * 最大人口
     */
    private Integer maxPop;

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
     * 召唤坟墓的动画
     */
    private String summonAnimation;

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
     * 晋升的模式(1用户选择 2随机 3固定)
     */
    private Integer promotionModle;

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
    public Integer getInitMoney() {
        return initMoney;
    }

    public void setInitMoney(Integer initMoney) {
        this.initMoney = initMoney;
    }
    public Integer getMaxPop() {
        return maxPop;
    }

    public void setMaxPop(Integer maxPop) {
        this.maxPop = maxPop;
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

    public void setAttachAnimation(String attachAnimation) {
        this.attachAnimation = attachAnimation;
    }
    public String getSummonAnimation() {
        return summonAnimation;
    }

    public void setSummonAnimation(String summonAnimation) {
        this.summonAnimation = summonAnimation;
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
    public Integer getPromotionModle() {
        return promotionModle;
    }

    public void setPromotionModle(Integer promotionModle) {
        this.promotionModle = promotionModle;
    }

    @Override
    public String toString() {
        return "UserTemplate{" +
        "id=" + id +
        ", userId=" + userId +
        ", templateName=" + templateName +
        ", templateDesc=" + templateDesc +
        ", initMoney=" + initMoney +
        ", maxPop=" + maxPop +
        ", attachExperience=" + attachExperience +
        ", counterattackExperience=" + counterattackExperience +
        ", killExperience=" + killExperience +
        ", antikillExperience=" + antikillExperience +
        ", attachAnimation=" + attachAnimation +
        ", summonAnimation=" + summonAnimation +
        ", squareWidth=" + squareWidth +
        ", squareHeight=" + squareHeight +
        ", attachModel=" + attachModel +
        ", unitMaxLevel=" + unitMaxLevel +
        ", promotionMaxNum=" + promotionMaxNum +
        ", promotionLevel=" + promotionLevel +
        ", promotionModle=" + promotionModle +
        "}";
    }
}
