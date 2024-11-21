package com.fzq.xiaopotato.controller;

import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.ResultUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.fzq.xiaopotato.service.EmailService;
import com.fzq.xiaopotato.service.UserfollowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public BaseResponse<Long> sendEmail(@RequestBody EmailSendDTO emailSendDTO, HttpServletRequest request) {
        if (emailSendDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "parameter is null");
        }
        return ResultUtils.success(emailService.createEmail(emailSendDTO,request));
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> sendEmail(@RequestBody IdDTO idDTO, HttpServletRequest request) {
        if (idDTO == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "parameter is null");
        }
        return ResultUtils.success(emailService.deleteEmail(idDTO,request));
    }


}
