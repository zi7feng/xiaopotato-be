package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.SavesMapper;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Saves;
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
class SavesServiceImplTest {

    @InjectMocks
    private SavesServiceImpl savesService;

    @Mock
    private UserService userService;

    @Mock
    private SavesMapper savesMapper;

    @Mock
    private UserPostMapper userPostMapper;

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
    void testSaveByPostId_SuccessSave() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        UserPost userPost = new UserPost();
        userPost.setPostId(100L);
        userPost.setUserId(2L); // Post created by another user

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(userPost);
        when(savesMapper.selectOne(any(QueryWrapper.class))).thenReturn(null); // Not saved initially

        // Act
        boolean result = savesService.saveByPostId(idDTO, request);

        // Assert
        assertTrue(result);
        verify(savesMapper, times(1)).insert(any(Saves.class));
    }

    @Test
    void testSaveByPostId_SuccessUnsave() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        UserPost userPost = new UserPost();
        userPost.setPostId(100L);
        userPost.setUserId(1L); // Post created by the same user

        Saves saves = new Saves();
        saves.setUserId(1L);
        saves.setPostId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(userPost);
        when(savesMapper.selectOne(any(QueryWrapper.class))).thenReturn(saves); // Already saved

        // Act
        boolean result = savesService.saveByPostId(idDTO, request);

        // Assert
        assertFalse(result);
        verify(savesMapper, times(1)).delete(any(QueryWrapper.class));
    }

    @Test
    void testSaveByPostId_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> savesService.saveByPostId(new IdDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN.getCode(), exception.getCode());
    }

    @Test
    void testSaveByPostId_PostNotFound() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> savesService.saveByPostId(idDTO, request));
        assertEquals(ErrorCode.PARAMS_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testIsSaved_True() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        Saves saves = new Saves();
        saves.setUserId(1L);
        saves.setPostId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(savesMapper.selectOne(any(QueryWrapper.class))).thenReturn(saves);

        // Act
        boolean isSaved = savesService.isSaved(idDTO, request);

        // Assert
        assertTrue(isSaved);
    }

    @Test
    void testIsSaved_False() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(100L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(savesMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act
        boolean isSaved = savesService.isSaved(idDTO, request);

        // Assert
        assertFalse(isSaved);
    }

    @Test
    void testIsSaved_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> savesService.isSaved(new IdDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN.getCode(), exception.getCode());
    }
}
