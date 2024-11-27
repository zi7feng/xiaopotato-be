package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.EmailUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.EmailMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.email.EmailQueryDTO;
import com.fzq.xiaopotato.model.dto.email.EmailSendDTO;
import com.fzq.xiaopotato.model.entity.Email;
import com.fzq.xiaopotato.model.vo.EmailVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private UserService userService;

    @Mock
    private EmailMapper emailMapper;

    @Mock
    private HttpServletRequest request;

    private UserVO mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserVO();
        mockUser.setId(1L);
        mockUser.setUserRole("USER");
    }

    @Test
    void testCreateEmail_Success() {
        // Arrange
        EmailSendDTO emailDto = new EmailSendDTO();
        emailDto.setSubject("Test Email");
        emailDto.setToUser("test@example.com");
        emailDto.setContent("Test content");

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(EmailUtils.sendEmail(emailDto)).thenReturn("123456"); // Mock email ID returned by EmailUtils

        // Act
        long result = emailService.createEmail(emailDto, request);

        // Assert
        assertEquals(0, result); // EmailMapper.insert should return 0 for now
        verify(emailMapper, times(1)).insert(any(Email.class));
    }

    @Test
    void testCreateEmail_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailService.createEmail(new EmailSendDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN, exception.getCode());
    }

    @Test
    void testDeleteEmail_Success() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        Email email = new Email();
        email.setEmailId(1L);
        email.setUserId(mockUser.getId());

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(emailMapper.selectById(1L)).thenReturn(email);
        when(emailMapper.deleteById(1L)).thenReturn(1);

        // Act
        boolean result = emailService.deleteEmail(idDTO, request);

        // Assert
        assertTrue(result);
        verify(emailMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEmail_EmailNotFound() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(emailMapper.selectById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailService.deleteEmail(idDTO, request));
        assertEquals(ErrorCode.NULL_ERROR, exception.getCode());
    }

    @Test
    void testDeleteEmail_NoPermission() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        Email email = new Email();
        email.setEmailId(1L);
        email.setUserId(2L); // Email belongs to another user

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(emailMapper.selectById(1L)).thenReturn(email);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailService.deleteEmail(idDTO, request));
        assertEquals(ErrorCode.NO_AUTH, exception.getCode());
    }

    @Test
    void testListEmailByPage_Success() {
        // Arrange
        EmailQueryDTO queryDTO = new EmailQueryDTO();
        queryDTO.setCurrentPage(1);
        queryDTO.setPageSize(10);

        Page<Email> emailPage = new Page<>();
        Email email = new Email();
        email.setEmailId(1L);
        email.setUserId(1L);
        email.setSubject("Test Subject");
        emailPage.setRecords(Collections.singletonList(email));

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(emailMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(emailPage);

        // Act
        IPage<EmailVO> result = emailService.listEmailByPage(queryDTO, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("Test Subject", result.getRecords().get(0).getSubject());
    }

    @Test
    void testListEmailByPage_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> emailService.listEmailByPage(new EmailQueryDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN, exception.getCode());
    }
}
