package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.RespHelper;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 单位信息表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@RestController
public class UnitMesController {

    @Autowired
    UnitMesService unitMesService;
    @Autowired
    UserRecordService userRecordService;

    /**
     * 获取 单位信息类表
     * @param pageSize
     * @param pageNow
     * @return
     */
    @GetMapping("/root/unit")
    public RespJson getUnitMesList(@RequestParam Long pageSize, @RequestParam Long pageNow) {
        Page<UnitMes> page = new Page<>(pageNow, pageSize);
        IPage<UnitMes> unitMesIPage = unitMesService.getList(page);
        return RespHelper.successPageResJson(unitMesIPage);
    }

    /**
     * 保存单位信息
     * @param unitMes
     * @param result
     * @return
     */
    @PutMapping("/root/unit")
    public RespJson saveUnit(@RequestBody @Validated UnitMes unitMes, BindingResult result) {
        unitMesService.saveUnitMes(unitMes);
        return RespHelper.successResJson();
    }

    /**
     * 获取一个单位详细信息
     * @param typeLevel
     * @return
     */
    @GetMapping("/unitInfo")
    public RespJson getUnitInfo(@RequestParam String typeLevel) {

        int index = typeLevel.lastIndexOf("_");
        if (index == 0) {
            return RespHelper.errResJson(40010);
        }
        String type = typeLevel.substring(0, index);
        Integer level = Integer.valueOf(typeLevel.substring(index + 1));
        UnitInfo unitInfo = unitMesService.getUnitInfo(type, level);
        return RespHelper.successResJson(unitInfo);
    }

    /**
     * 获取可购买的所有的单位的详细信息
     */
    @GetMapping("/unitInfo/list")
    public RespJson getUnitInfoList(@RequestParam String uuid) {
        UserRecord record = userRecordService.getRecordById(uuid);
        if (record == null) {
            return RespHelper.errResJson(40010);
        }
        Army army = AppUtil.getCurrentArmy(record);
        boolean hasLord = false;
        for (Unit unit : army.getUnits()) {
            if (unit.getType().equals(UnitEnum.LORD.type())) {
                hasLord = true;
                break;
            }
        }
        List<UnitInfo> unitInfoList = unitMesService.getUnitInfoList(hasLord);
        return RespHelper.successResJson(unitInfoList);
    }
}
