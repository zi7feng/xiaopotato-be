package com.fzq.xiaopotato.model.dto.comment;

import lombok.Data;

import java.io.Serializable;

@Data
public class FirstCommentCreateDTO implements Serializable {
    private static final long serialVersionUID = -8780669399479191616L;
    private String content;

    private Long postId;

}
