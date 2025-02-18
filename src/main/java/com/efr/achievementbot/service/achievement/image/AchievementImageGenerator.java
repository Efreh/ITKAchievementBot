package com.efr.achievementbot.service.achievement.image;

import com.efr.achievementbot.config.ImageConfig;
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

    // Внедрение настроек для изображений достижений
    private final ImageConfig imageConfig;

    public AchievementImageGenerator(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;
    }

    /**
     * Генерирует изображение достижения, используя внешний конфиг для параметров текста и шаблона.
     *
     * @param title       заголовок достижения
     * @param description описание достижения
     * @return сгенерированный файл изображения
     */
    @SneakyThrows
    public File createAchievementImage(String title, String description) {
        // Загрузка шаблона изображения из classpath
        InputStream templateStream = new ClassPathResource(imageConfig.getTemplatePath()).getInputStream();
        BufferedImage image = ImageIO.read(templateStream);

        // Создание графического контекста для рисования текста
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(imageConfig.getTextColor());

        // Рисование заголовка достижения с использованием настроек из конфигурации
        drawCenteredString(g, title, imageConfig.getTitle().getPosition(), imageConfig.getTitle().getFont());

        // Разбивка описания на строки с учетом максимальной длины строки из настроек
        List<String> descriptionLines = splitTextIntoLines(description, imageConfig.getDescription().getMaxLineLength());
        // Получение высоты строки для выбранного шрифта описания
        int lineHeight = g.getFontMetrics(imageConfig.getDescription().getFont()).getHeight();
        // Начальная позиция для описания
        Point currentPosition = new Point(imageConfig.getDescription().getPosition());
        for (String line : descriptionLines) {
            drawCenteredString(g, line, currentPosition, imageConfig.getDescription().getFont());
            currentPosition.y += lineHeight; // Переход на следующую строку
        }
        g.dispose();

        // Сохранение изображения во временный файл
        File output = File.createTempFile("achievement", ".jpg");
        ImageIO.write(image, "jpg", output);

        return output;
    }

    /**
     * Рисует центрированный текст в заданной позиции.
     *
     * @param g        графический контекст
     * @param text     текст для отображения
     * @param position центр, относительно которого нужно отобразить текст
     * @param font     шрифт текста
     */
    private void drawCenteredString(Graphics2D g, String text, Point position, Font font) {
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = position.x - metrics.stringWidth(text) / 2;
        int y = position.y - metrics.getHeight() / 2 + metrics.getAscent();
        g.drawString(text, x, y);
    }

    /**
     * Разбивает переданный текст на строки, не превышающие максимальную длину.
     *
     * @param text           исходный текст
     * @param maxLineLength  максимальная длина строки
     * @return список строк
     */
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
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }
}