package com.efr.achievementbot.service.goblin;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.model.Goblin;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.goblin.GoblinRepository;
import com.efr.achievementbot.repository.user.UserRepository;
import com.efr.achievementbot.service.bot.ThreadTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoblinService {

    private boolean activeGoblinFlag = false;

    private final GoblinRepository goblinRepository;
    private final UserRepository userRepository;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final ThreadTrackingService threadTrackingService;

    // Получаем ApplicationContext для динамического доступа к JavaCodeBot
    @Autowired
    private ApplicationContext applicationContext;

    private final Map<Long, ActiveGoblin> activeGoblins = new ConcurrentHashMap<>();

    private static class ActiveGoblin {
        private Goblin goblin;
        private Integer messageId;
        private Integer threadId; // Сохраняем тред, в котором заспавнился гоблин
        private ScheduledFuture<?> expirationTask;
    }

    private final String goblinImagePath = "images/goblins/greedy_goblin.jpg";
    private final String potionImagePath = "images/potions/potion.jpg";

    // Метод для спауна гоблина – теперь включается описание гоблина
    public void spawnGoblin(Long chatId) {
        if (activeGoblinFlag) {
            log.info("spawnGoblin: уже есть активный гоблин, пропускаем спавн.");
            return;
        }

        List<Goblin> goblins = goblinRepository.findAll();
        if (goblins.isEmpty()) {
            log.info("В БД отсутствуют гоблины – активность не запускается.");
            return;
        }

        Goblin selectedGoblin = goblins.get(new Random().nextInt(goblins.size()));
        Integer threadId = threadTrackingService.getRandomThread(); // Выбираем случайный тред

        try {
            InlineKeyboardButton catchButton = new InlineKeyboardButton();
            catchButton.setText(selectedGoblin.getButtonText());
            catchButton.setCallbackData("GOBLIN_CATCH:" + selectedGoblin.getId());
            List<InlineKeyboardButton> row = Collections.singletonList(catchButton);
            List<List<InlineKeyboardButton>> keyboard = Collections.singletonList(row);
            InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
            inlineKeyboard.setKeyboard(keyboard);

            String spawnText = "В чате появился " + selectedGoblin.getName() + "!\n" + selectedGoblin.getDescription();

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId.toString());
            sendPhoto.setMessageThreadId(threadId); // Отправляем в тред
            log.info("Гоблин заспавнен в треде: {}", threadId);

            sendPhoto.setCaption(spawnText);

            InputStream is = getClass().getClassLoader().getResourceAsStream(goblinImagePath);
            if (is == null) {
                log.error("Не удалось найти ресурс: {}", goblinImagePath);
                return;
            }
            sendPhoto.setPhoto(new InputFile(is, "greedy_goblin.jpg"));

            sendPhoto.setReplyMarkup(inlineKeyboard);
            var sentMessage = applicationContext.getBean(JavaCodeBot.class).execute(sendPhoto);
            Integer messageId = sentMessage.getMessageId();

            ActiveGoblin activeGoblin = new ActiveGoblin();
            activeGoblin.goblin = selectedGoblin;
            activeGoblin.messageId = messageId;
            activeGoblin.threadId = threadId; // Запоминаем тред, в котором появился гоблин
            ScheduledFuture<?> future = taskScheduler.schedule(() ->
                    expireGoblin(chatId, messageId), new Date(System.currentTimeMillis() + 20000));
            activeGoblin.expirationTask = future;

            activeGoblins.put(chatId, activeGoblin);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения о гоблине: {}", e.getMessage(), e);
        }
        activeGoblinFlag = true;
    }


    // Обработка callback-запроса при поимке гоблина – подставляем тэг/имя в сообщение с зельем
    public void handleGoblinCatch(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data == null || !data.startsWith("GOBLIN_CATCH:")) {
            return;
        }
        Long chatId = callbackQuery.getMessage().getChatId();
        ActiveGoblin activeGoblin = activeGoblins.remove(chatId);
        if (activeGoblin == null) {
            return;
        }
        if (activeGoblin.expirationTask != null && !activeGoblin.expirationTask.isDone()) {
            activeGoblin.expirationTask.cancel(false);
        }
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId.toString());
            deleteMessage.setMessageId(activeGoblin.messageId);
            applicationContext.getBean(JavaCodeBot.class).execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error("Ошибка при удалении сообщения о гоблине: {}", e.getMessage(), e);
        }
        Long userId = callbackQuery.getFrom().getId();
        String userTag = (callbackQuery.getFrom().getUserName() != null)
                ? "@" + callbackQuery.getFrom().getUserName()
                : callbackQuery.getFrom().getFirstName();
        UserDB user = userRepository.findByTelegramIdAndChatId(userId, chatId);
        if (user == null) {
            user = new UserDB();
            user.setTelegramId(userId);
            user.setChatId(chatId);
            user.setUserTag(userTag);
            userRepository.save(user);
        }
        int points = activeGoblin.goblin.getAwardPoints() != null ? activeGoblin.goblin.getAwardPoints() : 50;

        user.setAchievementScore((user.getAchievementScore() != null ? user.getAchievementScore() : 0) + points);
        user.setWeeklyAchievementScore((user.getWeeklyAchievementScore() != null ? user.getWeeklyAchievementScore() : 0) + points);
        user.setMonthlyAchievementScore((user.getMonthlyAchievementScore() != null ? user.getMonthlyAchievementScore() : 0) + points);

        userRepository.save(user);

        log.info("Пользователь {} (ID: {}) получил {} очков. Новый баланс: {} (неделя: {}, месяц: {})",
                userTag, userId, points, user.getAchievementScore(), user.getWeeklyAchievementScore(), user.getMonthlyAchievementScore());

        try {
            String baseSuccessMessage = activeGoblin.goblin.getSuccessMessage();
            String successText = baseSuccessMessage.contains("@userTag")
                    ? baseSuccessMessage.replace("@userTag", userTag)
                    : userTag + " " + baseSuccessMessage;

            SendPhoto successPhoto = new SendPhoto();
            successPhoto.setChatId(chatId.toString());
            successPhoto.setCaption(successText);

            InputStream is = getClass().getClassLoader().getResourceAsStream(potionImagePath);
            if (is == null) {
                log.error("Не удалось найти ресурс: {}", potionImagePath);
                return;
            }
            successPhoto.setPhoto(new InputFile(is, "greedy_goblin.jpg"));


            if (activeGoblin.threadId != null) {
                successPhoto.setMessageThreadId(activeGoblin.threadId); // Отправляем в тот же тред
            }
            applicationContext.getBean(JavaCodeBot.class).execute(successPhoto);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения об успехе: {}", e.getMessage(), e);
        }
        activeGoblinFlag = false;
    }

    // Метод, вызываемый по истечении 20 секунд, если гоблин не пойман
    private void expireGoblin(Long chatId, Integer messageId) {
        ActiveGoblin activeGoblin = activeGoblins.remove(chatId);
        if (activeGoblin == null) return;
        try {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId.toString());
            deleteMessage.setMessageId(messageId);
            applicationContext.getBean(JavaCodeBot.class).execute(deleteMessage);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            sendMessage.setText(activeGoblin.goblin.getFailureMessage());
            if (activeGoblin.threadId != null) {
                sendMessage.setMessageThreadId(activeGoblin.threadId); // Отправляем в тот же тред
            }
            applicationContext.getBean(JavaCodeBot.class).execute(sendMessage);

            log.info("Гоблин {} в чате {} не пойман и исчез.", activeGoblin.goblin.getName(), chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка при обработке истечения времени гоблина: {}", e.getMessage(), e);
        }
        activeGoblinFlag = false;
    }
}
