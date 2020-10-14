package pers.mihao.ancient_empire.core.manger;

import org.springframework.beans.factory.annotation.Value;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.common.util.IntegerUtil;

/**
 * 用户模板的同用方法
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\7 0007 15:27
 */
public class UserTemplateHelper {

    private static int maxLevel = 12;
    @Value("${experience.attach}")
    private static Integer attachExp = 20;
    @Value("${experience.antiAttack}")
    private static Integer antiAttackExp = 10;
    @Value("${experience.kill}")
    private static Integer killExp = 40;
    @Value("${experience.antikill}")
    private static Integer antiKillExp = 40;

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
    }

    public int getLevelExp(int level) {
        return dp[level];
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
     * 游戏模板
     */
    private UserTemplate userTemplate;

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


}
