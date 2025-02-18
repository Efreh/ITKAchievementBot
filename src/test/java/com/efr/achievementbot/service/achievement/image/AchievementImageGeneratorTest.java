package com.efr.achievementbot.service.achievement.image;

import com.efr.achievementbot.config.ImageConfig;
import com.efr.achievementbot.config.TextDrawConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;

public class AchievementImageGeneratorTest {

    @Test
    public void testCreateAchievementImage() throws Exception {
        // Настраиваем dummy-конфигурацию для генератора изображений
        ImageConfig imageConfig = new ImageConfig();
        imageConfig.setTemplatePath("images/achievement_image_template_1.jpg");
        imageConfig.setTextColor("#FFFFFF");

        TextDrawConfig titleConfig = new TextDrawConfig();
        titleConfig.setFontName("Arial");
        titleConfig.setFontStyle(1); // Font.BOLD
        titleConfig.setFontSize(35);
        titleConfig.setPosX(512);
        titleConfig.setPosY(560);
        titleConfig.setMaxLineLength(35);
        imageConfig.setTitle(titleConfig);

        TextDrawConfig descConfig = new TextDrawConfig();
        descConfig.setFontName("Arial");
        descConfig.setFontStyle(0); // Font.PLAIN
        descConfig.setFontSize(23);
        descConfig.setPosX(512);
        descConfig.setPosY(600);
        descConfig.setMaxLineLength(35);
        imageConfig.setDescription(descConfig);

        // Создаем экземпляр генератора изображений с конфигурацией
        AchievementImageGenerator generator = new AchievementImageGenerator(imageConfig);

        // Генерируем изображение достижения
        File imageFile = generator.createAchievementImage("Test Title", "Test description that is sufficiently long to require line breaks");

        // Проверяем, что файл с изображением создан и существует
        assertNotNull(imageFile);
        assertTrue(imageFile.exists());

        // Удаляем временный файл после проверки
        imageFile.delete();
    }
}
