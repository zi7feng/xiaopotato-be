package com.fzq.xiaopotato.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDTO implements Serializable {
    private static final long serialVersionUID = -6371645406801302560L;
    private Long id;
    /**
     * User Avatar
     */
    private String userAvatar;


    /**
     * email
     */
    private String email;

    private String phone;

    private String description;

    private String gender;
}
