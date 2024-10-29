package com.fzq.xiaopotato.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.common.PageDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzq.xiaopotato.model.vo.PostVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Post(Post Table)】的数据库操作Service
* @createDate 2024-10-23 14:21:47
*/
public interface PostService extends IService<Post> {

    Long postCreate(PostCreateDTO postCreateDTO, HttpServletRequest request);

    IPage<PostVO> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request);

    PostVO selectPostById(IdDTO idDTO, HttpServletRequest request);

    Boolean updatePostById(PostUpdateDTO postUpdateDTO, HttpServletRequest request);

    Boolean deletePostById(IdDTO idDTO, HttpServletRequest request);

    IPage<PostVO> listPostByUserId(PostQueryDTO postQueryDTO, HttpServletRequest request);

    Integer getLikedCount(IdDTO idDTO);

    Integer getSavedCount(IdDTO idDTO);

    IPage<PostVO> listLikesByPage(PageDTO pageDTO, HttpServletRequest request);

    IPage<PostVO> listSavesByPage(PageDTO pageDTO, HttpServletRequest request);

}
