package com.fzq.xiaopotato.model.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 4052654175359402528L;

    /**
     * id
     */

    private Long id;


    private String firstName;

    private String lastName;


    /**
     * User Account
     */
    private String userAccount;

    /**
     * User Avatar
     */
    private String userAvatar;

    /**
     * email
     */
    private String email;

    private String phone;

    private String gender;

    private String description;


    private Integer followCount;

    private Integer fansCount;

    private boolean followed;


    /**
     * user Role：user / admin
     */
    private String userRole;


    /**
     * account status（0- normal 1- banned）
     */
    private Integer status;




}
