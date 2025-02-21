package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.Color;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "achievement.image")
public class AchievementImageConfig {
    /**
     * Путь к шаблону изображения достижения.
     */
    private String templatePath = "images/achievement_image_template_1.jpg";

    /**
     * Цвет текста в формате HEX (например, "#FFFFFF" для белого).
     */
    private String textColor = "#FFFFFF";

    /**
     * Настройки для отрисовки заголовка достижения.
     */
    private AchievementImageTextDrawConfig title = new AchievementImageTextDrawConfig();

    /**
     * Настройки для отрисовки описания достижения.
     */
    private AchievementImageTextDrawConfig description = new AchievementImageTextDrawConfig();

    public Color getTextColor() {
        return Color.decode(textColor);
    }
}