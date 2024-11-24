package com.fzq.xiaopotato.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.email.EmailQueryDTO;
import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.fzq.xiaopotato.model.entity.Email;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzq.xiaopotato.model.vo.EmailVO;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Email】的数据库操作Service
* @createDate 2024-11-21 18:40:40
*/
public interface EmailService extends IService<Email> {
    long createEmail(EmailSendDTO emailDto, HttpServletRequest request);

    boolean deleteEmail(IdDTO idDTO, HttpServletRequest request);

    IPage<EmailVO> listEmailByPage(EmailQueryDTO emailQueryDTO, HttpServletRequest request);
}
