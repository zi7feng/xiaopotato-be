package com.fzq.xiaopotato.model.dto.email;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailSendDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fromUser;
    private String toUser;
    private String subject;
    private String content;
}