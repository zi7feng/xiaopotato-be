package com.fzq.xiaopotato.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/create")
    public BaseResponse<Long> createPost(@RequestBody PostCreateDTO postCreateDTO, HttpServletRequest request) {
        if (postCreateDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post info is null");
        }
        return ResultUtils.success(postService.postCreate(postCreateDTO, request));
    }

    @GetMapping("/selectByPage")
    public BaseResponse<IPage<Post>> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        if (postQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (postQueryDTO.getCurrentPage() <= 0 || postQueryDTO.getPageSize() <= 0) {
            postQueryDTO.setCurrentPage(1);
            postQueryDTO.setPageSize(10);
        }
        IPage<Post> result = postService.listPostByPage(postQueryDTO, request);
        return ResultUtils.success(result);
    }

    @GetMapping("/selectById")
    public BaseResponse<Post> getPostById(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(postService.selectPostById(idDTO, request));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updatePostById(@RequestBody PostUpdateDTO postUpdateDTO, HttpServletRequest request) {

        boolean result = postService.updatePostById(postUpdateDTO, request);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update post fail.");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePostById(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(postService.deletePostById(idDTO, request));
    }
}
