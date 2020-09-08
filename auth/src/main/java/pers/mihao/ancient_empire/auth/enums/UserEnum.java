package pers.mihao.ancient_empire.auth.enums;

public enum  UserEnum {

    ADMIN;

    public int getId() {
        if (this.equals(ADMIN)) {
            return 1;
        }
        return -1;
    }
}
