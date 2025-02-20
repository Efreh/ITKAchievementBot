package com.efr.achievementbot.bot.admin;

/**
 * Перечисление состояний процесса выдачи достижения.
 */
public enum AwardState {
    IDLE,
    AWAITING_USER_TAG,
    AWAITING_TITLE,
    AWAITING_DESCRIPTION
}
