package com.efr.achievementbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "bot")
public class BotVariable {
    private String token;
    private String username;
    private String groupId;
    private String administratorId;
}
