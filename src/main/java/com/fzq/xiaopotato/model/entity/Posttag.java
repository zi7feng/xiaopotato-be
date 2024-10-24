package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Post-Tag Relationship Table: stores the many-to-many relationship between posts and tags
 * @TableName Posttag
 */
@TableName(value ="Posttag")
@Data
public class Posttag implements Serializable {
    /**
     * Post ID
     */
    @TableField(value = "post_id")
    private Long postId;

    /**
     * Tag ID
     */
    @TableField(value = "tag_id")
    private Long tagId;

    /**
     * Create Time
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}