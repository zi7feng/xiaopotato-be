package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.EmailUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.EmailMapper;
import com.fzq.xiaopotato.mapper.UserMapper;
import com.fzq.xiaopotato.mapper.UseremailMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.fzq.xiaopotato.model.entity.Email;
import com.fzq.xiaopotato.model.entity.User;
import com.fzq.xiaopotato.model.entity.Useremail;
import com.fzq.xiaopotato.model.vo.EmailVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.EmailService;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chenley
 * @description 针对表【Email】的数据库操作Service实现
 * @createDate 2024-11-13 19:36:29
 */
@Service
public class EmailServiceImpl extends ServiceImpl<EmailMapper, Email> implements EmailService {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private UseremailMapper useremailMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    public long createEmail(EmailSendDTO emailDto, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Email email = new Email();
        email.setToUser(emailDto.getToUser());
        email.setFromUser(emailDto.getFromUser());
        email.setSubject(emailDto.getSubject());
        email.setContent(emailDto.getContent());
        email.setUserId(user.getId());
        String sendEmailId = EmailUtils.sendEmail(emailDto);
        if (sendEmailId.isEmpty()) {
            email.setStatus(3);
        } else {
            email.setStatus(2);
        }
        if (sendEmailId.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        int insertRes = emailMapper.insert(email);
        Useremail userEmail = new Useremail();
        userEmail.setUserId(user.getId());
        userEmail.setEmailId(email.getEmailId());
        int insertMapRes = useremailMapper.insert(userEmail);
        if(insertMapRes <= 0 || insertRes <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return email.getEmailId();
    }

    @Override
    public IPage<EmailVO> listEmailByPage(EmailSendDTO emailDto, HttpServletRequest request) {
        return null;
    }

    @Override
    public boolean deleteEmail(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long emailId = idDTO.getId();
        long userId = user.getId();
        Email email = emailMapper.selectById(emailId);
        if (email == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Email not found");
        }
        Useremail userEmail = useremailMapper.selectById(userId);
        if (userEmail == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Email does not belongs to user");
        }
        email.setStatus(4); // status: 4-> deleted
        return emailMapper.updateById(email) > 0;
    }
}




