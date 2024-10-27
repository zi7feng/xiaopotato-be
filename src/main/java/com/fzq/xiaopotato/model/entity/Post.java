package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Post Table
 * @TableName Post
 */
@TableName(value ="Post")
@Data
public class Post implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Post Title
     */
    @TableField(value = "post_title")
    private String postTitle;

    /**
     * Post Content
     */
    @TableField(value = "post_content")
    private String postContent;

    /**
     * post image
     */
    @TableField(value = "post_image")
    private String postImage;

    @TableField(value = "post_genre")
    private String postGenre;


    /**
     * Create Time
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * Update Time
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * Is Deleted? (1 - Deleted)
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}