package com.fzq.xiaopotato.service;

import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Userfollow;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Userfollow(User Follow Relationship Table)】的数据库操作Service
* @createDate 2024-10-28 18:08:23
*/
public interface UserfollowService extends IService<Userfollow> {
    boolean followByUserId(IdDTO idDTO, HttpServletRequest request);

    boolean isFollowedByUser(IdDTO idDTO, HttpServletRequest request);
    boolean isFollowed(long followerId, long followedId);

    }
