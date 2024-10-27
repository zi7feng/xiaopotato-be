package com.fzq.xiaopotato.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PostVO implements Serializable {
    /**
     * ID
     */
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



    /**
     * Create Time
     */
    private Date createTime;

    /**
     * Update Time
     */
    private Date updateTime;

    private int likeCount;

    private int saveCount;

    private int commentCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
