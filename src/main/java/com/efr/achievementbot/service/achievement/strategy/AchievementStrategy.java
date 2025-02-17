package com.efr.achievementbot.service.achievement.strategy;

import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Интерфейс для стратегии проверки достижения.
 */
public interface AchievementStrategy {
    /**
     * Проверяет, выполнено ли условие достижения.
     *
     * @param user       пользователь
     * @param definition определение достижения
     * @param message    сообщение из Telegram
     * @return true, если условие выполнено, иначе false
     */
    boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message);

    /**
     * Возвращает тип стратегии, который используется для сопоставления с определением достижения.
     *
     * @return строка, представляющая тип стратегии
     */
    String getType();
}
