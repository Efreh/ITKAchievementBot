package com.efr.achievementbot.service.achievement.image;

import com.efr.achievementbot.config.image.AchievementImageConfig;
import com.efr.achievementbot.config.image.DashboardImageConfig;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.service.config.BotConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementImageGenerator {

    private final AchievementImageConfig achievementImageConfig;
    private final DashboardImageConfig dashboardImageConfig;
    private final BotConfigService botConfigService;

    /**
     * Генерирует изображение достижения с использованием настроек из AchievementImageConfig.
     */
    public File createAchievementImage(String title, String description) {
        try {
            InputStream templateStream = new ClassPathResource(achievementImageConfig.getTemplatePath()).getInputStream();
            BufferedImage image = ImageIO.read(templateStream);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.decode(botConfigService.getConfig().getAchievementTextColor()));
            drawCenteredString(g, title, achievementImageConfig.getTitle().getPosition(), achievementImageConfig.getTitle().getFont());
            List<String> descriptionLines = splitTextIntoLines(description, achievementImageConfig.getDescription().getMaxLineLength());
            int lineHeight = g.getFontMetrics(achievementImageConfig.getDescription().getFont()).getHeight();
            Point currentPosition = new Point(achievementImageConfig.getDescription().getPosition());
            for (String line : descriptionLines) {
                drawCenteredString(g, line, currentPosition, achievementImageConfig.getDescription().getFont());
                currentPosition.y += lineHeight;
            }
            g.dispose();
            File output = File.createTempFile("achievement", ".jpg");
            ImageIO.write(image, "jpg", output);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Генерирует изображение дашборда топ-5 активных пользователей с использованием настроек из DashboardImageConfig.
     */
    public File createDashboardImage(List<UserDB> topUsers) {
        try {
            InputStream templateStream = new ClassPathResource(dashboardImageConfig.getTemplatePath()).getInputStream();
            BufferedImage image = ImageIO.read(templateStream);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(Color.decode(botConfigService.getConfig().getDashboardTextColor()));

            // Рисуем заголовок дашборда
            drawCenteredString(g, dashboardImageConfig.getTitle().getText(),
                    dashboardImageConfig.getTitle().getPosition(),
                    dashboardImageConfig.getTitle().getFont());

            // Выводим список пользователей, начиная с заданной координаты Y
            int startY = dashboardImageConfig.getListStartY();
            g.setFont(dashboardImageConfig.getUserText().getFont());
            FontMetrics metrics = g.getFontMetrics(dashboardImageConfig.getUserText().getFont());
            int lineHeight = metrics.getHeight();

            for (int i = 0; i < topUsers.size(); i++) {
                UserDB user = topUsers.get(i);
                String displayName = (user.getUserName() != null && !user.getUserName().trim().isEmpty())
                        ? user.getUserName() : user.getUserTag();
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Рисует текст по центру относительно заданной точки.
     */
    private void drawCenteredString(Graphics2D g, String text, Point position, Font font) {
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        int x = position.x - metrics.stringWidth(text) / 2;
        int y = position.y - metrics.getHeight() / 2 + metrics.getAscent();
        g.drawString(text, x, y);
    }

    /**
     * Разбивает текст на строки с ограничением по количеству символов.
     */
    private List<String> splitTextIntoLines(String text, int maxLineLength) {
        List<String> lines = new java.util.ArrayList<>();
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