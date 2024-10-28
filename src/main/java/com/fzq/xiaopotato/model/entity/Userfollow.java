package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User Follow Relationship Table
 * @TableName Userfollow
 */
@TableName(value ="Userfollow")
@Data
public class Userfollow implements Serializable {
    /**
     * ID of the user who is following
     */
    @TableField(value = "follower_id")
    private Long followerId;

    /**
     * ID of the user being followed
     */
    @TableField(value = "followed_id")
    private Long followedId;

    /**
     * Timestamp when the follow action was created
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}