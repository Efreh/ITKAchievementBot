package com.efr.ITKAchievementBot.bot.handlers.achievements;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementDefinition {
    private String title;
    private String description;
    private TriggerType triggerType;
    private int requiredCount;
}