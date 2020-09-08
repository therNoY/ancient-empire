package pers.mihao.ancient_empire.robot.constant;

import com.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 分析局势的结果 是根据其他国家的状态的几个指标分析出来的
 *
 * 指标
 *      军队价值 = 现有军队的价值 + 剩余资金 + 占有的可生产地形
 *      战斗力 = 军队战斗力
 *      阵营价值 = 阵营的总军队价值
 *      阵营战斗力 = 阵营的总战斗力
 */
public enum SituationEnum implements BaseEnum {

    /**
     * 支配状态, 现在正处于统治地位
     *
     * 判断出处于支配状态后
     */
    DOMINATE,

    /**
     * 该状态表明现在是强势状态 可以分析出要进攻的区域然后购买军队 进攻
     * 可以选择两个点进行进攻
     */
    STRONG,

    /**
     *该状态表明是中等状态 会选择一路弱的势力 进攻，强的防守 不去打扰
     */
    MEDIUM,

    /**
     * 该状态表明现在是弱势状态, 需要在升级军队的时候保护
     * 自己的领土进行防守
     */
    WEAK,

    /**
     * 即将灭国状态
     */
    DESTROY
}
