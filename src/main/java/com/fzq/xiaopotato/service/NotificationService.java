package com.fzq.xiaopotato.service;

import com.fzq.xiaopotato.model.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fzq.xiaopotato.model.vo.NotificationVO;

import java.util.List;

/**
* @author zfeng
* @description 针对表【Notification】的数据库操作Service
* @createDate 2024-10-30 20:27:24
*/
public interface NotificationService extends IService<Notification> {

    // 存储通知
    void storeNotification(Long followedId,NotificationVO notification);

    // 获取用户的未读通知
    List<NotificationVO> getUnreadNotifications(Long userId);

    // 标记通知为已读
    void markNotificationsAsRead(Long userId);
}
