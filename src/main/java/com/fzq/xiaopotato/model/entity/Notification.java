package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName Notification
 */
@TableName(value ="Notification")
@Data
public class Notification implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 
     */
    @TableField(value = "follower_id")
    private Long followerId;

    /**
     * 
     */
    @TableField(value = "first_name")
    private String firstName;

    /**
     * 
     */
    @TableField(value = "last_name")
    private String lastName;

    /**
     * 
     */
    @TableField(value = "account")
    private String account;

    /**
     * 
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 
     */
    @TableField(value = "notification_type")
    private String notificationType;

    /**
     * Timestamp when the follow action was created
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "is_read")
    private Integer isRead; // 使用 Integer 类型表示 0 = 未读, 1 = 已读

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}