package com.fzq.xiaopotato.controller;

import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.service.LikesService;
import com.fzq.xiaopotato.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikesController {
    @Autowired
    private LikesService likesService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Like or Unlike a Post by ID")
    @PostMapping("/likeByPostId")
    public BaseResponse<Boolean> likeByPostId(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(likesService.likeByPostId(idDTO, request), "data indicates whether the post is now liked (true) or unliked (false).");
    }


    @Operation(summary = "Check if the current user liked a Post by ID")
    @GetMapping("/isLiked")
    public BaseResponse<Boolean> isLiked(IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "id is null");
        }
        return ResultUtils.success(likesService.isLiked(idDTO, request));
    }
}
