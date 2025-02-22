package com.efr.achievementbot.bot.admin.util;

import com.efr.achievementbot.bot.JavaCodeBot;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * Утилиты для облегчения отправки текстовых сообщений и создания клавиатур.
 * Позволяет вызывать статические методы вроде:
 * AdminKeyboardUtils.sendMenu(bot, chatId, "Текст", new String[][]{{"Кнопка1","Кнопка2"}});
 */
@Slf4j
@UtilityClass
public class AdminKeyboardUtils {

    /**
     * Отправляет сообщение с кнопками (ReplyKeyboardMarkup).
     *
     * @param bot       - экземпляр бота
     * @param chatId    - идентификатор чата
     * @param text      - сообщение, которое отправляем
     * @param buttonRows - двумерный массив строк,
     *                     где каждый подмассив — это отдельная строка клавиатуры,
     *                     а элементы — это сами кнопки
     */
    public void sendMenu(JavaCodeBot bot, Long chatId, String text, String[][] buttonRows) {
        // Настройка клавиатуры
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);   // Кнопки подстраиваются под экран
        keyboard.setOneTimeKeyboard(false); // Клавиатура не скрывается после нажатия

        // Формируем строки клавиатуры
        List<KeyboardRow> rows = new ArrayList<>();
        for (String[] row : buttonRows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String label : row) {
                keyboardRow.add(new KeyboardButton(label));
            }
            rows.add(keyboardRow);
        }
        keyboard.setKeyboard(rows);

        // Формируем и отправляем сообщение
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке меню: {}", e.getMessage(), e);
        }
    }

    /**
     * Отправляет обычное текстовое сообщение без кнопок.
     *
     * @param bot    - экземпляр бота
     * @param chatId - идентификатор чата
     * @param text   - сообщение
     */
    public void sendSimpleMessage(JavaCodeBot bot, Long chatId, String text) {
        SendMessage message = new SendMessage(chatId.toString(), text);
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage(), e);
        }
    }
}
