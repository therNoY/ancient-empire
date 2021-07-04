package pers.mihao.ancient_empire.core.manger.status_machine;

/**
 * 状态机变换接口
 * @Author mihao
 * @Date 2020/11/2 9:36
 */
public interface GameStatusMachine {

    void transferInit();

    void transferShowMoveArea();

    void transferShowMoveLine();

    void transferMoveDone();

    void transferShowAction();

    void transferWillAttach();

    void transferWillSummon();

    void transferWillAttachRegion();

}
