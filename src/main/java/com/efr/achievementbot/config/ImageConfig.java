package com.efr.achievementbot.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.*;

@Getter
@Configuration
@ConfigurationProperties(prefix = "achievement.image")
public class ImageConfig {
    private String templatePath = "images/achievement_image_template_1.jpg";
    private Color textColor = Color.WHITE;
    private TextDrawConfig title;
    private TextDrawConfig description;
}