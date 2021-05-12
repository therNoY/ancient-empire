package pers.mihao.ancient_empire.base.controller;


import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dto.UserMapIdDTO;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserMapAttention;
import pers.mihao.ancient_empire.base.service.UserMapAttentionService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mihao
 * @since 2021-04-26
 */
@RestController
public class UserMapAttentionController {

    @Autowired
    UserMapAttentionService userMapAttentionService;

    @Autowired
    UserMapService userMapService;

    /**
     * 下载用户地图
     * @param userMapAttention
     * @return
     */
    @PostMapping("/api/userMap/download")
    public RespJson downloadMap(@RequestBody UserMapAttention userMapAttention) {
        userMapAttention.setUserId(AuthUtil.getUserId());
        UserMap userMap = userMapService.getUserMapById(userMapAttention.getMapId());
        userMapAttention.setUpdateTime(LocalDateTime.now());
        userMapAttention.setDownloadTime(LocalDateTime.now());
        userMapAttention.setMapType(userMap.getMapType());
        userMapAttentionService.saveOrUpdate(userMapAttention);
        return RespUtil.successResJson();
    }

    /**
     * 删除下载用户地图
     * @param userMapAttention
     * @return
     */
    @PostMapping("/api/userMap/download/delete")
    public RespJson delDownloadMap(@RequestBody UserMapIdDTO mapIdDTO) {
        UserMapAttention userMapAttention = new UserMapAttention();
        userMapAttention.setUserId(mapIdDTO.getUserId());
        userMapAttention.setMapId(mapIdDTO.getMapId());
        userMapAttentionService.deleteByPrimaryKey(userMapAttention);
        return RespUtil.successResJson();
    }

    /**
     * 更新用户的模板
     * @param id
     * @return
     */
    @PostMapping("/api/userMap/version/update")
    public RespJson updateMaxVersion(@RequestBody UserMapIdDTO id) {
        userMapAttentionService.updateMaxVersion(id);
        return RespUtil.successResJson();
    }

}
