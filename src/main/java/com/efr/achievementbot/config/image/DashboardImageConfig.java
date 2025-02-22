package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
public class DashboardImageConfig {
    /**
     * Путь к шаблону дашборда.
     */
    private String templatePath = "images/dashboard_image_template_1.jpg";

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
}