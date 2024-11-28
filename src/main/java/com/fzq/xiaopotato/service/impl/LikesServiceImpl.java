package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.esotericsoftware.minlog.Log;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.mapper.UserPostMapper;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Likes;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.LikesService;
import com.fzq.xiaopotato.mapper.LikesMapper;
import com.fzq.xiaopotato.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.fzq.xiaopotato.common.NotificationType.FOLLOW;
import static com.fzq.xiaopotato.common.NotificationType.LIKE;

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

    @Autowired
    private UserPostMapper userPostMapper;

    @Autowired
    private SocketIOUtils socketIOUtils;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean likeByPostId(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = user.getId();
        long postId = idDTO.getId();
        String lockKey = "user:like:" + userId + ":" + postId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    Log.info("getLock: " + Thread.currentThread().getId());
                    QueryWrapper<UserPost> userPostQuery = new QueryWrapper<>();
                    userPostQuery.eq("post_id", postId);
                    UserPost userPost = userPostMapper.selectOne(userPostQuery);

                    if (userPost == null) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "Post creator not found");
                    }

                    long creatorId = userPost.getUserId();

                    boolean isLiked = isPostLikedByUser(userId, postId);
                    if (!isLiked) {
                        Likes likes = new Likes();
                        likes.setUserId(userId);
                        likes.setPostId(postId);
                        likesMapper.insert(likes);

                        if (user.getId() != creatorId) {
                            sendLikeNotification(user, creatorId);
                        }
                        return true;
                    } else {
                        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("user_id", userId).eq("post_id", postId);
                        likesMapper.delete(queryWrapper);
                        return false;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error(Thread.currentThread().getId() + " get lock failed");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "get lock failed");
        } finally {
            // can only release itself lock
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                Log.info("release lock: {}", String.valueOf(Thread.currentThread().getId()));
            }

        }




    }

    private void sendLikeNotification(UserVO user, Long destinateId) {
        NotificationVO notification = new NotificationVO();

        notification.setSourceId(user.getId());
        notification.setFirstName(user.getFirstName());
        notification.setLastName(user.getLastName());
        notification.setAccount(user.getUserAccount());
        notification.setAvatar(user.getUserAvatar());
        notification.setNotificationType(String.valueOf(LIKE));

        // 设置时间戳为字符串格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        notification.setTimestamp(LocalDateTime.now().format(formatter));

        socketIOUtils.sendHeartbeat(destinateId);
        // 发送通知
        socketIOUtils.sendNotification(destinateId, notification);
    }

    private boolean isPostLikedByUser(long userId, long postId) {
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("post_id", postId);
        return likesMapper.selectOne(queryWrapper) != null;
    }

    @Override
    public boolean isLiked(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return isPostLikedByUser(user.getId(), idDTO.getId());
    }
}




