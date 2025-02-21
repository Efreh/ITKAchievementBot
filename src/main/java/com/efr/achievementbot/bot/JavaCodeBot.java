package com.efr.achievementbot.bot;

import com.efr.achievementbot.bot.admin.menu.AdminMenuHandler;
import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.bot.ThreadTrackingService;
import com.efr.achievementbot.service.goblin.GoblinService;
import com.efr.achievementbot.service.user.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaCodeBot extends TelegramLongPollingBot {

    private final UserActivityService userActivityService;
    private final BotProperties botProperties;
    private final AchievementService achievementService;
    private final AdminMenuHandler adminMenuHandler;
    private final GoblinService goblinService;
    private final ThreadTrackingService threadTrackingService;

    @Override
    public void onUpdateReceived(Update update) {
        // 1. Обработка колбэка от "гоблинов" (Inline кнопки)
        if (update.hasCallbackQuery()) {
            goblinService.handleGoblinCatch(update.getCallbackQuery());
            return;
        }

        // 2. Если нет сообщения, пропускаем
        if (!update.hasMessage()) {
            log.debug("Пропуск обновления без сообщения.");
            return;
        }

        Message message = update.getMessage();
        Chat chat = message.getChat();  // информация о чате
        Long chatId = chat.getId();
        Long senderId = message.getFrom().getId();

        log.info("Получено сообщение из чата ID: {}", chatId);

        // Если в сообщении есть "ThreadId" (т.е. это саб-тред в группе)
        Integer threadId = message.getMessageThreadId();
        if (threadId != null) {
            threadTrackingService.registerThread(threadId);
        }

        // 3. Если это группа (или супер-группа) - обрабатываем только механику достижений, гоблинов и т.п.
        //    Админское меню в группе не показываем.
        if (chat.isGroupChat() || chat.isSuperGroupChat()) {
            // Можно дополнительно проверить, что chatId совпадает с botProperties.getGroupId()
            // Если хотите игнорировать другие группы.
            if (!chatId.toString().equals(botProperties.getGroupId())) {
                log.warn("Чат {} не соответствует groupId бота, игнорируем.", chatId);
                return;
            }

            // Допустим, выполняем логику, связанную с сообщениями (счётчики, достижения):
            UserDB user = userActivityService.updateUserActivity(message);
            achievementService.checkAchievements(user, message, this);
            // Если нужно, сюда можно вставить handleGoblinSpawn() или другую групповую логику
            return;
        }

        // 4. Если это приватный (личный) чат:
        if (chat.isUserChat()) {
            // Проверяем, админ ли отправитель
            if (isAdmin(senderId)) {
                // Логика админ-меню
                adminMenuHandler.handleAdminCommand(update, this);
            } else {
                // Не админ — при желании можно ответить "Вы не админ" или вообще игнорировать
                log.info("Сообщение от не-админа в личном чате, пропускаем или отвечаем");
                // Пример:
                // sendSimpleMessage(chatId, "Извините, эта функция доступна только администратору");
            }
            return;
        }

        // 5. Если это канал или какой-то другой тип (редко), можно игнорировать
        log.debug("Чат {} имеет тип, не являющийся group/supergroup/private, пропуск", chatId);
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
}