package com.efr.achievementbot.bot.admin.session;

import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.Goblin;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс, представляющий состояние диалога с администратором (в конкретном чате).
 * Хранит текущее состояние (AdminMenuState) и "черновые" данные для создания/редактирования.
 */
@Getter
@Setter
public class AdminSession {

    /**
     * Хранилище сессий по chatId.
     */
    private static final Map<Long, AdminSession> SESSIONS = new ConcurrentHashMap<>();

    /**
     * Возвращает (или создаёт) сессию для данного chatId.
     */
    public static AdminSession getSession(Long chatId) {
        return SESSIONS.computeIfAbsent(chatId, k -> new AdminSession());
    }

    // По умолчанию состояние IDLE (или поменяйте на MAIN_MENU, если хотите)
    private AdminMenuState state = AdminMenuState.IDLE;

    // Промежуточный объект базового достижения
    private AchievementDefinition tempAchievementDefinition;
    // ID достижения, которое мы редактируем
    private Long editingAchievementId;

    // Поля для КАСТОМНОГО достижения
    private String customUserTag;
    private String customName;
    private String customTitle;
    private String customDescription;
    private Integer customWeight;

    // Промежуточный объект для гоблина, который создаём/редактируем
    private Goblin tempGoblin;
    // ID гоблина, который редактируем/просматриваем
    private Long editingGoblinId;

    // Пример для пагинации
    private int currentPage = 0;
}