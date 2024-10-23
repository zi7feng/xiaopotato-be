package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User-Post Relationship Table
 * @TableName UserPost
 */
@TableName(value ="UserPost")
@Data
public class UserPost implements Serializable {
    @TableField("user_id")
    private Long userId;

    @TableField("post_id")
    private Long postId;

    /**
     * Create Time
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}