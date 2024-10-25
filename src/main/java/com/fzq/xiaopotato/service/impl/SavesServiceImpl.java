package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.SavesMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Saves;
import com.fzq.xiaopotato.model.entity.Saves;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.SavesService;
import com.fzq.xiaopotato.mapper.SavesMapper;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author zfeng
* @description 针对表【Saves】的数据库操作Service实现
* @createDate 2024-10-25 17:08:10
*/
@Service
public class SavesServiceImpl extends ServiceImpl<SavesMapper, Saves>
    implements SavesService{

    @Autowired
    private UserService userService;

    @Autowired
    private SavesMapper savesMapper;

    @Override
    public boolean saveByPostId(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = user.getId();
        long postId = idDTO.getId();

        boolean isSaved = isPostSavedByUser(userId, postId);
        if (!isSaved) {
            Saves saves = new Saves();
            saves.setUserId(userId);
            saves.setPostId(postId);
            savesMapper.insert(saves);
            return true;
        } else {
            QueryWrapper<Saves> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("post_id", postId);
            savesMapper.delete(queryWrapper);
            return false;
        }

    }

    private boolean isPostSavedByUser(long userId, long postId) {
        QueryWrapper<Saves> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("post_id", postId);
        return savesMapper.selectOne(queryWrapper) != null;
    }

    public boolean isSaved(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return isPostSavedByUser(user.getId(), idDTO.getId());
    }

}




