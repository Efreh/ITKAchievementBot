package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;

import java.awt.Font;
import java.awt.Point;

@Getter
@Setter
public class DashboardImageTextDrawConfig {
    /**
     * Текст заголовка дашборда.
     */
    private String text = "Топ 5 заклинателей кода";

    /**
     * Имя шрифта заголовка.
     */
    private String fontName = "Arial";

    /**
     * Стиль шрифта заголовка.
     */
    private int fontStyle = Font.BOLD;

    /**
     * Размер шрифта заголовка.
     */
    private int fontSize = 30;

    /**
     * Координата X для заголовка.
     */
    private int posX = 512;

    /**
     * Координата Y для заголовка.
     */
    private int posY = 350;

    public Font getFont() {
        return new Font(fontName, fontStyle, fontSize);
    }

    public Point getPosition() {
        return new Point(posX, posY);
    }
}