package com.efr.ITKAchievementBot.bot.service;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class AchievementImageGenerator {

    private static final String TEMPLATE_PATH = "images/achievement_image_template_1.jpg";
    private static final int IMAGE_WIDTH = 1024;
    private static final int IMAGE_HEIGHT = 1024;

    // Координаты и параметры текста
    private static final Point TITLE_POSITION = new Point(512, 560);
    private static final Point DESCRIPTION_POSITION = new Point(512, 600);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 35);
    private static final Font DESCRIPTION_FONT = new Font("Arial", Font.PLAIN, 23);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final int MAX_LINE_LENGTH = 35; // Максимальная длина строки

    @SneakyThrows
    public File createAchievementImage(String title, String description) {
        // Загрузка шаблона
        InputStream templateStream = new ClassPathResource(TEMPLATE_PATH).getInputStream();
        BufferedImage image = ImageIO.read(templateStream);

        // Создание графического контекста
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Настройка шрифта и цвета
        g.setColor(TEXT_COLOR);

        // Рисуем название достижения
        drawCenteredString(g, title, TITLE_POSITION, TITLE_FONT);

        // Разбиваем описание на строки
        List<String> descriptionLines = splitTextIntoLines(description, MAX_LINE_LENGTH);

        // Рисуем описание построчно
        int lineHeight = g.getFontMetrics(DESCRIPTION_FONT).getHeight();
        Point currentPosition = new Point(DESCRIPTION_POSITION);

        for (String line : descriptionLines) {
            drawCenteredString(g, line, currentPosition, DESCRIPTION_FONT);
            currentPosition.y += lineHeight; // Переход на следующую строку
        }

        // Завершаем работу с графикой
        g.dispose();

        // Сохраняем во временный файл
        File output = File.createTempFile("achievement", ".jpg");
        ImageIO.write(image, "jpg", output);

        return output;
    }

    private void drawCenteredString(Graphics2D g, String text, Point position, Font font) {
        // Устанавливаем шрифт
        g.setFont(font);

        // Получаем метрики шрифта
        FontMetrics metrics = g.getFontMetrics(font);

        // Рассчитываем позицию для центрирования
        int x = position.x - metrics.stringWidth(text) / 2;
        int y = position.y - metrics.getHeight() / 2 + metrics.getAscent();

        // Рисуем текст
        g.drawString(text, x, y);
    }

    private List<String> splitTextIntoLines(String text, int maxLineLength) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();
            }
            currentLine.append(word).append(" ");
        }

        // Добавляем последнюю строку
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }
}