package com.logger.agentmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(
        basePackages = "com.logger.agentmodule.client"
)
public class AgentModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentModuleApplication.class, args);
    }

}
