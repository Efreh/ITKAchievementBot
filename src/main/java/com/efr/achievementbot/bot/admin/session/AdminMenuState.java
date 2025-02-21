package com.efr.achievementbot.bot.admin.session;

/**
 * Перечисление всех возможных состояний админского интерфейса.
 * Позволяет разбить логику бота на разные этапы, удобные при работе в стиле "мастера".
 */
public enum AdminMenuState {

    // Главное меню
    MAIN_MENU,

    // Подменю для достижений (базовые + кастомные)
    ACHIEVEMENTS_MENU,

    // Процесс добавления нового базового достижения
    ACHIEVEMENTS_ADD_TITLE,
    ACHIEVEMENTS_ADD_DESCRIPTION,
    ACHIEVEMENTS_ADD_WEIGHT,
    ACHIEVEMENTS_ADD_TYPE_SELECT,

    // Процесс просмотра/редактирования достижений
    ACHIEVEMENTS_VIEW_LIST,
    ACHIEVEMENTS_VIEW_DETAILS,
    ACHIEVEMENTS_EDIT_TITLE,
    ACHIEVEMENTS_EDIT_DESCRIPTION,
    ACHIEVEMENTS_EDIT_WEIGHT,
    ACHIEVEMENTS_EDIT_TYPE,
    ACHIEVEMENTS_DELETE_CONFIRM,

    // Процесс выдачи кастомного достижения
    CUSTOM_ACHIEVEMENTS_USER_TAG,
    CUSTOM_ACHIEVEMENTS_NAME,
    CUSTOM_ACHIEVEMENTS_TITLE,
    CUSTOM_ACHIEVEMENTS_DESCRIPTION,
    CUSTOM_ACHIEVEMENTS_WEIGHT,

    // Подменю для гоблинов
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

    // Служебные (не обязательно использовать)
    IDLE,
    CANCEL
}
