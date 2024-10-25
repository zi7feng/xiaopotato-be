package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName Saves
 */
@TableName(value ="Saves")
@Data
public class Saves implements Serializable {
    /**
     * User ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * Post ID
     */
    @TableField(value = "post_id")
    private Long postId;

    /**
     * Create Time
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}