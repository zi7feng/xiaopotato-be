package com.fzq.xiaopotato.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.comment.FirstCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.FirstQueryDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondQueryDTO;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.vo.FirstCommentVO;
import com.fzq.xiaopotato.model.vo.PostVO;
import com.fzq.xiaopotato.model.vo.SecondCommentVO;
import com.fzq.xiaopotato.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/createFirstComment")
    public BaseResponse<Long> createFirstLevelComment(@RequestBody FirstCommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        if (commentCreateDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "commentCreateDTO is null");
        }
        return ResultUtils.success(commentService.createFirstLevelComment(commentCreateDTO, request));
    }

    @PostMapping("/createSecondComment")
    public BaseResponse<Long> createSecondLevelComment(@RequestBody SecondCommentCreateDTO commentCreateDTO, HttpServletRequest request) {
        if (commentCreateDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "commentCreateDTO is null");
        }
        return ResultUtils.success(commentService.createSecondLevelComment(commentCreateDTO, request));
    }

    @GetMapping("/first-level")
    public BaseResponse<IPage<FirstCommentVO>> getFirstLevelComments(FirstQueryDTO firstQueryDTO, HttpServletRequest request) {
        if (firstQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "firstQueryDTO is null");
        }
        if (firstQueryDTO.getCurrentPage() <= 0 || firstQueryDTO.getPageSize() <= 0) {
            firstQueryDTO.setCurrentPage(1);
            firstQueryDTO.setPageSize(10);
        }
        IPage<FirstCommentVO> result = commentService.listFirstLevelCommentsByPostId(firstQueryDTO, request);
        return ResultUtils.success(result);

    }

    @GetMapping("/second-level")
    public BaseResponse<IPage<SecondCommentVO>> getSecondLevelComments(SecondQueryDTO secondQueryDTO, HttpServletRequest request) {
        if (secondQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "secondQueryDTO is null");
        }
        if (secondQueryDTO.getCurrentPage() <= 0 || secondQueryDTO.getPageSize() <= 0) {
            secondQueryDTO.setCurrentPage(1);
            secondQueryDTO.setPageSize(10);
        }
        IPage<SecondCommentVO> result = commentService.listSecondLevelCommentsByPostId(secondQueryDTO, request);
        return ResultUtils.success(result);

    }

    @PostMapping("/deleteByCommentId")
    public BaseResponse<Boolean> deleteComment(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null || idDTO.getId() == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Comment ID is null");
        }

        return ResultUtils.success(commentService.deleteComment(idDTO, request));
    }
}