package com.fzq.xiaopotato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class XiaopotatoApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaopotatoApplication.class, args);
    }

}
