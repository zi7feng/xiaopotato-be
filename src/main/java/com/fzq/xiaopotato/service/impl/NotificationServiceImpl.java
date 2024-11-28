package com.fzq.xiaopotato.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fzq.xiaopotato.model.entity.Notification;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.service.NotificationService;
import com.fzq.xiaopotato.mapper.NotificationMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void storeNotification(Long followedId, NotificationVO notificationVO) {
        Assert.notNull(followedId, "User ID cannot be null");
        Assert.notNull(notificationVO, "Notification cannot be null");

        try {
            Notification notification = new Notification();
            BeanUtils.copyProperties(notificationVO, notification);
            notification.setIsRead(0);
            notification.setUserId(followedId);

            // 添加日志
            log.info("Storing notification for user {}: {}", followedId, notification);

            int result = notificationMapper.insert(notification);
            if (result <= 0) {
                log.error("Failed to store notification for user {}", followedId);
                throw new RuntimeException("Failed to store notification");
            }

            log.info("Successfully stored notification for user {}", followedId);
        } catch (Exception e) {
            log.error("Error storing notification for user {}: {}", followedId, e.getMessage(), e);
            throw new RuntimeException("Error storing notification", e);
        }
    }

    @Override
    public List<NotificationVO> getUnreadNotifications(Long userId) {
        Assert.notNull(userId, "User ID cannot be null");

        try {
            QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                    .eq("is_read", 0)
                    .eq("is_push", 0)
                    .orderByDesc("create_time");

            return notificationMapper.selectList(queryWrapper)
                    .stream()
                    .map(notification -> {
                        NotificationVO notificationVO = new NotificationVO();
                        BeanUtils.copyProperties(notification, notificationVO);
                        return notificationVO;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting unread notifications for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving notifications", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markNotificationsAsRead(Long userId) {
        Assert.notNull(userId, "User ID cannot be null");

        try {
            QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("is_read", 0);

            Notification updateEntity = new Notification();
            updateEntity.setIsRead(1);
            int result = notificationMapper.update(updateEntity, queryWrapper);
            log.info("Marked {} notifications as read for user {}", result, userId);
        } catch (Exception e) {
            log.error("Error marking notifications as read for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error updating notifications", e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markNotificationsAsPush(Long userId) {
        Assert.notNull(userId, "User ID cannot be null");

        try {
            QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).eq("is_push", 0);

            Notification updateEntity = new Notification();
            updateEntity.setIsPush(1);


            int result = notificationMapper.update(updateEntity, queryWrapper);
            log.info("Marked {} notifications as push for user {}", result, userId);
        } catch (Exception e) {
            log.error("Error marking notifications as push for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error updating notifications", e);
        }
    }

    @Override
    public List<NotificationVO> getAllNotifications(Long userId) {
        Assert.notNull(userId, "User ID cannot be null");

        try {
            QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId).orderByDesc("create_time");

            return notificationMapper.selectList(queryWrapper)
                    .stream()
                    .map(notification -> {
                        NotificationVO notificationVO = new NotificationVO();
                        BeanUtils.copyProperties(notification, notificationVO);

                        notificationVO.setTimestamp(
                                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notification.getCreateTime())
                        );
                        return notificationVO;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting all notifications for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error retrieving notifications", e);
        }
    }

}