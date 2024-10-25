package com.fzq.xiaopotato.service;

import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Likes;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author zfeng
* @description 针对表【Like】的数据库操作Service
* @createDate 2024-10-25 16:14:48
*/
public interface LikesService extends IService<Likes> {
    boolean likeByPostId(IdDTO idDTO, HttpServletRequest request);

    boolean isLiked(IdDTO idDTO, HttpServletRequest request);
}