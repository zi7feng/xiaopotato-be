package com.fzq.xiaopotato.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Post(Post Table)】的数据库操作Service
* @createDate 2024-10-23 14:21:47
*/
public interface PostService extends IService<Post> {

    Long postCreate(PostCreateDTO postCreateDTO, HttpServletRequest request);

    IPage<Post> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request);

    Post selectPostById(IdDTO idDTO, HttpServletRequest request);

    Boolean updatePostById(PostUpdateDTO postUpdateDTO, HttpServletRequest request);

    Boolean deletePostById(IdDTO idDTO, HttpServletRequest request);

    IPage<Post> listPostByUserId(PostQueryDTO postQueryDTO, HttpServletRequest request);


}
