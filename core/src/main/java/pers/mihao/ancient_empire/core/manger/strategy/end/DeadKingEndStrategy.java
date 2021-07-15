package pers.mihao.ancient_empire.core.manger.strategy.end;

/**
 * @author mihao
 */
public class DeadKingEndStrategy extends EndStrategy {

    private static DeadKingEndStrategy endHandle = null;

    public static DeadKingEndStrategy instance() {
        if (endHandle == null) {
            endHandle = new DeadKingEndStrategy();
        }
        return endHandle;
    }
}
