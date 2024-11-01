package com.fzq.xiaopotato.common.utils;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.service.NotificationService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SocketIOUtils {

    private final Map<Long, SocketIOClient> onlineUsers = new ConcurrentHashMap<>();

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ONLINE_USER_KEY_PREFIX = "user:online:";

    public void onConnect(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
        if (token != null) {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("id", Long.class);

            onlineUsers.put(userId, client);

            // 用户上线后，将在线状态存入 Redis
            redisTemplate.opsForValue().set(ONLINE_USER_KEY_PREFIX + userId, "online", 12, TimeUnit.HOURS);

            // 查询并推送用户的未读通知
            List<NotificationVO> unreadNotifications = notificationService.getUnreadNotifications(userId);
            unreadNotifications.forEach(notification -> client.sendEvent("notification", notification));

            // 更新所有通知为已读状态
            notificationService.markNotificationsAsRead(userId);
            log.info("Client connected: {}", client.getSessionId());

        }
    }


    public void onDisconnect(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");
        if (token != null) {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("id", Long.class);
            onlineUsers.remove(userId);

            // 移除 Redis 中的在线状态
            redisTemplate.delete(ONLINE_USER_KEY_PREFIX + userId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(Long userId, NotificationVO notification) {
        try {
            // 先尝试存储通知，确保无论用户是否在线都会保存
            notificationService.storeNotification(userId, notification);
            log.info("Stored notification for user {}", userId);

            // 如果用户在线，则实时推送
            if (isUserOnline(userId)) {
                SocketIOClient client = onlineUsers.get(userId);
                if (client != null && client.isChannelOpen()) {
                    try {
                        client.sendEvent("notification", notification);
                        log.info("Successfully sent real-time notification to user {}", userId);
                    } catch (Exception e) {
                        log.error("Failed to send real-time notification to user {}: {}", userId, e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error in sendNotification for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to process notification", e);
        }
    }

    public boolean isUserOnline(Long userId) {
        try {
            // 检查内存中的在线状态
            boolean inMemory = onlineUsers.containsKey(userId) &&
                    onlineUsers.get(userId).isChannelOpen();

            // 检查Redis中的在线状态
            boolean inRedis = Boolean.TRUE.equals(
                    redisTemplate.hasKey(ONLINE_USER_KEY_PREFIX + userId));

            // 如果状态不一致，以Redis为准
            if (inMemory != inRedis) {
                log.warn("Inconsistent online status for user {}: memory={}, redis={}",
                        userId, inMemory, inRedis);
                if (!inRedis) {
                    onlineUsers.remove(userId);
                }
            }

            return inRedis;
        } catch (Exception e) {
            log.error("Error checking online status for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    public void sendHeartbeat(Long userId) {
        if (isUserOnline(userId)) {
            SocketIOClient client = onlineUsers.get(userId);
            if (client != null) {
                client.sendEvent("heartbeat", "A new notification is about to arrive!");
            }
        }
    }

    private final Random random = new Random();

    /**
     * 处理前端消息并返回随机数
     * @param client Socket客户端
     * @param data 前端发送的数据
     */
    public void handleMessage(SocketIOClient client, String data) {
        log.info("Received message from client: {}", data);
        try {
            log.info("Received message from client: {}", data);

            // 生成1到100之间的随机数
            int randomNumber = random.nextInt(100) + 1;

            // 构建响应对象
            Map<String, Object> response = Map.of(
                    "randomNumber", randomNumber,
                    "receivedMessage", data,
                    "timestamp", System.currentTimeMillis()
            );

            // 发送随机数回客户端
            client.sendEvent("randomResponse", response);
            log.info("Sent random number {} to client", randomNumber);

        } catch (Exception e) {
            log.error("Error handling message: {}", e.getMessage(), e);
            // 发送错误响应
            client.sendEvent("error", "Failed to process message: " + e.getMessage());
        }
    }

}