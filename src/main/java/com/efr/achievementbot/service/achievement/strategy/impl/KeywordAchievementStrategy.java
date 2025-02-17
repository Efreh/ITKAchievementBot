package com.efr.achievementbot.service.achievement.strategy.impl;

import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class KeywordAchievementStrategy implements AchievementStrategy {
    @Override
    public boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message) {
        if (message == null || !message.hasText()) return false;

        String requiredKeyword = definition.getRequiredKeyword();
        if (requiredKeyword == null || requiredKeyword.isEmpty()) return false;

        String text = message.getText().toLowerCase();
        String keyword = requiredKeyword.toLowerCase();

        if (keyword.isEmpty()) return false;

        // Используем регулярное выражение для поиска целого слова
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}