package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.SavesMapper;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Saves;
import com.fzq.xiaopotato.model.entity.Saves;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.SavesService;
import com.fzq.xiaopotato.mapper.SavesMapper;
import com.fzq.xiaopotato.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fzq.xiaopotato.common.NotificationType.LIKE;
import static com.fzq.xiaopotato.common.NotificationType.SAVE;

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

    @Autowired
    private SocketIOUtils socketIOUtils;

    @Autowired
    private UserPostMapper userPostMapper;

    @Override
    public boolean saveByPostId(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = user.getId();
        long postId = idDTO.getId();

        QueryWrapper<UserPost> userPostQuery = new QueryWrapper<>();
        userPostQuery.eq("post_id", postId);
        UserPost userPost = userPostMapper.selectOne(userPostQuery);
        if (userPost == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Post creator not found");
        }

        long creatorId = userPost.getUserId();

        boolean isSaved = isPostSavedByUser(userId, postId);
        if (!isSaved) {
            Saves saves = new Saves();
            saves.setUserId(userId);
            saves.setPostId(postId);
            savesMapper.insert(saves);
            sendFollowNotification(user, creatorId);

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

    @Override
    public boolean isSaved(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return isPostSavedByUser(user.getId(), idDTO.getId());
    }

    private void sendFollowNotification(UserVO user, Long destinateId) {
        NotificationVO notification = new NotificationVO();

        notification.setSourceId(user.getId());
        notification.setFirstName(user.getFirstName());
        notification.setLastName(user.getLastName());
        notification.setAccount(user.getUserAccount());
        notification.setAvatar(user.getUserAvatar());
        notification.setNotificationType(String.valueOf(SAVE));

        // 设置时间戳为字符串格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        notification.setTimestamp(LocalDateTime.now().format(formatter));

        socketIOUtils.sendHeartbeat(destinateId);
        // 发送通知
        socketIOUtils.sendNotification(destinateId, notification);
    }

}




