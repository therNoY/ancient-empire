package com.mihao.ancient_empire.handle.end;

public class DeadKingEndHandle extends EndHandle{

    private static DeadKingEndHandle endHandle = null;

    public static DeadKingEndHandle instance() {
        if (endHandle == null) {
            endHandle = new DeadKingEndHandle();
        }
        return endHandle;
    }
}
