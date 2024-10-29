package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName Postcomment
 */
@TableName(value ="Postcomment")
@Data
public class Postcomment implements Serializable {
    /**
     * 
     */
    @TableField(value = "comment_id")
    private Long commentId;

    /**
     * 
     */
    @TableField(value = "post_id")
    private Long postId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}