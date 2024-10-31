package com.fzq.xiaopotato;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@SpringBootApplication
public class XiaopotatoApplication {

    @Autowired
    private SocketIOServer socketIOServer;

    private static final Logger logger = LoggerFactory.getLogger(XiaopotatoApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(XiaopotatoApplication.class, args);
    }

    @PostConstruct
    public void startSocketIOServer() {
        try {
            socketIOServer.start();
            logger.info("Socket.IO server started successfully on port 8081");
        } catch (Exception e) {
            logger.error("Failed to start Socket.IO server: ", e);
        }
    }

    @PreDestroy
    public void stopSocketIOServer() {
        try {
            if (socketIOServer != null) {
                socketIOServer.stop();
                logger.info("Socket.IO server stopped successfully");
            }
        } catch (Exception e) {
            logger.error("Error stopping Socket.IO server: ", e);
        }
    }

}
