package com.efr.achievementbot.config;

import lombok.Getter;
import lombok.Setter;

import java.awt.Font;
import java.awt.Point;

@Getter
@Setter
public class TextDrawConfig {
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
    private int fontSize = 23;

    /**
     * Координата X для позиционирования текста.
     */
    private int posX = 512;

    /**
     * Координата Y для позиционирования текста.
     */
    private int posY = 600;

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
