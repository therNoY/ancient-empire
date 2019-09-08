package com.mihao.ancient_empire.constant;

public enum  UserEnum {

    ADMIN;

    public int getId() {
        if (this.equals(ADMIN)) {
            return 1;
        }
        return -1;
    }
}
