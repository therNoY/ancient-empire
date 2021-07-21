package pers.mihao.ancient_empire.core.controller.dto;

import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;

/**
 * @Author mihao
 * @Date 2021/7/6 17:17
 */
public class GetMonitorOutDTO extends UserRecord {

    /**
     * 状态机
     */
    private StatusMachineEnum statusMachine;

    /**
     * 子状态
     */
    private SubStatusMachineEnum subStatusMachine;

    public StatusMachineEnum getStatusMachine() {
        return statusMachine;
    }

    public void setStatusMachine(StatusMachineEnum statusMachine) {
        this.statusMachine = statusMachine;
    }

    public SubStatusMachineEnum getSubStatusMachine() {
        return subStatusMachine;
    }

    public void setSubStatusMachine(SubStatusMachineEnum subStatusMachine) {
        this.subStatusMachine = subStatusMachine;
    }
}
