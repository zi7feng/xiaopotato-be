package com.fzq.xiaopotato.model.dto.post;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostQueryDTO implements Serializable {

    private static final long serialVersionUID = -860411473338993839L;
    private Integer currentPage = 1;
    private Integer pageSize = 10;
    private String postTitle;
    private String postContent;
    private String postGenre;
    private String sort;



}
