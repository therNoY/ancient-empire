package pers.mihao.ancient_empire.core.handel.end;

public class DeadKingEndHandle extends EndHandle{

    private static DeadKingEndHandle endHandle = null;

    public static DeadKingEndHandle instance() {
        if (endHandle == null) {
            endHandle = new DeadKingEndHandle();
        }
        return endHandle;
    }
}
