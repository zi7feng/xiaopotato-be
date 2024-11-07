package com.fzq.xiaopotato.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class NotificationVO implements Serializable {

    private static final long serialVersionUID = 3081899075138319045L;
    private Long sourceId;
    private String firstName;
    private String lastName;
    private String account;
    private String avatar;

    private String notificationType;

    private String timestamp;
}
