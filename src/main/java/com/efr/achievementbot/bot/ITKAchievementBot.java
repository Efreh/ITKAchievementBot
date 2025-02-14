package com.efr.achievementbot.bot;

import com.efr.achievementbot.bot.service.AdminSession;
import com.efr.achievementbot.bot.service.AwardState;
import com.efr.achievementbot.config.BotVariable;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.efr.achievementbot.bot.service.AwardState.AWAITING_DESCRIPTION;

@Slf4j
@Component
@RequiredArgsConstructor
public class ITKAchievementBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final BotVariable botVariable;
    private final AchievementService achievementService;

    private final Map<Long, AdminSession> adminSessions = new ConcurrentHashMap<>();


    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage()) return;

        // Обработка админских команд (без прерывания потока)
        if (isAdmin(update.getMessage().getFrom().getId())) {
            handleAdminCommands(update);
            // Убираем return чтобы продолжить обработку
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

    // Кнопки
    private ReplyKeyboardMarkup createKeyboard(List<List<String>> buttonLabels) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        for (List<String> rowLabels : buttonLabels) {
            KeyboardRow row = new KeyboardRow();
            for (String label : rowLabels) {
                row.add(new KeyboardButton(label));
            }
            keyboardRows.add(row);
        }

        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    // Модифицированный метод обработки команд
    private void handleAdminCommands(Update update) {
        Message message = update.getMessage();
        if (!message.hasText()) return; // Игнорируем сообщения без текста
        String text = message.getText();
        Long chatId = message.getChatId();
        Long senderId = message.getFrom().getId();

        if (!isAdmin(senderId)) return;

        if (text.equals("/start") && isAdmin(senderId)) {
            sendMessageWithKeyboard(
                    chatId,
                    "Админ-панель:",
                    createKeyboard(List.of(
                            List.of("Выдать достижение"),
                            List.of("Отмена")
                    ))
            );
            return;
        }

        AdminSession session = adminSessions.getOrDefault(chatId, new AdminSession());

        try {
            if ("Отмена".equalsIgnoreCase(text)) {
                cancelSession(chatId, session);
                return;
            }

            switch (session.getState()) {
                case IDLE -> handleIdleState(text, chatId, session);
                case AWAITING_USER_TAG -> handleUserTagInput(text, chatId, session);
                case AWAITING_TITLE -> handleTitleInput(text, chatId, session);
                case AWAITING_DESCRIPTION -> handleDescriptionInput(text, chatId, session);
            }
        } finally {
            adminSessions.put(chatId, session);
        }
    }

    private void handleIdleState(String text, Long chatId, AdminSession session) {
        if ("Выдать достижение".equalsIgnoreCase(text)) {
            session.setState(AwardState.AWAITING_USER_TAG);
            sendMessageWithKeyboard(
                    chatId,
                    "Введите тег пользователя (@username):",
                    createKeyboard(List.of(List.of("Отмена"))) // Убираем "Далее" на этом шаге
            );
        }
    }

    private void handleUserTagInput(String text, Long chatId, AdminSession session) {
        if ("Отмена".equalsIgnoreCase(text)) {
            cancelSession(chatId, session);
            return;
        }

        if (!text.startsWith("@")) {
            sendError(chatId, "⚠️ Тег должен начинаться с @");
            return;
        }

        session.setUserTag(text);
        session.setState(AwardState.AWAITING_TITLE);
        sendMessageWithKeyboard(
                chatId,
                "Введите название достижения:",
                createKeyboard(List.of(List.of("Отмена")))
        );
    }

    private void handleTitleInput(String text, Long chatId, AdminSession session) {
        session.setTitle(text);
        session.setState(AWAITING_DESCRIPTION);
        sendMessageWithKeyboard(chatId, "Введите описание достижения:", createKeyboard(List.of(
                List.of("Далее", "Отмена")
        )));
    }

    private void handleDescriptionInput(String text, Long chatId, AdminSession session) {
        session.setDescription(text);

        achievementService.awardCustomAchievement(
                session.getUserTag(),
                session.getTitle(),
                session.getDescription(),
                Long.parseLong(botVariable.getGroupId()),
                this
        );

        sendMessageWithKeyboard(chatId, "Достижение успешно выдано!", createKeyboard(List.of(
                List.of("Выдать достижение"),
                List.of("Отмена")
        )));
        adminSessions.remove(chatId);
    }

    private void cancelSession(Long chatId, AdminSession session) {
        adminSessions.remove(chatId);
        sendMessageWithKeyboard(chatId, "Операция отменена", createKeyboard(List.of(
                List.of("Выдать достижение"),
                List.of("Отмена")
        )));
    }

    private void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard); // Важно: клавиатура должна быть явно включена
        message.setParseMode("Markdown"); // Опционально для форматирования

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки клавиатуры: {}", e.getMessage());
        }
    }

    private void sendError(Long chatId, String message) {
        try {
            execute(new SendMessage(chatId.toString(), message));
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
    private boolean isAdmin(Long userId) {
        return userId.toString().equals(botVariable.getAdministratorId());
    }
}
