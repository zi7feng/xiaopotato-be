package com.fzq.xiaopotato.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SecondCommentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long commentId;
    private String content;
    private Date createTime;

    private Long replyToUserId;
    private String replyToFirstName;
    private String replyToLastName;
    private String replyToAccount;
    private String replyToAvatar;


    private Long commentUserId;
    private String commentorFirstName;
    private String commentorLastName;
    private String commentorAccount;
    private String commentorAvatar;
}
