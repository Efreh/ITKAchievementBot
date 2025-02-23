package com.efr.achievementbot.bot;

import com.efr.achievementbot.bot.admin.menu.AdminMenuHandler;
import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.model.BotConfigDB;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.bot.ThreadTrackingService;
import com.efr.achievementbot.service.config.BotConfigService;
import com.efr.achievementbot.service.goblin.GoblinService;
import com.efr.achievementbot.service.user.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaCodeBot extends TelegramLongPollingBot {

    private final UserActivityService userActivityService;
    private final BotProperties botProperties;           // хранит token, username, secretKey
    private final BotConfigService botConfigService;       // сервис для чтения/записи adminId, groupId из БД

    private final AchievementService achievementService;
    private final AdminMenuHandler adminMenuHandler;
    private final GoblinService goblinService;
    private final ThreadTrackingService threadTrackingService;

    @Override
    public void onUpdateReceived(Update update) {
        // 1. Обработка CallbackQuery от "гоблинов" (Inline-кнопки)
        if (update.hasCallbackQuery()) {
            goblinService.handleGoblinCatch(update.getCallbackQuery());
            return;
        }

        // 2. Если нет сообщения — пропускаем
        if (!update.hasMessage()) {
            log.debug("Пропуск обновления без сообщения.");
            return;
        }

        Message message = update.getMessage();
        Chat chat = message.getChat();
        Long chatId = chat.getId();
        Long senderId = message.getFrom().getId();

        // 3. Проверяем команды (обязательно перед остальной логикой)
        String text = message.getText();
        if (text != null) {
            // Команда для регистрации админа: /register_admin secretKey
            if (text.startsWith("/register_admin")) {
                handleRegisterAdminCommand(message);
                return; // сразу выходим после обработки
            }
            // Команда для регистрации группы: /register_chat
            if (text.startsWith("/register_chat")) {
                handleRegisterChatCommand(message);
                return; // сразу выходим
            }
        }

        // 4. Регистрируем threadId, если это саб-тред (Thread в группах)
        Integer threadId = message.getMessageThreadId();
        if (threadId != null) {
            threadTrackingService.registerThread(threadId);
        }

        // 5. Проверяем, что это за тип чата?
        if (chat.isGroupChat() || chat.isSuperGroupChat()) {
            // Получаем из БД текущие настройки
            BotConfigDB cfg = botConfigService.getConfig();
            Long savedGroupId = cfg.getGroupId();

            // Если groupId ещё не задан или не совпадает — игнорируем
            if (savedGroupId == null || !savedGroupId.equals(chatId)) {
                log.warn("Сообщение из группы {}, но зарегистрирована другая группа {}. Пропускаем.", chatId, savedGroupId);
                return;
            }

            // Логика для группы: обновляем активность пользователя, проверяем достижения...
            UserDB user = userActivityService.updateUserActivity(message);
            achievementService.checkAchievements(user, message, this);
            // и т.д.
            return;
        }

        if (chat.isUserChat()) {
            // Личный чат — проверим, админ ли это
            if (isAdmin(senderId)) {
                // Админ-меню
                adminMenuHandler.handleAdminCommand(update, this);
            } else {
                // Если хотите — отвечайте, что эта функция только для админа
                sendSimpleMessage(chatId, "Извините, но это личный чат бота, доступен только администратору.");
            }
            return;
        }

        // Если это канал или какой-то другой тип — игнорируем
        log.debug("Чат {} имеет тип, не являющийся group/supergroup/private, пропускаем.", chatId);
    }

    /**
     * Команда /register_admin secretKey
     * Регистрирует отправителя как администратора, если совпал secretKey.
     */
    private void handleRegisterAdminCommand(Message message) {
        String text = message.getText();
        Long chatId = message.getChatId();
        String[] parts = text.split("\\s+");
        if (parts.length < 2) {
            sendSimpleMessage(chatId, "Использование: /register_admin secretKey");
            return;
        }
        String inputSecret = parts[1];

        // Сравниваем с тем, что хранится в BotProperties (жёстко прописанный секрет)
        if (!botProperties.getSecretKey().equals(inputSecret)) {
            sendSimpleMessage(chatId, "Неверный секретный ключ!");
            return;
        }

        Long newAdminId = message.getFrom().getId();

        // Записываем adminId в БД
        BotConfigDB cfg = botConfigService.getConfig();
        cfg.setAdminId(newAdminId);
        botConfigService.saveConfig(cfg);

        sendSimpleMessage(chatId, "Поздравляем! Теперь вы администратор. Ваш ID: " + newAdminId);
    }

    /**
     * Команда /register_chat
     * Регистрирует текущую группу как основную группу бота.
     * Теперь регистрация разрешается только один раз и выполняется только администратором.
     */
    private void handleRegisterChatCommand(Message message) {
        Long chatId = message.getChatId();
        Long senderId = message.getFrom().getId();

        // Проверяем, что команда запущена в группе/супергруппе
        if (!message.getChat().isGroupChat() && !message.getChat().isSuperGroupChat()) {
            sendSimpleMessage(chatId, "Данную команду нужно выполнять внутри группы (или супергруппы).");
            return;
        }

        // Проверяем, что команду выполняет администратор
        if (!isAdmin(senderId)) {
            sendSimpleMessage(chatId, "Команда /register_chat доступна только администратору.");
            return;
        }

        // Получаем текущую конфигурацию из БД
        BotConfigDB cfg = botConfigService.getConfig();
        if (cfg.getGroupId() != null) {
            sendSimpleMessage(chatId, "Бот уже зарегистрирован в группе с id=" + cfg.getGroupId() + ". Регистрация повторно не допускается.");
            return;
        }

        // Регистрируем текущую группу
        cfg.setGroupId(chatId);
        botConfigService.saveConfig(cfg);

        sendSimpleMessage(chatId, "Группа зарегистрирована! Теперь groupId=" + chatId);
    }

    /**
     * Проверка, является ли пользователь админом, на основе данных из БД.
     */
    private boolean isAdmin(Long userId) {
        BotConfigDB cfg = botConfigService.getConfig();
        // Если в БД ещё нет adminId — возврат false
        if (cfg.getAdminId() == null) {
            return false;
        }
        return cfg.getAdminId().equals(userId);
    }

    /**
     * Утилита для быстрой отправки текстового сообщения.
     */
    private void sendSimpleMessage(Long chatId, String text) {
        SendMessage sm = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }
}