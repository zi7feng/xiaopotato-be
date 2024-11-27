package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Tag Table: stores tags or keywords associated with posts or other entities
 * @TableName Tag
 */
@TableName(value ="Tag")
@Data
public class Tag implements Serializable {
    /**
     * Unique identifier for the tag
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Content of the tag, typically a keyword or label
     */
    @TableField(value = "content")
    private String content;

    /**
     * Timestamp when the tag was created
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * Timestamp when the tag was last updated
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * Indicates if the tag has been deleted (0 - Not deleted, 1 - Deleted)
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}