package com.efr.achievementbot.service.achievement.image;

import com.efr.achievementbot.config.ImageConfig;
import com.efr.achievementbot.model.UserDB;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;

@Component
public class AchievementImageGenerator {

    private final ImageConfig imageConfig;

    public AchievementImageGenerator(ImageConfig imageConfig) {
        this.imageConfig = imageConfig;
    }

    @SneakyThrows
    public File createAchievementImage(String title, String description) {
        InputStream templateStream = new ClassPathResource(imageConfig.getTemplatePath()).getInputStream();
        BufferedImage image = ImageIO.read(templateStream);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(imageConfig.getTextColor());
        drawCenteredString(g, title, imageConfig.getTitle().getPosition(), imageConfig.getTitle().getFont());
        java.util.List<String> descriptionLines = splitTextIntoLines(description, imageConfig.getDescription().getMaxLineLength());
        int lineHeight = g.getFontMetrics(imageConfig.getDescription().getFont()).getHeight();
        Point currentPosition = new Point(imageConfig.getDescription().getPosition());
        for (String line : descriptionLines) {
            drawCenteredString(g, line, currentPosition, imageConfig.getDescription().getFont());
            currentPosition.y += lineHeight;
        }
        g.dispose();
        File output = File.createTempFile("achievement", ".jpg");
        ImageIO.write(image, "jpg", output);
        return output;
    }

    /**
     * Новый метод для генерации дашборда топ-5 активных пользователей.
     * Использует шаблон "dashboard_image_template_1.jpg".
     */
    @SneakyThrows
    public File createDashboardImage(List<UserDB> topUsers) {
        // Загружаем шаблон дашборда
        InputStream templateStream = new ClassPathResource("images/dashboard_image_template_1.jpg").getInputStream();
        BufferedImage image = ImageIO.read(templateStream);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Используем чёрный цвет для текста
        g.setColor(Color.BLACK);

        // Рисуем заголовок "Топ 5 заклинателей кода" в точке (512,350)
        Font titleFont = imageConfig.getTitle().getFont();
        drawCenteredString(g, "Топ 5 заклинателей кода", new Point(512, 350), titleFont);

        // Начинаем вывод списка пользователей ниже заголовка (начальное значение y = 400)
        int startY = 400;
        Font userFont = imageConfig.getDescription().getFont();
        g.setFont(userFont);
        FontMetrics metrics = g.getFontMetrics(userFont);
        int lineHeight = metrics.getHeight();

        for (int i = 0; i < topUsers.size(); i++) {
            UserDB user = topUsers.get(i);
            String displayName = (user.getUserName() != null && !user.getUserName().trim().isEmpty())
                    ? user.getUserName()
                    : user.getUserTag();
            int score = (user.getWeeklyMessageCount() != null ? user.getWeeklyMessageCount() : 0)
                    + (user.getWeeklyAchievementScore() != null ? user.getWeeklyAchievementScore() : 0);
            String line = String.format("%d. %s - %d очков восхождения", i + 1, displayName, score);
            int textWidth = metrics.stringWidth(line);
            int x = (image.getWidth() - textWidth) / 2;
            int y = startY + i * lineHeight;
            g.drawString(line, x, y);
        }

        g.dispose();
        File output = File.createTempFile("dashboard", ".jpg");
        ImageIO.write(image, "jpg", output);
        return output;
    }

    private void drawCenteredString(Graphics2D g, String text, Point position, Font font) {
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = position.x - metrics.stringWidth(text) / 2;
        int y = position.y - metrics.getHeight() / 2 + metrics.getAscent();
        g.drawString(text, x, y);
    }

    private java.util.List<String> splitTextIntoLines(String text, int maxLineLength) {
        java.util.List<String> lines = new java.util.ArrayList<>();
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
