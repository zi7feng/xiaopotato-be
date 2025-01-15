package com.fzq.xiaopotato.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    private final SocketIOUtils socketIOUtils;

    @Value("${socketio.host}") // 从配置文件中读取主机名
    private String host;

    @Value("${socketio.port}") // 从配置文件中读取端口号
    private int port;

    @Autowired
    public SocketIOConfig(SocketIOUtils socketIOUtils) {
        this.socketIOUtils = socketIOUtils;
    }

    @Bean
    @Lazy
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("*");
        config.setTransports(Transport.WEBSOCKET, Transport.POLLING);

        SocketIOServer server = new SocketIOServer(config);

        server.addConnectListener(socketIOUtils::onConnect);
        server.addDisconnectListener(socketIOUtils::onDisconnect);
        server.addEventListener("message", String.class, (client, data, ackSender) -> {
            socketIOUtils.handleMessage(client, data);
        });
        server.addEventListener("pull", String.class, (client, data, ackSender) -> {
            socketIOUtils.handlePullAllNotifications(client);
        });
        server.addEventListener("heartbeat", Map.class, (client, data, ackSender) -> {
            socketIOUtils.handleHeartbeat(client);
        });

        return server;
    }

    @Bean
    @DependsOn("socketIOServer")
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }
}
