package com.efr.ITKAchievementBot.bot;

import com.efr.ITKAchievementBot.config.BotVariable;
import com.efr.ITKAchievementBot.model.UserDB;
import com.efr.ITKAchievementBot.service.AchievementService;
import com.efr.ITKAchievementBot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ITKAchievementBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final BotVariable botVariable;
    private final AchievementService achievementService;

    private static final Pattern CUSTOM_ACHIEVEMENT_PATTERN = Pattern.compile(
            "^/award_custom\\s+(?<userTag>@\\w+)\\s+(?<title>[^;]+)\\s*;\\s*(?<description>.+)$",
            Pattern.CASE_INSENSITIVE
    );


    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage()) return;

        if (update.hasMessage() && update.getMessage().hasText()) {
            handleAdminCommands(update);
        }

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        int threadChatId = Optional.ofNullable(message.getMessageThreadId()).orElse(0);

        // Получаем данные отправителя
        org.telegram.telegrambots.meta.api.objects.User telegramUser = message.getFrom();
        String userName = telegramUser.getUserName();
        Long userId = telegramUser.getId();
        String userTag = telegramUser.getUserName() != null ?
                "@" + telegramUser.getUserName() : null;

        // Находим пользователя по telegramId и chatId, либо создаём нового
        UserDB user = userService.findByTelegramIdAndChatId(userId, chatId);
        if (user == null) {
            user = new UserDB();
            user.setTelegramId(userId);
            user.setChatId(chatId);
            user.setUserTag(userTag);
            user.setUserName(message.getFrom().getUserName());
            user.setMessageCount(0);
            user.setReactionCount(0);
            user.setMediaCount(0);
            user.setStickerCount(0);
        }else if (userTag != null && !userTag.equals(user.getUserTag())) {
            user.setUserTag(userTag);
        }

        // Увеличиваем счётчик сообщений
        user.setMessageCount(user.getMessageCount() + 1);

        // Обновляем счётчик медиа
        if (message.hasPhoto() || message.hasVideo() || message.hasDocument() || message.hasVoice()) {
            user.setMediaCount(user.getMediaCount() + 1);
        }

        // Обновляем счётчик стикеров
        if (message.hasSticker()) {
            user.setStickerCount(user.getStickerCount() + 1);
        }

        userService.saveUser(user);

        // Проверяем достижения
        achievementService.checkAchievements(user, message, this);

        log.info("Сообщение от {} (@{}) в чате {} + подчат {}",
                userId, userName, chatId, threadChatId);
    }

    @Override
    public String getBotUsername() {
        return botVariable.getUsername();
    }

    @Override
    public String getBotToken() {
        return botVariable.getToken();
    }

    private void handleAdminCommands(Update update) {
        Message message = update.getMessage();
        String text = message.getText();
        Long chatId = message.getChatId();
        Long senderId = message.getFrom().getId();

        Matcher matcher = CUSTOM_ACHIEVEMENT_PATTERN.matcher(text);
        if (matcher.matches()) {
            String userTag = matcher.group("userTag");
            String title = matcher.group("title").trim();
            String description = matcher.group("description").trim();

            if (!userTag.startsWith("@")) {
                sendError(chatId, "Тег пользователя должен начинаться с @");
                return;
            }

            Long groupChatId = Long.parseLong(botVariable.getGroupId());
            achievementService.awardCustomAchievement(
                    userTag,
                    title,
                    description,
                    groupChatId,
                    this
            );
        } else if (text.startsWith("/award_custom")) {
            sendError(chatId, """
                Неверный формат команды. Используйте:
                /award_custom @username Название; Описание
                Пример:
                /award_custom @user123 Лучший автор; За выдающийся вклад в развитие сообщества
                """);
        }
    }

    private void sendError(Long chatId, String message) {
        try {
            execute(new SendMessage(chatId.toString(), message));
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
}
