package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.EmailUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.UserMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.email.EmailQueryDTO;
import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.fzq.xiaopotato.model.entity.Email;
import com.fzq.xiaopotato.model.entity.User;
import com.fzq.xiaopotato.model.vo.EmailVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.EmailService;
import com.fzq.xiaopotato.mapper.EmailMapper;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.fzq.xiaopotato.constant.UserConstant.ADMIN_ROLE;

/**
* @author zfeng
* @description 针对表【Email】的数据库操作Service实现
* @createDate 2024-11-21 18:40:40
*/
@Service
public class EmailServiceImpl extends ServiceImpl<EmailMapper, Email>
    implements EmailService{

    @Autowired
    private UserService userService;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public long createEmail(EmailSendDTO emailDto, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        Email email = new Email();
        BeanUtils.copyProperties(emailDto, email);

        email.setUserId(user.getId());
        String sendEmailId = EmailUtils.sendEmail(emailDto);

        if (sendEmailId.isEmpty()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return emailMapper.insert(email);

    }

    @Override
    public boolean deleteEmail(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long id = idDTO.getId();

        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Email ID cannot be null");
        }

        Email email = emailMapper.selectById(id);
        if (email == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Email record not found");
        }

        if (!ADMIN_ROLE.equals(user.getUserRole()) && !email.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "You do not have permission to delete this email");
        }

        int rows = emailMapper.deleteById(id);
        return rows > 0;
    }

    @Override
    public IPage<EmailVO> listEmailByPage(EmailQueryDTO emailQueryDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        Page<Email> page = new Page<>(emailQueryDTO.getCurrentPage(), emailQueryDTO.getPageSize());

        QueryWrapper<Email> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId); // 根据 userId 查询
        if (emailQueryDTO.getSubject() != null && !emailQueryDTO.getSubject().isEmpty()) {
            queryWrapper.like("subject", emailQueryDTO.getSubject()); // 按 subject 模糊查询
        }
        if (emailQueryDTO.getToUser() != null && !emailQueryDTO.getToUser().isEmpty()) {
            queryWrapper.like("to_user", emailQueryDTO.getToUser()); // 按 toUser 模糊查询
        }
        if (emailQueryDTO.getContent() != null && !emailQueryDTO.getContent().isEmpty()) {
            queryWrapper.like("content", emailQueryDTO.getContent()); // 按 content 模糊查询
        }

        queryWrapper.orderByDesc("create_time");

        IPage<Email> emailPage = emailMapper.selectPage(page, queryWrapper);


        IPage<EmailVO> emailVOPage = emailPage.convert(email -> {
            EmailVO emailVO = new EmailVO();
            emailVO.setEmailId(email.getEmailId());
            emailVO.setUserId(email.getUserId());
            emailVO.setFromUser(email.getFromUser());
            emailVO.setToUser(email.getToUser());
            emailVO.setSubject(email.getSubject());
            emailVO.setContent(email.getContent());
            emailVO.setCreateTime(email.getCreateTime());
            return emailVO;
        });

        return emailVOPage;

    }
}




