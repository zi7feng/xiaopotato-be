package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.JwtUtils;
import com.fzq.xiaopotato.common.utils.PasswordUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.*;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.user.UserLoginDTO;
import com.fzq.xiaopotato.model.dto.user.UserRegisterDTO;
import com.fzq.xiaopotato.model.dto.user.UserUpdateDTO;
import com.fzq.xiaopotato.model.entity.User;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserfollowService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserfollowService userfollowService;

    private UserVO mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserVO();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUserAccount("john_doe");
        mockUser.setUserAvatar("avatar_url");
        mockUser.setUserRole("USER");
    }

    @Test
    void testUserRegister_Success() {
        // Arrange
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setFirstName("John");
        userRegisterDTO.setLastName("Doe");
        userRegisterDTO.setUserAccount("john_doe");
        userRegisterDTO.setUserPassword("Password123");
        userRegisterDTO.setCheckPassword("Password123");
        userRegisterDTO.setEmail("john@example.com");

        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        // Act
        Long userId = userService.userRegister(userRegisterDTO);

        // Assert
        assertNotNull(userId);
        assertEquals(1L, userId);
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    void testUserRegister_AccountAlreadyExists() {
        // Arrange
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUserAccount("john_doe");

        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.userRegister(userRegisterDTO));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testUserLogin_Success() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserAccount("john_doe");
        userLoginDTO.setUserPassword("Password123");

        User user = new User();
        BeanUtils.copyProperties(mockUser, user);

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(user);

        // Act
        UserVO result = userService.userLogin(userLoginDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testUserLogin_WrongCredentials() {
        // Arrange
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserAccount("john_doe");
        userLoginDTO.setUserPassword("WrongPassword");

        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.userLogin(userLoginDTO));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testGetCurrentUser_Success() {
        // Arrange
        String token = "Bearer mock_token";
        when(request.getHeader("Authorization")).thenReturn(token);

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("id", 1L);
        Claims claims = mock(Claims.class);
        when(claims.get("id", Long.class)).thenReturn(1L);

        User user = new User();
        BeanUtils.copyProperties(mockUser, user);

        when(jwtUtils.isTokenBlacklisted("mock_token")).thenReturn(false);
        when(jwtUtils.getClaimsFromToken("mock_token")).thenReturn(claims);
        when(userMapper.selectById(1L)).thenReturn(user);

        // Act
        UserVO result = userService.getCurrentUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userMapper, times(1)).selectById(1L);
    }

    @Test
    void testGetCurrentUser_InvalidToken() {
        // Arrange
        String token = "Bearer invalid_token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtils.isTokenBlacklisted("invalid_token")).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getCurrentUser(request));
        assertEquals(ErrorCode.NO_AUTH.getCode(), exception.getCode());
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("john_updated@example.com");
        userUpdateDTO.setPhone("1234567890");

        User user = new User();
        BeanUtils.copyProperties(mockUser, user);

        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // Act
        int result = userService.updateUser(userUpdateDTO, mockUser, request);

        // Assert
        assertEquals(1, result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateUser_NoAuth() {
        // Arrange
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();

        UserVO anotherUser = new UserVO();
        anotherUser.setId(2L);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUser(userUpdateDTO, anotherUser, request));
        assertEquals(ErrorCode.NO_AUTH.getCode(), exception.getCode());
    }
}
