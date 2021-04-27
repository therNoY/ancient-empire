package pers.mihao.ancient_empire.core.manger;

import org.springframework.beans.factory.annotation.Value;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.IntegerUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * 用户模板的同用方法
 *
 * @version 1.0
 * @author mihao
 * @date 2020\10\7 0007 15:27
 */
public class UserTemplateHelper {

    private static int maxLevel = 12;
    private static Integer attachExp = 20;
    private static Integer antiAttackExp = 10;
    private static Integer killExp = 40;
    private static Integer antiKillExp = 40;
    private static Integer summonExp = 20;
    private static Integer DESTROYER_EXP = 30;
    private static UserTemplate defaultTemp = null;
    /**
     * 游戏模板
     */
    private UserTemplate userTemplate;

    /**
     * 晋升模式
     */
    public static final Integer NO = 0;
    public static final Integer USER_CHOOSE = 1;
    public static final Integer RANDOM = 2;
    public static final Integer COMMON = 3;


    private static int[] dp = new int[maxLevel];

    static {
        for (int i = 0; i < dp.length; i++) {
            if (i == 0) {
                dp[i] = 1;
            } else if (i == 1) {
                dp[i] = 2;
            } else {
                dp[i] = dp[i - 1] + dp[i - 2];
            }
        }

        defaultTemp = ApplicationContextHolder.getBean(UserTemplateService.class).getById(1);
    }

    public int getLevelExp(int level) {
        return dp[level] * 100;
    }

    public Integer getAttachExp() {
        return attachExp;
    }

    public Integer getAntiAttackExp() {
        return antiAttackExp;
    }

    public Integer getKillExp() {
        return killExp;
    }

    public Integer getAntiKillExp() {
        return antiKillExp;
    }

    /**
     * 获取召唤的经验
     * @return
     */
    public Integer getSummonExp() {
        return summonExp;
    }

    /**
     * 获取破化者的经验
     * @return
     */
    public Integer getDestroyerExp() {
        return DESTROYER_EXP;
    }

    public static void setSummonExp(Integer summonExp) {
        UserTemplateHelper.summonExp = summonExp;
    }


    public UserTemplate getUserTemplate() {
        return userTemplate;
    }

    public void setUserTemplate(UserTemplate userTemplate) {
        this.userTemplate = userTemplate;
    }


    /**
     * 获取单位的攻击（大于最小 小于最大）
     */
    public int getAttachNum(UnitLevelMes levelMes) {
        if (userTemplate.getAttachModel() == 1) {
            int min = levelMes.getMinAttack();
            int max = levelMes.getMaxAttack();
            return (min + max) / 2;
        } else {
            int min = levelMes.getMinAttack();
            int max = levelMes.getMaxAttack();
            return IntegerUtil.getRandomIn(min, max);
        }
    }

    public UserTemplate getDefaultTemplate() {
        return defaultTemp;
    }


    public int getTypePromotionCount() {
        return 1;
    }

    /**
     * 随机模式获取晋级机会
     *
     * @return
     */
    public boolean getRandomPromotionChance() {
        return UserTemplateHelper.COMMON.equals(userTemplate.getPromotionMode())
                || IntegerUtil.getRandomIn(10) < 5;
    }

    /**
     * 获取中毒者减少的生命
     * @return
     */
    public int getPoisonDesLife() {
        return 10;
    }

    /**
     *
     * 状态维持回合数
     * TODO 后期考虑全局buff  debuff数减少1  buff增加1
     * @return
     */
    public int getStatusRoundNum() {
        return 3;
    }

    /**
     * 获取这个坟墓存在的回合数
     */
    public int getTombPresenceNum() {
        return 3;
    }

    public int getChangeLifeByDestroyTomb() {
        return 10;
    }

    /**
     * 根据能力回血
     * @param ability
     * @return
     */
    public int getRecoverByAbility(AbilityEnum ability) {
        return 10;
    }

    /**
     * 获取等级提升的图片
     * @return
     */
    public String getLevelUpImg() {
        return StringUtil.isNotBlack(userTemplate.getLevelupAnimation())
                ? userTemplate.getLevelupAnimation() : defaultTemp.getLevelupAnimation();
    }

    public String getHeavenFuryImg(){
        return "1/heaven_fury_1.png,1/heaven_fury_2.png,1/heaven_fury_3.png,1/heaven_fury_4.png,";
    }
}
