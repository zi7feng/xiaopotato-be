package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.*;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.model.vo.PostVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.LikesService;
import com.fzq.xiaopotato.service.SavesService;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private UserPostMapper userPostMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private PosttagMapper posttagMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private LikesService likesService;

    @Mock
    private SavesService savesService;

    @Mock
    private UserService userService;

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
    void testPostCreate_Success() {
        // Arrange
        PostCreateDTO postCreateDTO = new PostCreateDTO();
        postCreateDTO.setPostTitle("Test Title");
        postCreateDTO.setPostContent("Test Content");
        postCreateDTO.setPostImage("test.jpg");
        postCreateDTO.setPostGenre("Test Genre");

        Post post = new Post();
        post.setId(1L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postMapper.insert(any(Post.class))).thenAnswer(invocation -> {
            Post insertedPost = invocation.getArgument(0);
            insertedPost.setId(1L);
            return 1;
        });

        // Act
        Long postId = postService.postCreate(postCreateDTO, request);

        // Assert
        assertEquals(1L, postId);
        verify(postMapper, times(1)).insert(any(Post.class));
        verify(userPostMapper, times(1)).insert(any(UserPost.class));
    }

    @Test
    void testPostCreate_NotLoggedIn() {
        // Arrange
        when(userService.getCurrentUser(request)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> postService.postCreate(new PostCreateDTO(), request));
        assertEquals(ErrorCode.NOT_LOGIN, exception.getCode());
    }

    @Test
    void testListPostByPage_Success() {
        // Arrange
        PostQueryDTO postQueryDTO = new PostQueryDTO();
        postQueryDTO.setCurrentPage(1);
        postQueryDTO.setPageSize(10);

        Post post = new Post();
        post.setId(1L);
        post.setPostTitle("Test Title");
        post.setPostContent("Test Content");

        Page<Post> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(post));

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postMapper.selectPage(any(Page.class), any(QueryWrapper.class))).thenReturn(page);

        // Act
        IPage<PostVO> result = postService.listPostByPage(postQueryDTO, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("Test Title", result.getRecords().get(0).getPostTitle());
        verify(postMapper, times(1)).selectPage(any(Page.class), any(QueryWrapper.class));
    }

    @Test
    void testSelectPostById_Success() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setPostTitle("Test Title");
        post.setPostContent("Test Content");

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postMapper.selectById(1L)).thenReturn(post);

        // Act
        PostVO result = postService.selectPostById(idDTO, request);

        // Assert
        assertNotNull(result);
        assertEquals("Test Title", result.getPostTitle());
        verify(postMapper, times(1)).selectById(1L);
    }

    @Test
    void testSelectPostById_PostNotFound() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postMapper.selectById(1L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> postService.selectPostById(idDTO, request));
        assertEquals(ErrorCode.SYSTEM_ERROR, exception.getCode());
    }

    @Test
    void testDeletePostById_Success() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        Post post = new Post();
        post.setId(1L);

        UserPost userPost = new UserPost();
        userPost.setUserId(1L);
        userPost.setPostId(1L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postMapper.selectById(1L)).thenReturn(post);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(userPost);
        when(postMapper.deleteById(1L)).thenReturn(1);

        // Act
        boolean result = postService.deletePostById(idDTO, request);

        // Assert
        assertTrue(result);
        verify(postMapper, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePostById_NoAuth() {
        // Arrange
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1L);

        Post post = new Post();
        post.setId(1L);

        when(userService.getCurrentUser(request)).thenReturn(mockUser);
        when(postMapper.selectById(1L)).thenReturn(post);
        when(userPostMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> postService.deletePostById(idDTO, request));
        assertEquals(ErrorCode.NO_AUTH, exception.getCode());
    }
}
