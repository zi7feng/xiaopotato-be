package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.UserfollowMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Userfollow;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserfollowServiceImplTest {

    @InjectMocks
    private UserfollowServiceImpl userfollowService;

    @Mock
    private UserService userService;

    @Mock
    private UserfollowMapper userfollowMapper;

    @Mock
    private HttpServletRequest request;

    private UserVO mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new UserVO();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setUserAccount("john_doe");
        mockUser.setUserAvatar("avatar_url");
    }

    @Test
    void testFollowByUserId_SuccessFollow() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(2L); // ID of the user to follow

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userfollowMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // Not followed initially

        // Act
        boolean result = userfollowService.followByUserId(idDTO, request);

        // Assert
        assertTrue(result);
        verify(userfollowMapper, times(1)).insert(any(Userfollow.class));
    }

    @Test
    void testFollowByUserId_SuccessUnfollow() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(2L); // ID of the user to unfollow

        Userfollow userfollow = new Userfollow();
        userfollow.setFollowerId(1L);
        userfollow.setFollowedId(2L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userfollowMapper.selectOne(any(QueryWrapper.class))).thenReturn(userfollow); // Already followed

        // Act
        boolean result = userfollowService.followByUserId(idDTO, request);

        // Assert
        assertFalse(result);
        verify(userfollowMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testFollowByUserId_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userfollowService.followByUserId(new IdDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN.getCode(), exception.getCode());
    }

    @Test
    void testFollowByUserId_FollowYourself() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L); // Same as current user ID

        when(userService.getCurrentUser(request)).thenReturn(mockUser);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userfollowService.followByUserId(idDTO, request));
        assertEquals(ErrorCode.FORBIDDEN.getCode(), exception.getCode());
    }

    @Test
    void testIsFollowedByUser_True() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(2L); // ID of the user to check

        Userfollow userfollow = new Userfollow();
        userfollow.setFollowerId(1L);
        userfollow.setFollowedId(2L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userfollowMapper.selectOne(any(QueryWrapper.class))).thenReturn(userfollow);

        // Act
        boolean isFollowed = userfollowService.isFollowedByUser(idDTO, request);

        // Assert
        assertTrue(isFollowed);
    }

    @Test
    void testIsFollowedByUser_False() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(2L); // ID of the user to check

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userfollowMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act
        boolean isFollowed = userfollowService.isFollowedByUser(idDTO, request);

        // Assert
        assertFalse(isFollowed);
    }

    @Test
    void testIsFollowedByUser_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userfollowService.isFollowedByUser(new IdDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN.getCode(), exception.getCode());
    }
}
