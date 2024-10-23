package com.fzq.xiaopotato.model.dto.post;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class PostCreateDTO implements Serializable {

    private static final long serialVersionUID = -6949764339471632092L;
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

}
