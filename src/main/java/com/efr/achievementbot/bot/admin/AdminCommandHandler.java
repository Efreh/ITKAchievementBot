package com.efr.achievementbot.bot.admin;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.config.BotProperties;
import com.efr.achievementbot.service.achievement.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminCommandHandler {

    private final AchievementService achievementService;
    private final BotProperties botProperties;
    private final Map<Long, AdminSession> adminSessions = new ConcurrentHashMap<>();

    /**
     * Обрабатывает входящие админские команды.
     */
    public void handleAdminCommand(Update update, JavaCodeBot bot) {
        Message message = update.getMessage();
        if (!message.hasText()) return;

        String text = message.getText();
        Long chatId = message.getChatId();
        Long senderId = message.getFrom().getId();

        if (!isAdmin(senderId)) {
            log.warn("Попытка выполнения админской команды от неадминистратора, senderId: {}", senderId);
            return;
        }

        if ("/start".equals(text)) {
            sendMessageWithKeyboard(bot, chatId, "Админ-панель:", createKeyboard(List.of(
                    List.of("Выдать достижение"),
                    List.of("Отмена")
            )));
            return;
        }

        AdminSession session = adminSessions.getOrDefault(chatId, new AdminSession());
        try {
            if ("Отмена".equalsIgnoreCase(text)) {
                cancelSession(bot, chatId, session);
                return;
            }
            switch (session.getState()) {
                case IDLE -> handleIdleState(bot, text, chatId, session);
                case AWAITING_USER_TAG -> handleUserTagInput(bot, text, chatId, session);
                case AWAITING_TITLE -> handleTitleInput(bot, text, chatId, session);
                case AWAITING_DESCRIPTION -> handleDescriptionInput(bot, text, chatId, session);
            }
        } finally {
            adminSessions.put(chatId, session);
        }
    }

    private void handleIdleState(JavaCodeBot bot, String text, Long chatId, AdminSession session) {
        if ("Выдать достижение".equalsIgnoreCase(text)) {
            session.setState(AwardState.AWAITING_USER_TAG);
            sendMessageWithKeyboard(bot, chatId, "Введите тег пользователя (@username):", createKeyboard(List.of(
                    List.of("Отмена")
            )));
        }
    }

    private void handleUserTagInput(JavaCodeBot bot, String text, Long chatId, AdminSession session) {
        if (!text.startsWith("@")) {
            sendError(bot, chatId, "⚠️ Тег должен начинаться с @");
            return;
        }
        session.setUserTag(text);
        session.setState(AwardState.AWAITING_TITLE);
        sendMessageWithKeyboard(bot, chatId, "Введите название достижения:", createKeyboard(List.of(
                List.of("Отмена")
        )));
    }

    private void handleTitleInput(JavaCodeBot bot, String text, Long chatId, AdminSession session) {
        session.setTitle(text);
        session.setState(AwardState.AWAITING_DESCRIPTION);
        sendMessageWithKeyboard(bot, chatId, "Введите описание достижения:", createKeyboard(List.of(
                List.of("Далее", "Отмена")
        )));
    }

    private void handleDescriptionInput(JavaCodeBot bot, String text, Long chatId, AdminSession session) {
        session.setDescription(text);
        achievementService.awardCustomAchievement(
                session.getUserTag(),
                session.getTitle(),
                session.getDescription(),
                Long.parseLong(botProperties.getGroupId()),
                bot
        );
        sendMessageWithKeyboard(bot, chatId, "Достижение успешно выдано!", createKeyboard(List.of(
                List.of("Выдать достижение"),
                List.of("Отмена")
        )));
        adminSessions.remove(chatId);
    }

    private void cancelSession(JavaCodeBot bot, Long chatId, AdminSession session) {
        adminSessions.remove(chatId);
        sendMessageWithKeyboard(bot, chatId, "Операция отменена", createKeyboard(List.of(
                List.of("Выдать достижение"),
                List.of("Отмена")
        )));
    }

    private ReplyKeyboardMarkup createKeyboard(java.util.List<java.util.List<String>> buttonLabels) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        java.util.List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (java.util.List<String> rowLabels : buttonLabels) {
            KeyboardRow row = new KeyboardRow();
            for (String label : rowLabels) {
                row.add(new KeyboardButton(label));
            }
            keyboardRows.add(row);
        }
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    private void sendMessageWithKeyboard(JavaCodeBot bot, Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);
        message.setParseMode("Markdown");
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки клавиатуры: {}", e.getMessage());
        }
    }

    private void sendError(JavaCodeBot bot, Long chatId, String errorText) {
        try {
            bot.execute(new SendMessage(chatId.toString(), errorText));
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
        }
    }

    private boolean isAdmin(Long userId) {
        return userId.toString().equals(botProperties.getAdministratorId());
    }
}