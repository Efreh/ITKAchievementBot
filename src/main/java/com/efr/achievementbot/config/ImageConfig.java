package com.efr.achievementbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.Color;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "achievement.image")
public class ImageConfig {
    /**
     * Путь к шаблону изображения.
     */
    private String templatePath = "images/achievement_image_template_1.jpg";

    /**
     * Цвет текста в формате HEX (например, "#FFFFFF" для белого).
     */
    private String textColor = "#FFFFFF";

    /**
     * Настройки для отрисовки заголовка.
     */
    private TextDrawConfig title = new TextDrawConfig();

    /**
     * Настройки для отрисовки описания.
     */
    private TextDrawConfig description = new TextDrawConfig();

    /**
     * Преобразует строку HEX в объект Color.
     */
    public Color getTextColor() {
        return Color.decode(textColor);
    }
}