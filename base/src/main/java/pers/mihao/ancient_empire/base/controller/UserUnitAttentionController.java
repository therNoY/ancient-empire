package pers.mihao.ancient_empire.base.controller;


import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dto.ReqUnitIdDTO;
import pers.mihao.ancient_empire.base.dto.RequestDownUnitDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserUnitAttention;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserUnitAttentionService;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2021-04-29
 */
@RestController
public class UserUnitAttentionController {

    @Autowired
    UserUnitAttentionService userUnitAttentionService;

    @Autowired
    UnitMesService unitMesService;

    /**
     * 删除用户的单位
     *
     * @param id
     * @return
     */
    @DeleteMapping("/api/unitMes/download")
    public RespJson deleteUserDownloadUnit(@RequestParam Integer id) {
        userUnitAttentionService.deleteUserDownloadUnit(AuthUtil.getUserId(), id);
        return RespUtil.successResJson();
    }

    /**
     * 删除用户的单位
     *
     * @param id
     * @return
     */
    @PutMapping("/api/unitMes/download")
    public RespJson deleteUserDownloadUnit(@RequestBody RequestDownUnitDTO unitIdDTO) {
        UserUnitAttention attention = new UserUnitAttention();
        attention.setUserId(unitIdDTO.getUserId());
        attention.setUnitId(unitIdDTO.getUnitId());
        attention.setUnitCommend(unitIdDTO.getComment());
        attention.setUnitStart(unitIdDTO.getStart());
        attention.setUnitType(unitMesService.getUnitMesById(unitIdDTO.getUnitId()).getType());
        attention.setDownloadTime(LocalDateTime.now());
        attention.setUpdateTime(LocalDateTime.now());
        userUnitAttentionService.saveOrUpdate(attention);
        return RespUtil.successResJson();
    }

    /**
     * 删除用户的单位
     * @param id
     * @return
     */
    @PostMapping("/api/unitMes/version/update")
    public RespJson updateDownloadVersion(@RequestBody ReqUnitIdDTO id){

        // 查询已经下载的单位
        UserUnitAttention attention = new UserUnitAttention();
        attention.setUserId(id.getUserId());
        attention.setUnitId(id.getUnitId());
        UserUnitAttention userUnitAttention = userUnitAttentionService.selectByPrimaryKey(attention);

        if (userUnitAttention == null) {
            return RespUtil.error();
        }

        UnitMes unit = unitMesService.getUnitMesById(id.getUnitId());

        // 获取最新的发布版本单位
        UnitMes unitMes = unitMesService.getMaxVersionUnitByType(unit.getType());

        if (userUnitAttention.getUnitId().equals(unitMes.getId())) {
            return RespUtil.error("已经是最新版本 不需要更新");
        }
        // 更新单位
        userUnitAttentionService.deleteByPrimaryKey(attention);
        userUnitAttention.setUnitId(unitMes.getId());
        userUnitAttention.setUpdateTime(LocalDateTime.now());
        userUnitAttention.setUnitType(unitMes.getType());
        userUnitAttentionService.saveOrUpdate(userUnitAttention);
        return RespUtil.successResJson();
    }

    /**
     * 回退草稿版本
     * @param id
     * @return
     */
    @PostMapping("/api/unitMes/version/revert")
    public RespJson revertDraftVersion(@RequestBody ReqUnitIdDTO id) {
        UnitMes unitMes = unitMesService.getUnitMesById(id.getUnitId());

        if (unitMes.getCreateUserId().equals(id.getUserId())) {
            unitMesService.removeById(unitMes.getId());
            return RespUtil.successResJson();
        } else {
            return RespUtil.error("用户信息异常");
        }
    }

}
