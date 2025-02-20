package com.fzq.xiaopotato.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * User table
 * @TableName User
 */
@TableName(value ="User")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * User First Name
     */
    @TableField(value = "first_name")
    private String firstName;

    /**
     * User Last Name
     */
    @TableField(value = "last_name")
    private String lastName;

    /**
     * User Account
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * User Avatar
     */
    @TableField(value = "user_avatar")
    private String userAvatar;

    /**
     * email
     */
    @TableField(value = "email")
    private String email;

    /**
     * phone
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * user Role：user / admin
     */
    @TableField(value = "user_role")
    private String userRole;

    /**
     * user password
     */
    @TableField(value = "user_password")
    private String userPassword;

    /**
     * user gender
     */
    @TableField(value = "gender")
    private String gender;

    @TableField(value = "description")
    private String description;

    /**
     * account status（0- normal 1- banned）
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * create time
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * update time
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * is delete? 1- deleted
     */
    @TableLogic
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}