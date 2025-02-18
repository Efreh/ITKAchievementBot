package com.efr.achievementbot.bot;

import com.efr.achievementbot.bot.admin.AdminCommandHandler;
import com.efr.achievementbot.config.BotProperties;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.user.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ITKAchievementBot extends TelegramLongPollingBot {

    private final UserActivityService userActivityService;
    private final BotProperties botProperties;
    private final AchievementService achievementService;
    private final AdminCommandHandler adminCommandHandler;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return; // Игнорируем обновления без сообщений

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        log.info("Получено сообщение из чата ID: {}", chatId);

        if (!isAllowedChat(chatId, message.getFrom().getId())) {
            log.info("Чат {} не разрешен для обработки", chatId);
            return;
        }

        // Если сообщение от администратора – делегируем обработку админским командам
        if (isAdmin(message.getFrom().getId())) {
            adminCommandHandler.handleAdminCommand(update, this);
        }

        // Обновляем статистику активности пользователя через специальный сервис
        UserDB user = userActivityService.updateUserActivity(message);

        // Проверка достижений
        achievementService.checkAchievements(user, message, this);
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    // Проверка, является ли отправитель администратором
    private boolean isAdmin(Long userId) {
        return userId.toString().equals(botProperties.getAdministratorId());
    }

    // Разрешаем либо группу из конфига, либо личные сообщения админа
    private boolean isAllowedChat(Long chatId, Long senderId) {
        return chatId.toString().equals(botProperties.getGroupId()) ||
                (isAdmin(senderId) && chatId > 0);
    }
}