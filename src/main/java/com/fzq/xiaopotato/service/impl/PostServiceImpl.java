package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.TagUtils;
import com.fzq.xiaopotato.common.utils.UploadUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.*;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.dto.post.PostCreateDTO;
import com.fzq.xiaopotato.model.dto.post.PostQueryDTO;
import com.fzq.xiaopotato.model.dto.post.PostUpdateDTO;
import com.fzq.xiaopotato.model.entity.*;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.PostService;
import com.fzq.xiaopotato.service.UserService;
import com.fzq.xiaopotato.service.UsertagService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private PosttagMapper posttagMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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

        List<String> tags = TagUtils.extractTags(postCreateDTO.getPostContent());

        for (String tagContent : tags) {
            // check if the tag exists
            Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().eq("content", tagContent));
            Long tagId;

            if (tag == null) {
                Tag newTag = new Tag();
                newTag.setContent(tagContent);
                tagMapper.insert(newTag);
                tagId = newTag.getId();
            } else {
                tagId = tag.getId();
            }

            // add relationship in Posttag table
            Posttag posttag = new Posttag();
            posttag.setPostId(post.getId());
            posttag.setTagId(tagId);
            posttagMapper.insert(posttag);
        }

        return post.getId();

    }

    @Override
    public IPage<Post> listPostByPage(PostQueryDTO postQueryDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
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
        List<Post> allPosts = this.list(queryWrapper);
        // get recommended post list from redis
        String redisKey = "user_recommendation:" + user.getId();
        List<Object> recommendedPostIdsObj = redisTemplate.opsForList().range(redisKey, 0, -1);

        List<Long> recommendedPostIds = recommendedPostIdsObj.stream()
                .flatMap(obj -> ((List<Long>) obj).stream())
                .collect(Collectors.toList());
        List<Post> sortedPosts = allPosts.stream()
                .sorted((post1, post2) -> {
                    boolean post1Recommended = recommendedPostIds.contains(post1.getId());
                    boolean post2Recommended = recommendedPostIds.contains(post2.getId());

                    // 推荐帖子优先排列
                    if (post1Recommended && !post2Recommended) return -1;
                    if (!post1Recommended && post2Recommended) return 1;
                    return 0;
                })
                .collect(Collectors.toList());

        int currentPage = postQueryDTO.getCurrentPage();
        int pageSize = postQueryDTO.getPageSize();
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, sortedPosts.size());
        List<Post> paginatedPosts = sortedPosts.subList(start, end);

        Page<Post> pageResult = new Page<>(currentPage, pageSize, sortedPosts.size());
        pageResult.setRecords(paginatedPosts);

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
        if (result > 0) {
            // delete old tag relations
            posttagMapper.delete(new QueryWrapper<Posttag>().eq("post_id", postId));
            // get new tags
            List<String> newTags = TagUtils.extractTags(postUpdateDTO.getPostContent());

            for (String tagContent : newTags) {
                // check if the tag exists
                Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().eq("content", tagContent));
                Long tagId;

                if (tag == null) {
                    Tag newTag = new Tag();
                    newTag.setContent(tagContent);
                    tagMapper.insert(newTag);
                    tagId = newTag.getId();
                } else {
                    tagId = tag.getId();
                }

                // add relationship in Posttag table
                Posttag posttag = new Posttag();
                posttag.setPostId(post.getId());
                posttag.setTagId(tagId);
                posttagMapper.insert(posttag);
            }
        }
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


    /**
    * @author zfeng
    * @description 针对表【Usertag(User-Tag Relationship Table: stores the relationship between users and their custom tags)】的数据库操作Service实现
    * @createDate 2024-10-24 15:05:29
    */
    @Service
    public static class UsertagServiceImpl extends ServiceImpl<UsertagMapper, Usertag>
        implements UsertagService {

    }
}




