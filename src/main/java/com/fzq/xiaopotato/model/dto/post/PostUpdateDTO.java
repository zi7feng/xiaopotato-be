package com.fzq.xiaopotato.model.dto.post;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostUpdateDTO implements Serializable {
    private static final long serialVersionUID = -164615953534119381L;

    private Long id;
    /**
     * Post Title
     */
    private String postTitle;

    /**
     * Post Content
     */
    private String postContent;

    /**
     * post image
     */
    private String postImage;
    private String postGenre;

    private Integer imageWidth;
    private Integer imageHeight;

}
