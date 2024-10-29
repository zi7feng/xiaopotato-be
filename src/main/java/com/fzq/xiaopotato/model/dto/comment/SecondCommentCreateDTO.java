package com.fzq.xiaopotato.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class SecondCommentCreateDTO implements Serializable {
    private static final long serialVersionUID = -9220373048561792181L;
    private String content;

    private Long postId;

    private Long commentId; // reply to which comment

}
