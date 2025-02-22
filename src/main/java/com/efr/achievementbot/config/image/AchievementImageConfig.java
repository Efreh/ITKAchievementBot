package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class AchievementImageConfig {
    /**
     * Путь к шаблону изображения достижения.
     */
    private String templatePath = "images/achievement_image_template_1.jpg";

    /**
     * Настройки для отрисовки заголовка достижения.
     */
    private AchievementImageTextDrawTitleConfig title = new AchievementImageTextDrawTitleConfig();

    /**
     * Настройки для отрисовки описания достижения.
     */
    private AchievementImageTextDrawDescriptionConfig description = new AchievementImageTextDrawDescriptionConfig();
}