package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Likes;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.LikesService;
import com.fzq.xiaopotato.mapper.LikesMapper;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【Like】的数据库操作Service实现
* @createDate 2024-10-25 16:14:48
*/
@Service
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes>
    implements LikesService {


    @Autowired
    private UserService userService;

    @Autowired
    private LikesMapper likesMapper;

    @Override
    public boolean likeByPostId(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = user.getId();
        long postId = idDTO.getId();

        boolean isLiked = isPostLikedByUser(userId, postId);
        if (!isLiked) {
            Likes likes = new Likes();
            likes.setUserId(userId);
            likes.setPostId(postId);
            likesMapper.insert(likes);
            return true;
        } else {
            QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("post_id", postId);
            likesMapper.delete(queryWrapper);
            return false;
        }

    }

    private boolean isPostLikedByUser(long userId, long postId) {
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("post_id", postId);
        return likesMapper.selectOne(queryWrapper) != null;
    }

    public boolean isLiked(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return isPostLikedByUser(user.getId(), idDTO.getId());
    }
}




