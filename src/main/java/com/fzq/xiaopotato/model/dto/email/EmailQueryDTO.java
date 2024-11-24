package com.fzq.xiaopotato.model.dto.email;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailQueryDTO implements Serializable {

    private static final long serialVersionUID = -860411473338993839L;
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private String subject;
    private String toUser;
    private String content;
}
