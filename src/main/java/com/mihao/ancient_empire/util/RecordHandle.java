package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.entity.mongo.UserRecord;

public interface RecordHandle {

    void handle(UserRecord userRecord);
}
