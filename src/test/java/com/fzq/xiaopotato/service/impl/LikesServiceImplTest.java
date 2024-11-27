package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.LikesMapper;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Likes;
import com.fzq.xiaopotato.model.entity.UserPost;
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
class LikesServiceImplTest {

    @InjectMocks
    private LikesServiceImpl likesService;

    @Mock
    private UserService userService;

    @Mock
    private LikesMapper likesMapper;

    @Mock
    private UserPostMapper userPostMapper;

    @Mock
    private SocketIOUtils socketIOUtils;

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
    void testLikeByPostId_SuccessLike() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        UserPost userPost = new UserPost();
        userPost.setPostId(100L);
        userPost.setUserId(2L); // Post created by another user

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(userPost);
        when(likesMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // Not liked initially

        // Act
        boolean result = likesService.likeByPostId(idDTO, request);

        // Assert
        assertTrue(result);
        verify(likesMapper, times(1)).insert(any(Likes.class));
        verify(socketIOUtils, times(1)).sendNotification(eq(2L), any());
    }

    @Test
    void testLikeByPostId_SuccessUnlike() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        UserPost userPost = new UserPost();
        userPost.setPostId(100L);
        userPost.setUserId(1L); // Post created by the same user

        Likes likes = new Likes();
        likes.setUserId(1L);
        likes.setPostId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(userPost);
        when(likesMapper.selectOne(any(QueryWrapper.class))).thenReturn(likes); // Already liked

        // Act
        boolean result = likesService.likeByPostId(idDTO, request);

        // Assert
        assertFalse(result);
        verify(likesMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testLikeByPostId_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> likesService.likeByPostId(new IdDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN.getCode(), exception.getCode());
    }

    @Test
    void testLikeByPostId_PostNotFound() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> likesService.likeByPostId(idDTO, request));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testIsLiked_True() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        Likes likes = new Likes();
        likes.setUserId(1L);
        likes.setPostId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(likesMapper.selectOne(any(QueryWrapper.class))).thenReturn(likes);

        // Act
        boolean isLiked = likesService.isLiked(idDTO, request);

        // Assert
        assertTrue(isLiked);
    }

    @Test
    void testIsLiked_False() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(likesMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act
        boolean isLiked = likesService.isLiked(idDTO, request);

        // Assert
        assertFalse(isLiked);
    }

    @Test
    void testIsLiked_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> likesService.isLiked(new IdDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN.getCode(), exception.getCode());
    }
}
