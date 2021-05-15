package pers.mihao.ancient_empire.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UserTempAttentionService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;

/**
 * @Author mh32736
 * @Date 2021/5/6 22:11
 */
@RestController
public class UserTempAttentionController {

    @Autowired
    UserTemplateService userTemplateService;

    @Autowired
    UserTempAttentionService userTempAttentionService;

    /**
     * 下载模板
     *
     * @param userTempAttention
     * @return
     */
    @PostMapping("/api/userTemp/downloadTemp")
    public void downloadTemp(@RequestBody UserTempAttention userTempAttention) {
        UserTemplate template = userTemplateService.getTemplateById(userTempAttention.getTemplateId());
        userTempAttention.setUserId(LoginUserHolder.getUserId());
        userTempAttention.setCreateTime(LocalDateTime.now());
        userTempAttention.setTemplateType(template.getTemplateType());
        userTempAttentionService.saveOrUpdate(userTempAttention);
    }

    /**
     * 获取用户下载的模板
     *
     * @return
     */
    @PostMapping("/api/userAttentionTemp/page")
    public IPage<UserTemplateVO> getAttentionTemplateWithPage(@RequestBody ReqUserTemplateDTO reqUserTemplateDTO) {
        return userTempAttentionService.getAttentionTemplateWithPage(reqUserTemplateDTO);
    }


    /**
     * 删除用户下载模板
     *
     * @param id
     * @return
     */
    @DeleteMapping("/api/userTempAttention/{id}")
    public void removeUserTemplateAttention(@PathVariable("id") String id) {
        userTempAttentionService.removeUserAttention(LoginUserHolder.getUserId(), id);
    }

    /**
     * 更新用户的模板
     *
     * @param id
     * @return
     */
    @PostMapping("/api/userTempAttention/version/update")
    public void updateMaxVersion(@RequestBody TemplateIdDTO id) {
        userTempAttentionService.updateMaxVersion(id);
    }
}
