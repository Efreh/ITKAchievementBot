package com.efr.achievementbot.bot.admin.session;

public enum AdminMenuState {

    IDLE,          // <- Добавили, чтобы у нас было состояние по умолчанию
    MAIN_MENU,

    // --- Достижения
    ACHIEVEMENTS_MENU,
    ACHIEVEMENTS_ADD_TITLE,
    ACHIEVEMENTS_ADD_DESCRIPTION,
    ACHIEVEMENTS_ADD_WEIGHT,
    ACHIEVEMENTS_ADD_TYPE_SELECT,
    ACHIEVEMENTS_VIEW_LIST,
    ACHIEVEMENTS_VIEW_DETAILS,
    ACHIEVEMENTS_EDIT_TITLE,
    ACHIEVEMENTS_EDIT_DESCRIPTION,
    ACHIEVEMENTS_EDIT_WEIGHT,
    ACHIEVEMENTS_EDIT_TYPE,
    ACHIEVEMENTS_DELETE_CONFIRM,

    // --- Гоблины
    GOBLIN_MENU,
    GOBLIN_ADD_NAME,
    GOBLIN_ADD_DESCRIPTION,
    GOBLIN_ADD_BUTTON_TEXT,
    GOBLIN_ADD_SUCCESS_MESSAGE,
    GOBLIN_ADD_FAILURE_MESSAGE,
    GOBLIN_ADD_AWARD_POINTS,
    GOBLIN_VIEW_LIST,
    GOBLIN_VIEW_DETAILS,
    GOBLIN_EDIT_NAME,
    GOBLIN_EDIT_DESCRIPTION,
    GOBLIN_EDIT_BUTTON_TEXT,
    GOBLIN_EDIT_SUCCESS_MESSAGE,
    GOBLIN_EDIT_FAILURE_MESSAGE,
    GOBLIN_EDIT_AWARD_POINTS,
    GOBLIN_DELETE_CONFIRM,

    // --- Кастомные ачивки
    CUSTOM_ACHIEVEMENTS_USER_TAG,
    CUSTOM_ACHIEVEMENTS_NAME,
    CUSTOM_ACHIEVEMENTS_TITLE,
    CUSTOM_ACHIEVEMENTS_DESCRIPTION,
    CUSTOM_ACHIEVEMENTS_WEIGHT,

    // --- Настройки
    BOT_SETTINGS_MENU,
    BOT_SETTINGS_EDIT_COOLDOWN,
    BOT_SETTINGS_EDIT_GOBLIN_ENABLED,
    BOT_SETTINGS_EDIT_SPAWN_DAYS_MIN,
    BOT_SETTINGS_EDIT_SPAWN_DAYS_MAX,
    BOT_SETTINGS_EDIT_SPAWN_HOUR_START,
    BOT_SETTINGS_EDIT_SPAWN_HOUR_END,
    BOT_SETTINGS_EDIT_ACHIEVEMENT_COLOR,
    BOT_SETTINGS_EDIT_DASHBOARD_COLOR
}
