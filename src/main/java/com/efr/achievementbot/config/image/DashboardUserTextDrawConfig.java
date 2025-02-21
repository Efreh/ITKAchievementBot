package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;

import java.awt.Font;

@Getter
@Setter
public class DashboardUserTextDrawConfig {
    /**
     * Имя шрифта для строк списка пользователей.
     */
    private String fontName = "Arial";

    /**
     * Стиль шрифта для строк списка пользователей.
     */
    private int fontStyle = Font.PLAIN;

    /**
     * Размер шрифта для строк списка пользователей.
     */
    private int fontSize = 20;

    public Font getFont() {
        return new Font(fontName, fontStyle, fontSize);
    }
}
