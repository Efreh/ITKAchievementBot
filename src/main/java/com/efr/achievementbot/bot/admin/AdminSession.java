package com.efr.achievementbot.bot.admin;

import lombok.Getter;
import lombok.Setter;

/**
 * Сессия для админских команд, позволяющая отслеживать состояние выдачи достижения.
 */
@Getter
@Setter
public class AdminSession {
    private AwardState state = AwardState.IDLE;
    private String userTag;
    private String title;
    private String description;
}
