package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.constant.MqMethodEnum;

public class MqMessage {
    private MqMethodEnum mqMethodEnum;
    private Object value;

    public MqMessage() {
    }

    public MqMessage(MqMethodEnum mqMethodEnum, Object value) {
        this.mqMethodEnum = mqMethodEnum;
        this.value = value;
    }

    public MqMethodEnum getMqMethodEnum() {
        return mqMethodEnum;
    }

    public void setMqMethodEnum(MqMethodEnum mqMethodEnum) {
        this.mqMethodEnum = mqMethodEnum;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
