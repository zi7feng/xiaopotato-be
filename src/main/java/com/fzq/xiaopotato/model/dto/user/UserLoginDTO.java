package com.fzq.xiaopotato.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {
    private static final long serialVersionUID = -7958497045644841717L;
    /**
     * User Account
     */
    private String userAccount;


    private String userPassword;
}
