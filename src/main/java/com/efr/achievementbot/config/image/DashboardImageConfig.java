package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.awt.Color;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "dashboard.image")
public class DashboardImageConfig {
    /**
     * Путь к шаблону дашборда.
     */
    private String templatePath = "images/dashboard_image_template_1.jpg";

    /**
     * Цвет текста для дашборда (по умолчанию черный).
     */
    private String textColor = "#000000";

    /**
     * Конфигурация для заголовка дашборда.
     */
    private DashboardImageTextDrawConfig title = new DashboardImageTextDrawConfig();

    /**
     * Конфигурация для строк списка пользователей.
     */
    private DashboardUserTextDrawConfig userText = new DashboardUserTextDrawConfig();

    /**
     * Начальная координата Y для вывода списка пользователей.
     */
    private int listStartY = 400;

    public Color getTextColor() {
        return Color.decode(textColor);
    }
}