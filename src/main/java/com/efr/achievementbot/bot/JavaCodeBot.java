package com.efr.achievementbot.bot;

import com.efr.achievementbot.bot.admin.AdminCommandHandler;
import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.user.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaCodeBot extends TelegramLongPollingBot {

    private final UserActivityService userActivityService;
    private final BotProperties botProperties;
    private final AchievementService achievementService;
    private final AdminCommandHandler adminCommandHandler;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            log.debug("Пропуск обновления без сообщения.");
            return;
        }

        Message message = update.getMessage();
        Long chatId = message.getChatId();
        log.info("Получено сообщение из чата ID: {}", chatId);

        if (!isAllowedChat(chatId, message.getFrom().getId())) {
            log.warn("Чат {} не разрешён для обработки, отправитель ID: {}", chatId, message.getFrom().getId());
            return;
        }

        // Обработка команд админа
        if (isAdmin(message.getFrom().getId())) {
            adminCommandHandler.handleAdminCommand(update, this);
        }

        // Обновление статистики пользователя и проверка достижений
        UserDB user = userActivityService.updateUserActivity(message);
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

    /**
     * Проверяет, является ли отправитель администратором.
     */
    private boolean isAdmin(Long userId) {
        return userId.toString().equals(botProperties.getAdministratorId());
    }

    /**
     * Разрешает обработку сообщений либо для указанной группы, либо для личных сообщений админа.
     */
    private boolean isAllowedChat(Long chatId, Long senderId) {
        return chatId.toString().equals(botProperties.getGroupId()) ||
                (isAdmin(senderId) && chatId > 0);
    }
}