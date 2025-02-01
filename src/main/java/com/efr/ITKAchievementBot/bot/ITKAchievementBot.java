package com.efr.ITKAchievementBot.bot;

import com.efr.ITKAchievementBot.config.BotVariable;
import com.efr.ITKAchievementBot.model.User;
import com.efr.ITKAchievementBot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class ITKAchievementBot extends TelegramLongPollingBot {

    private final UserService userService;
    private final BotVariable botVariable;

    public ITKAchievementBot(UserService userService, BotVariable botVariable) {
        this.userService = userService;
        this.botVariable = botVariable;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String userName = message.getFrom().getUserName();

            User user = userService.findByChatId(chatId);
            if (user == null) {
                user = new User();
                user.setChatId(chatId);
                user.setUserName(userName);
                user.setMessageCount(0);
            }

            user.setMessageCount(user.getMessageCount() + 1);
            userService.saveUser(user);

            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("Сообщение получено! Ваш счет: " + user.getMessageCount());

            try {
                execute(response);
            } catch (TelegramApiException e) {
                log.error("Ошибка при обработке сообщения: {}", e.getMessage(), e);
            }
        }
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
