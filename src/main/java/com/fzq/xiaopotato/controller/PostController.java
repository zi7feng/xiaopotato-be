package com.fzq.xiaopotato.controller;

import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.ResultUtils;
import com.fzq.xiaopotato.common.UploadUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/upload")
    public BaseResponse<String> upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "File is null.");
        }
        return ResultUtils.success(UploadUtils.uploadImage(file));
    }

    @PostMapping("/create")
    public BaseResponse<Long> createPost(@RequestBody PostCreateDTO postCreateDTO, HttpServletRequest request) {
        if (postCreateDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post info is null");
        }
        return ResultUtils.success(postService.postCreate(postCreateDTO, request));
    }
}
