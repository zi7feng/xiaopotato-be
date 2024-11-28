package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.esotericsoftware.minlog.Log;
import com.fzq.xiaopotato.common.ErrorCode;
import com.fzq.xiaopotato.common.NotificationType;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import com.fzq.xiaopotato.exception.BusinessException;
import com.fzq.xiaopotato.model.dto.common.IdDTO;
import com.fzq.xiaopotato.model.entity.Likes;
import com.fzq.xiaopotato.model.entity.Saves;
import com.fzq.xiaopotato.model.entity.UserPost;
import com.fzq.xiaopotato.model.entity.Userfollow;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.model.vo.UserVO;
import com.fzq.xiaopotato.service.UserService;
import com.fzq.xiaopotato.service.UserfollowService;
import com.fzq.xiaopotato.mapper.UserfollowMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.fzq.xiaopotato.common.NotificationType.FOLLOW;

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

    @Autowired
    private SocketIOUtils socketIOUtils;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean followByUserId(IdDTO idDTO, HttpServletRequest request) {
        UserVO user = userService.getCurrentUser(request);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long followerId = user.getId();
        long followedId = idDTO.getId();
        if (followedId == followerId) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "Cannot follow yourself.");
        }
        String lockKey = "user:follow:" + followerId + ":" + followedId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    Log.info("getLock: " + Thread.currentThread().getId());
                    boolean isFollowed =  isFollowed(followerId, followedId);
                    if (!isFollowed) {
                        Userfollow userfollow = new Userfollow();
                        userfollow.setFollowerId(followerId);
                        userfollow.setFollowedId(followedId);
                        userfollowMapper.insert(userfollow);
                        sendFollowNotification(user, followedId);
                        return true;
                    } else {
                        QueryWrapper<Userfollow> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("follower_id", followerId).eq("followed_id", followedId);
                        userfollowMapper.delete(queryWrapper);
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

    @Override
    public boolean isFollowed(long followerId, long followedId) {
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

    /**
     * 发送关注通知
     */
    private void sendFollowNotification(UserVO follower, Long followedId) {
        NotificationVO notification = new NotificationVO();

        // 手动设置字段值
        notification.setSourceId(follower.getId());
        notification.setFirstName(follower.getFirstName());
        notification.setLastName(follower.getLastName());
        notification.setAccount(follower.getUserAccount());
        notification.setAvatar(follower.getUserAvatar());
        notification.setNotificationType(String.valueOf(FOLLOW));

        // 设置时间戳为字符串格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        notification.setTimestamp(LocalDateTime.now().format(formatter));

        socketIOUtils.sendHeartbeat(followedId);
        // 发送通知
        socketIOUtils.sendNotification(followedId, notification);
    }
}




