package com.fzq.xiaopotato.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.email.EmailQueryDTO;
import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.vo.EmailVO;
import com.fzq.xiaopotato.model.vo.PostVO;
import com.fzq.xiaopotato.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public BaseResponse<Boolean> sendEmail(@RequestBody EmailSendDTO emailSendDTO, HttpServletRequest request) {
        if (emailSendDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "parameter is null");
        }
        return ResultUtils.success(emailService.createEmail(emailSendDTO, request) > 0);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEmail(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "parameter is null");
        }
        return ResultUtils.success(emailService.deleteEmail(idDTO, request));
    }


    @Operation(summary = "List email with pagination")
    @GetMapping("/selectByPage")
    public BaseResponse<IPage<EmailVO>> listPostByPage(EmailQueryDTO emailQueryDTO, HttpServletRequest request) {
        if (emailQueryDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Null query dto.");
        }
        if (emailQueryDTO.getCurrentPage() <= 0 || emailQueryDTO.getPageSize() <= 0) {
            emailQueryDTO.setCurrentPage(1);
            emailQueryDTO.setPageSize(10);
        }
        IPage<EmailVO> result = emailService.listEmailByPage(emailQueryDTO, request);
        return ResultUtils.success(result);
    }
}
