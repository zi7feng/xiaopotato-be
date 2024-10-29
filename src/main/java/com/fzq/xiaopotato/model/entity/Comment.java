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
 * @TableName Comment
 */
@TableName(value ="Comment")
@Data
public class Comment implements Serializable {
    /**
     * 
     */
    @TableId(value = "comment_id", type = IdType.AUTO)
    private Long commentId;

    /**
     * 
     */
    @TableField(value = "content")
    private String content;

    /**
     * 
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * Timestamp when the follow action was created
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 
     */
    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}