package com.fzq.xiaopotato.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = 1620524957106567594L;
    private String firstName;

    private String lastName;

    /**
     * User Account
     */
    private String userAccount;


    private String userPassword;

    private String checkPassword;

    /**
     * email
     */
    private String email;
    private String phone;

    private String gender;




}
