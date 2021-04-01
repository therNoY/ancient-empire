package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
public class GameRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 房间号
     */
    @TableId(value = "room_id", type = IdType.INPUT)
    private String roomId;

    /**
     * 选择的地图
     */
    private String mapId;

    /**
     * 房间名字
     */
    private String roomName;

    /**
     * 玩家数
     */
    private Integer playerCount;

    /**
     * 加入玩家数
     */
    private Integer joinCount;

    /**
     * 房主
     */
    @TableField(strategy = FieldStrategy.IGNORED)
    private Integer roomOwner;

    /**
     * { key: "1", value: "公开" },
     * { key: "2", value: "私有" },
     */
    private Integer gameType;

    /**
     * 是否可以观战
     */
    private Integer obEnable;

    /**
     * 地图配置
     */
    private String mapConfig;

    /**
     * 创建玩家
     */
    private Integer creater;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String creatTimeShow;

    @TableField(exist = false)
    private String ready;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }
    public Integer getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(Integer joinCount) {
        this.joinCount = joinCount;
    }
    public Integer getRoomOwner() {
        return roomOwner;
    }

    public void setRoomOwner(Integer roomOwner) {
        this.roomOwner = roomOwner;
    }
    public Integer getCreater() {
        return creater;
    }

    public void setCreater(Integer creater) {
        this.creater = creater;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }


    public Integer getGameType() {
        return gameType;
    }

    public void setGameType(Integer gameType) {
        this.gameType = gameType;
    }

    public Integer getObEnable() {
        return obEnable;
    }

    public void setObEnable(Integer obEnable) {
        this.obEnable = obEnable;
    }

    public String getMapConfig() {
        return mapConfig;
    }

    public void setMapConfig(String mapConfig) {
        this.mapConfig = mapConfig;
    }


    public String getCreatTimeShow() {
        return creatTimeShow;
    }

    public void setCreatTimeShow(String creatTimeShow) {
        this.creatTimeShow = creatTimeShow;
    }

    public String getReady() {
        return ready;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "GameRoom{" +
        "roomId=" + roomId +
        ", mapId=" + mapId +
        ", roomName=" + roomName +
        ", playerCount=" + playerCount +
        ", joinCount=" + joinCount +
        ", roomOwner=" + roomOwner +
        ", creater=" + creater +
        ", createTime=" + createTime +
        "}";
    }
}
