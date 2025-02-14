package com.efr.achievementbot.bot.service;

import lombok.Getter;
import lombok.Setter;

// Класс для хранения состояния диалога
@Getter
@Setter
public class AdminSession {
    private AwardState state = AwardState.IDLE;
    private String userTag;
    private String title;
    private String description;
}