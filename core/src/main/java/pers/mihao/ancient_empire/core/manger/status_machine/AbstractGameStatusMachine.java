package pers.mihao.ancient_empire.core.manger.status_machine;

import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;

/**
 * @Author mh32736
 * @Date 2020/11/2 9:46
 */
public abstract class AbstractGameStatusMachine implements GameStatusMachine {

    private StatusMachineEnum statusMachineEnum;

    private SubStatusMachineEnum subStatusMachineEnum;


}
