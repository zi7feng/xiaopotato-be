package com.fzq.xiaopotato.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FirstCommentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long commentId;
    private String content;
    private Date createTime;
    private Long commentUserId;

    private Integer secondLevelCount;

    private String commentorFirstName;
    private String commentorLastName;
    private String commentorAccount;
    private String commentorAvatar;
}
