package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User-Tag Relationship Table: stores the relationship between users and their custom tags
 * @TableName Usertag
 */
@TableName(value ="Usertag")
@Data
public class Usertag implements Serializable {
    /**
     * User ID
     */
    @TableField(value = "user_id")
    private Long userId;

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