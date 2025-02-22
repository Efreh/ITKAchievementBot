package com.efr.achievementbot.config.image;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class AchievementImageTextDrawTitleConfig {
    /**
     * Имя шрифта (например, "Arial").
     */
    private String fontName = "Arial";

    /**
     * Стиль шрифта (например, Font.PLAIN, Font.BOLD, Font.ITALIC).
     */
    private int fontStyle = Font.PLAIN;

    /**
     * Размер шрифта.
     */
    private int fontSize = 33;

    /**
     * Координата X для позиционирования текста.
     */
    private int posX = 512;

    /**
     * Координата Y для позиционирования текста.
     */
    private int posY = 560;

    /**
     * Максимальная длина строки для переноса текста.
     */
    private int maxLineLength = 35;

    /**
     * Формирует объект Font на основе настроек.
     */
    public Font getFont() {
        return new Font(fontName, fontStyle, fontSize);
    }

    /**
     * Формирует объект Point для позиционирования текста.
     */
    public Point getPosition() {
        return new Point(posX, posY);
    }
}
