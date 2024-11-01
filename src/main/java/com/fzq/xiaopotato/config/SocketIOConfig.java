package com.fzq.xiaopotato.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.fzq.xiaopotato.common.utils.SocketIOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Autowired
    private SocketIOUtils socketIOUtils;  // 注入 SocketIOUtils

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8081);
        config.setOrigin("*");
        config.setTransports(com.corundumstudio.socketio.Transport.WEBSOCKET, Transport.POLLING);

        SocketIOServer server = new SocketIOServer(config);

        // 注册事件监听器
        server.addConnectListener(socketIOUtils::onConnect);
        server.addDisconnectListener(socketIOUtils::onDisconnect);

        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }
}