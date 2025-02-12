package com.efr.ITKAchievementBot.bot.handlers.achievements;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "achievements")
@Getter
@Setter
public class AchievementConfig {
    private Map<String, AchievementDefinition> achievements = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Loaded achievements: {}", achievements);
    }
}