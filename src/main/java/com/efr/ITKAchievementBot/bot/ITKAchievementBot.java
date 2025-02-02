package com.efr.ITKAchievementBot.bot;

import com.efr.ITKAchievementBot.config.BotVariable;
import com.efr.ITKAchievementBot.model.UserDB;
import com.efr.ITKAchievementBot.service.AchievementService;
import com.efr.ITKAchievementBot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ITKAchievementBot extends TelegramLongPollingBot {
    private final UserService userService;
    private final BotVariable botVariable;
    private final AchievementService achievementService;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        Message message = update.getMessage();

        // Получаем ID чата (группы или личного чата)
        Long chatId = message.getChatId();
        int threadChatId = Optional.ofNullable(message.getMessageThreadId()).orElse(0);

        // Получаем отправителя сообщения
        User telegramUser = message.getFrom();
        String userName = telegramUser.getUserName();
        Long userId = telegramUser.getId(); // Уникальный ID пользователя в Telegram

        // Сохраняем/обновляем пользователя
        UserDB user = userService.findByTelegramIdAndChatId(userId,chatId);
        if (user == null) {
            user = new UserDB();
            user.setTelegramId(userId);
            user.setUserName(userName);
            user.setChatId(chatId); // Сохраняем ID чата, где было отправлено сообщение
            user.setMessageCount(0);
        }

        user.setMessageCount(user.getMessageCount() + 1);
        userService.saveUser(user);

        // Проверка достижений
        achievementService.checkAchievements(user, message, this);

        log.info("Сообщение от {} (@{}) в чате {} + подчат {}",
                userId,
                userName,
                chatId,
                threadChatId);
    }

    @Override
    public String getBotUsername() {
        return botVariable.getUsername();
    }

    @Override
    public String getBotToken() {
        return botVariable.getToken();
    }
}