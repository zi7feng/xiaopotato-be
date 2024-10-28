package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Likes;
import com.fzq.xiaopotato.model.entity.Userfollow;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import com.fzq.xiaopotato.service.UserfollowService;
import com.fzq.xiaopotato.mapper.UserfollowMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【Userfollow(User Follow Relationship Table)】的数据库操作Service实现
* @createDate 2024-10-28 18:08:23
*/
@Service
public class UserfollowServiceImpl extends ServiceImpl<UserfollowMapper, Userfollow>
    implements UserfollowService{



    @Autowired
    private UserService userService;

    @Autowired
    private UserfollowMapper userfollowMapper;

    @Override
    public boolean followByUserId(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long followerId = user.getId();
        long followedId = idDTO.getId();
        boolean isFollowed =  isFollowed(followerId, followedId);
        if (!isFollowed) {
            Userfollow userfollow = new Userfollow();
            userfollow.setFollowerId(followerId);
            userfollow.setFollowedId(followedId);
            userfollowMapper.insert(userfollow);
            return true;
        } else {
            QueryWrapper<Userfollow> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("follower_id", followerId).eq("followed_id", followedId);
            userfollowMapper.delete(queryWrapper);
            return false;
        }
    }

    private boolean isFollowed(long followerId, long followedId) {
        QueryWrapper<Userfollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId).eq("followed_id", followedId);
        return userfollowMapper.selectOne(queryWrapper) != null;
    }

    @Override
    public boolean isFollowedByUser(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return isFollowed(user.getId(), idDTO.getId());
    }
}




