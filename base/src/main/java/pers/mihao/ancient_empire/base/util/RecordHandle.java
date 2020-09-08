package pers.mihao.ancient_empire.base.util;

import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

public interface RecordHandle {

    void handle(UserRecord userRecord);
}
