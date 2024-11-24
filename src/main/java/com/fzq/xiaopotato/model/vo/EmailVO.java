package com.fzq.xiaopotato.model.vo;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EmailVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fromUser;
    private String toUser;
    private String subject;
    private String content;
    private Date createTime;
}
