package com.efr.achievementbot.service.achievement.notification;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.model.UserDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

/**
 * Сервис для асинхронной отправки уведомлений о достижениях.
 */
@Slf4j
@Service
public class AchievementNotificationService {

    /**
     * Отправляет уведомление о получении достижения асинхронно.
     *
     * @param user             пользователь, получивший достижение
     * @param message          исходное сообщение из Telegram
     * @param bot              экземпляр Telegram-бота
     * @param imageFile        сгенерированный файл изображения достижения
     * @param achievementTitle заголовок достижения
     */
    @Async("taskExecutor")
    public void sendAchievementNotification(UserDB user, Message message, JavaCodeBot bot, File imageFile, String achievementTitle) {
        log.info("Начало отправки уведомления о достижении '{}' для пользователя '{}'", achievementTitle, user.getUserName());
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(user.getChatId().toString());
        sendPhoto.setMessageThreadId(message.getMessageThreadId());
        sendPhoto.setCaption(user.getUserTag() + " получает достижение: " + achievementTitle + "!");
        sendPhoto.setPhoto(new InputFile(imageFile));
        try {
            bot.execute(sendPhoto);
            log.info("Уведомление о достижении '{}' успешно отправлено пользователю '{}'", achievementTitle, user.getUserName());
        } catch (TelegramApiException e) {
            log.error("Ошибка при асинхронной отправке уведомления о достижении '{}'", achievementTitle, e);
        } finally {
            imageFile.delete();
        }
    }

    /**
     * Отправляет уведомление о получении кастомного достижения асинхронно.
     *
     * @param userTag тег пользователя (формат @username)
     * @param chatId  ID чата
     * @param bot     экземпляр Telegram-бота
     * @param image   сгенерированный файл изображения достижения
     * @param title   заголовок достижения
     */
    @Async("taskExecutor")
    public void sendCustomAchievementNotification(String userTag, Long chatId, JavaCodeBot bot, File image, String title) {
        log.info("Начало отправки кастомного уведомления о достижении '{}' для пользователя с тегом '{}'", title, userTag);
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId.toString());
        photo.setCaption(userTag + " получает достижение: " + title + "!");
        try {
            photo.setPhoto(new InputFile(image));
            bot.execute(photo);
            log.info("Кастомное уведомление о достижении '{}' успешно отправлено пользователю с тегом '{}'", title, userTag);
        } catch (TelegramApiException e) {
            log.error("Ошибка при асинхронной отправке кастомного уведомления о достижении '{}'", title, e);
        } finally {
            image.delete();
        }
    }
}
