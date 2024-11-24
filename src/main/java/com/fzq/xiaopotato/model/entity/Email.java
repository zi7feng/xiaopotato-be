package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName Email
 */
@TableName(value ="Email")
@Data
public class Email implements Serializable {
    /**
     * 
     */
    @TableId(value = "email_id", type = IdType.AUTO)
    private Long emailId;

    @TableField(value = "user_id")
    private Long userId;
    /**
     * From user
     */
    @TableField(value = "from_user")
    private String fromUser;

    /**
     * To user
     */
    @TableField(value = "to_user")
    private String toUser;

    /**
     * Subject of E-mail
     */
    @TableField(value = "subject")
    private String subject;

    /**
     * Content of E-mail
     */
    @TableField(value = "content")
    private String content;

    /**
     * Timestamp when the follow action was created
     */
    @TableField(value = "create_time")
    private Date createTime;


    /**
     * Indicates if the tag has been deleted (0 - Not deleted, 1 - Deleted)
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}