package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.CommentMapper;
import com.fzq.xiaopotato.mapper.PostcommentMapper;
import com.fzq.xiaopotato.mapper.UserMapper;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.comment.FirstCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.comment.FirstQueryDTO;
import com.fzq.xiaopotato.model.dto.comment.SecondCommentCreateDTO;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Comment;
import com.fzq.xiaopotato.model.entity.Postcomment;
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

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostcommentMapper postcommentMapper;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SocketIOUtils socketIOUtils;

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
    }

    @Test
    void testCreateFirstLevelComment_Success() {
        // Arrange
        FirstCommentCreateDTO dto = new FirstCommentCreateDTO();
        dto.setPostId(1L);
        dto.setContent("Test comment");

        Comment comment = new Comment();
        comment.setCommentId(1L);

        UserPost userPost = new UserPost();
        userPost.setUserId(2L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(userPost);
        when(commentMapper.insert(any(Comment.class))).thenAnswer(invocation -> {
            Comment arg = invocation.getArgument(0);
            arg.setCommentId(1L);
            return 1;
        });

        // Act
        Long commentId = commentService.createFirstLevelComment(dto, request);

        // Assert
        assertEquals(1L, commentId);
        verify(postcommentMapper, times(1)).insert(any(Postcomment.class));
        verify(socketIOUtils, times(1)).sendHeartbeat(2L);
    }

    @Test
    void testCreateFirstLevelComment_UserNotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.createFirstLevelComment(new FirstCommentCreateDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN, exception.getCode());
    }

    @Test
    void testDeleteComment_Success() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setUserId(mockUser.getId());

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(commentMapper.selectById(1L)).thenReturn(comment);
        when(commentMapper.deleteById(1L)).thenReturn(1);

        // Act
        boolean result = commentService.deleteComment(idDTO, request);

        // Assert
        assertTrue(result);
        verify(commentMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(commentMapper.selectById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> commentService.deleteComment(idDTO, request));
        assertEquals(ErrorCode.NULL_ERROR, exception.getCode());
    }

    @Test
    void testListFirstLevelCommentsByPostId_Success() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postcommentMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.singletonList(new Postcomment()));

        Page<Comment> page = new Page<>();
        page.setRecords(Collections.singletonList(new Comment()));

        when(commentMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        // Act
        IPage<?> result = commentService.listFirstLevelCommentsByPostId(new FirstQueryDTO(), request);

        // Assert
        assertNotNull(result);
        verify(commentMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testCreateSecondLevelComment_Success() {
        // Arrange
        SecondCommentCreateDTO dto = new SecondCommentCreateDTO();
        dto.setPostId(1L);
        dto.setCommentId(1L);
        dto.setContent("Reply to a comment");

        when(userService.getCurrentUser(request)).thenReturn(mockUser);

        // Act
        Long commentId = commentService.createSecondLevelComment(dto, request);

        // Assert
        assertNotNull(commentId);
        verify(commentMapper, times(1)).insert(any(Comment.class));
    }
}
