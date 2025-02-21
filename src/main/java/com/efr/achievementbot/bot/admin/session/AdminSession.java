package com.efr.achievementbot.bot.admin.session;

import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.Goblin;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий состояние диалога с администратором (в конкретном чате).
 * Хранит текущее состояние (AdminMenuState) и "черновые" данные для создания/редактирования.
 */
@Getter
@Setter
public class AdminSession {

    private AdminMenuState state = AdminMenuState.IDLE;

    /**
     * Промежуточный объект базового достижения, который администратор создаёт
     * или редактирует (title, description, type и т.д.).
     */
    private AchievementDefinition tempAchievementDefinition;

    /**
     * ID достижения, которое мы сейчас редактируем/просматриваем, чтобы не искать его заново.
     */
    private Long editingAchievementId;

    /**
     * Поля для создания/выдачи КАСТОМНОГО достижения (если вы решите делать это отдельно).
     */
    private String customUserTag;
    private String customName;
    private String customTitle;
    private String customDescription;
    private Integer customWeight;

    /**
     * Промежуточный объект для гоблина, который администратор создаёт или редактирует.
     */
    private Goblin tempGoblin;

    /**
     * ID гоблина, который редактируется или просматривается.
     */
    private Long editingGoblinId;

    // Пример поля для "пагинации" — если захотите разбивать списки на страницы
    private int currentPage = 0;
}