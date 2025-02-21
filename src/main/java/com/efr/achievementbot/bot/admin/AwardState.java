package com.efr.achievementbot.bot.admin;

public enum AwardState {
    IDLE,
    AWAITING_USER_TAG,
    AWAITING_NAME,
    AWAITING_TITLE,
    AWAITING_DESCRIPTION,
    AWAITING_WEIGHT // <-- новое состояние
}