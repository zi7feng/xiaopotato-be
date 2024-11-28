package com.fzq.xiaopotato.common.utils;

import com.corundumstudio.socketio.SocketIOClient;
import com.fzq.xiaopotato.XiaopotatoApplication;
import com.fzq.xiaopotato.constant.NotificationConstant;
import com.fzq.xiaopotato.model.vo.NotificationVO;
import com.fzq.xiaopotato.service.NotificationService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.fzq.xiaopotato.common.NotificationType.RECOMMEND;

@Slf4j
@Component
public class SocketIOUtils {

    private static final Logger logger = LoggerFactory.getLogger(SocketIOUtils.class);

    private final Map<Long, SocketIOClient> onlineUsers = new ConcurrentHashMap<>();

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String ONLINE_USER_KEY_PREFIX = "user:online:";

    private static int sendRecommendation = 0;

    public void onConnect(SocketIOClient client) {
        logger.info("onConnect");
//        String token = client.getHandshakeData().getSingleUrlParam("token");
        String token = URLDecoder.decode(client.getHandshakeData().getSingleUrlParam("token"), StandardCharsets.UTF_8);

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
            notificationService.markNotificationsAsPush(userId);
            log.info("Client connected: {}", client.getSessionId());

        }
    }

    public void onDisconnect(SocketIOClient client) {
//        String token = URLDecoder.decode(client.getHandshakeData().getSingleUrlParam("token"), StandardCharsets.UTF_8);

        String token = URLDecoder.decode(client.getHandshakeData().getSingleUrlParam("token"), StandardCharsets.UTF_8);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }
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
        boolean inMemory = onlineUsers.containsKey(userId) && onlineUsers.get(userId).isChannelOpen();
        boolean inRedis = Boolean.TRUE.equals(redisTemplate.hasKey(ONLINE_USER_KEY_PREFIX + userId));

        if (inMemory != inRedis) {
            if (!inRedis) {
                onlineUsers.remove(userId);
            }
            return inRedis;
        }
        return inMemory || inRedis;
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


    public void handlePullAllNotifications(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token != null) {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("id", Long.class);

            if (userId != null) {
                // 获取所有通知（包括已读和未读）
                List<NotificationVO> allNotifications = notificationService.getAllNotifications(userId);

                // 推送所有通知到客户端
                allNotifications.forEach(notification -> client.sendEvent("pull", notification));

                log.info("Pushed {} notifications (all) to user {}", allNotifications.size(), userId);
            }
        }
    }

    public void handleHeartbeat(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        if (token != null) {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            Long userId = claims.get("id", Long.class);
            if (userId != null) {
                // 更新 Redis 中用户的在线状态
                redisTemplate.opsForValue().set(ONLINE_USER_KEY_PREFIX + userId, "online", 1, TimeUnit.MINUTES);

                // 更新内存中的用户状态
                if (!onlineUsers.containsKey(userId) || !onlineUsers.get(userId).equals(client)) {
                    onlineUsers.put(userId, client);
                }

                if (sendRecommendation % 40 == 1) {
                    sendRecommendationKnowledge(client);
                    sendRecommendation = 1;
                }
                sendRecommendation += 1;
                log.info("Received heartbeat from user {}", userId);
            }
        }
    }

    public void sendRecommendationKnowledge(SocketIOClient client) {
        String randomContent = getRandom();

        // 创建 NotificationVO 实例并设置内容
        NotificationVO notification = new NotificationVO();
        notification.setContent(randomContent);
        notification.setNotificationType(String.valueOf(RECOMMEND));
        notification.setTimestamp(LocalDateTime.now().toString());

        // 发送通知
        client.sendEvent("notification", notification);
    }

    private String getRandom() {
        Random random = new Random();
        return NotificationConstant.FUN_SENTENCES.get(random.nextInt(NotificationConstant.FUN_SENTENCES.size()));
    }

}