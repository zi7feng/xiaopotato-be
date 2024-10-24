package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.BaseResponse;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.UploadUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.Post;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.PostService;
import com.fzq.xiaopotato.mapper.PostMapper;
import com.fzq.xiaopotato.service.UserService;
import com.fzq.xiaopotato.service.UserPostService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【Post(Post Table)】的数据库操作Service实现
* @createDate 2024-10-23 14:21:47
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserPostMapper userPostMapper;

    @Override
    public Long postCreate(PostCreateDTO postCreateDTO, HttpServletRequest request) {
        UserVO currentUser = userService.getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Post post = new Post();
        post.setPostTitle(postCreateDTO.getPostTitle());
        post.setPostContent(postCreateDTO.getPostContent());
        post.setPostImage(postCreateDTO.getPostImage());

        postMapper.insert(post);
        UserPost userPost = new UserPost();
        userPost.setUserId(currentUser.getId());
        userPost.setPostId(post.getId());

        userPostMapper.insert(userPost);

        return post.getId();

    }

    @Override
    public IPage<Post> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        if (userService.getCurrentUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Page<Post> page = new Page<>(postQueryDTO.getCurrentPage(), postQueryDTO.getPageSize());
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();

        String title = postQueryDTO.getPostTitle();
        String content = postQueryDTO.getPostContent();

        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("post_title", title);
        }
        if (!StringUtils.isEmpty(content)) {
            queryWrapper.like("post_content", content);
        }


        IPage<Post> pageResult = this.page(page, queryWrapper);
        return pageResult;
    }

    @Override
    public Post selectPostById(IdDTO idDTO, HttpServletRequest request) {
        if (userService.getCurrentUser(request) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long postId = idDTO.getId();
        if (postId == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post ID is null.");
        }

        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Post not found or deleted.");
        }
        return post;
    }

    @Override
    public Boolean updatePostById(PostUpdateDTO postUpdateDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }


        Long userId = user.getId();
        Long postId = postUpdateDTO.getId();
        // check if the post exist
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post not found");
        }

        // check if the user is the creator
        UserPost userPost = userPostMapper.selectOne(
                new QueryWrapper<UserPost>().eq("user_id", userId).eq("post_id", postId)
        );
        if (userPost == null) {
            throw new BusinessException(ErrorCode.NO_AUTH, "Not the owner of the post.");
        }



        // delete old picture
        if (post.getPostImage() != null) {
            String oldImageUrl = post.getPostImage();
            UploadUtils.deleteImage(oldImageUrl);
        }

        post.setPostTitle(postUpdateDTO.getPostTitle());
        post.setPostContent(postUpdateDTO.getPostContent());
        post.setPostImage(postUpdateDTO.getPostImage());
        int result = postMapper.updateById(post);
        return result > 0;
    }

    @Override
    public Boolean deletePostById(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        Long postId = idDTO.getId();

        // check if the post exist
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "Post not found");
        }

        // check if the user is the creator
        UserPost userPost = userPostMapper.selectOne(
                new QueryWrapper<UserPost>().eq("user_id", userId).eq("post_id", postId)
        );
        if (userPost == null) {
            throw new BusinessException(ErrorCode.NO_AUTH, "Not the owner of the post.");
        }

        // delete file in oss
        if (post.getPostImage() != null) {
            String imageUrl = post.getPostImage();
            UploadUtils.deleteImage(imageUrl);
        }

        int result = postMapper.deleteById(postId);
        return result > 0;
    }


}




