package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
public class UserJoinRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    @TableId
    private Integer userId;

    /**
     * 房间号
     */
    private String roomId;

    /**
     * 加入的军队 为空表示观战
     */
    private String joinArmy;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getJoinArmy() {
        return joinArmy;
    }

    public void setJoinArmy(String joinArmy) {
        this.joinArmy = joinArmy;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    @Override
    public String toString() {
        return "UserJoinRoom{" +
        "userId=" + userId +
        ", roomId=" + roomId +
        "}";
    }
}
