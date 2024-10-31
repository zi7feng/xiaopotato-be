package com.fzq.xiaopotato.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.common.PageDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.model.vo.PostVO;
import com.fzq.xiaopotato.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Operation(summary = "Create a new post")
    @PostMapping("/create")
    public BaseResponse<Long> createPost(@RequestBody PostCreateDTO postCreateDTO, HttpServletRequest request) {
        if (postCreateDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post info is null");
        }
        return ResultUtils.success(postService.postCreate(postCreateDTO, request));
    }

    @Operation(summary = "List posts with pagination")
    @GetMapping("/selectByPage")
    public BaseResponse<IPage<PostVO>> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        if (postQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (postQueryDTO.getCurrentPage() <= 0 || postQueryDTO.getPageSize() <= 0) {
            postQueryDTO.setCurrentPage(1);
            postQueryDTO.setPageSize(10);
        }
        IPage<PostVO> result = postService.listPostByPage(postQueryDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get post details by ID")
    @GetMapping("/selectById")
    public BaseResponse<PostVO> getPostById(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(postService.selectPostById(idDTO, request));
    }

    @Operation(summary = "Update a post by ID")
    @PostMapping("/update")
    public BaseResponse<Boolean> updatePostById(@RequestBody PostUpdateDTO postUpdateDTO, HttpServletRequest request) {

        boolean result = postService.updatePostById(postUpdateDTO, request);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Update post fail.");
        }
        return ResultUtils.success(true);
    }

    @Operation(summary = "Delete a post by ID")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePostById(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(postService.deletePostById(idDTO, request));
    }

    @Operation(summary = "List posts by user ID with pagination")
    @GetMapping("/selectByUserId")
    public BaseResponse<IPage<PostVO>> listPostByUserId(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        if (postQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (postQueryDTO.getCurrentPage() <= 0 || postQueryDTO.getPageSize() <= 0) {
            postQueryDTO.setCurrentPage(1);
            postQueryDTO.setPageSize(10);
        }
        IPage<PostVO> result = postService.listPostByUserId(postQueryDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get current user's liked posts by page")
    @GetMapping("/likes")
    public BaseResponse<IPage<PostVO>> listLikesByPage(PageDTO pageDTO, HttpServletRequest request) {
        if (pageDTO.getCurrentPage() <= 0 || pageDTO.getPageSize() <= 0) {
            pageDTO.setCurrentPage(1);
            pageDTO.setPageSize(10);
        }
        IPage<PostVO> result = postService.listLikesByPage(pageDTO, request);
        return ResultUtils.success(result);
    }

    @Operation(summary = "Get current user's saved posts by page")
    @GetMapping("/saves")
    public BaseResponse<IPage<PostVO>> listSavesByPage(PageDTO pageDTO, HttpServletRequest request) {
        if (pageDTO.getCurrentPage() <= 0 || pageDTO.getPageSize() <= 0) {
            pageDTO.setCurrentPage(1);
            pageDTO.setPageSize(10);
        }
        IPage<PostVO> result = postService.listSavesByPage(pageDTO, request);
        return ResultUtils.success(result);
    }


}
