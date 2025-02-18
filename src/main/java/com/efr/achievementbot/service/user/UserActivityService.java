package com.efr.achievementbot.service.user;

import com.efr.achievementbot.model.UserDB;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Сервис для обновления статистики активности пользователя.
 */
@Service
public class UserActivityService {

    private final UserService userService;

    public UserActivityService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Обновляет статистику активности пользователя на основе полученного сообщения.
     * Если пользователь не найден, создается новый.
     *
     * @param message сообщение от Telegram
     * @return обновленный или созданный объект UserDB
     */
    public UserDB updateUserActivity(Message message) {
        Long chatId = message.getChatId();
        Long telegramId = message.getFrom().getId();
        String userTag = message.getFrom().getUserName() != null ? "@" + message.getFrom().getUserName() : null;

        // Поиск пользователя по telegramId и chatId
        UserDB user = userService.findByTelegramIdAndChatId(telegramId, chatId);
        if (user == null) {
            user = new UserDB();
            user.setTelegramId(telegramId);
            user.setChatId(chatId);
            user.setUserTag(userTag);
            user.setUserName(message.getFrom().getUserName());
            user.setMessageCount(0);
            user.setReactionCount(0);
            user.setMediaCount(0);
            user.setStickerCount(0);
            user.setWeeklyMessageCount(0);
        } else if (userTag != null && !userTag.equals(user.getUserTag())) {
            user.setUserTag(userTag);
        }

        // Увеличиваем счетчик сообщений
        user.setMessageCount(user.getMessageCount() + 1);

        // Обновление еженедельного счетчика
        user.setWeeklyMessageCount(user.getWeeklyMessageCount() + 1);

        // Обновляем счетчик медиа, если сообщение содержит фото, видео, документ или голосовое сообщение
        if (message.hasPhoto() || message.hasVideo() || message.hasDocument() || message.hasVoice()) {
            user.setMediaCount(user.getMediaCount() + 1);
        }

        // Обновляем счетчик стикеров
        if (message.hasSticker()) {
            user.setStickerCount(user.getStickerCount() + 1);
        }

        // Сохраняем обновленные данные пользователя
        return userService.saveUser(user);
    }
}