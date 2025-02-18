package com.efr.achievementbot.service.achievement.strategy.impl;

import com.efr.achievementbot.model.Achievement;
import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.achievement.AchievementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class KeywordAchievementStrategy implements AchievementStrategy {

    // Период охлаждения – 8 часов
    private static final Duration COOLDOWN = Duration.ofHours(8);

    private final AchievementRepository achievementRepository;

    @Autowired
    public KeywordAchievementStrategy(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Override
    public boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message) {
        if (message == null || !message.hasText()) return false;

        String requiredKeyword = definition.getRequiredKeyword();
        if (requiredKeyword == null || requiredKeyword.isEmpty()) return false;

        // Проверяем, не находится ли данное достижение в периоде охлаждения
        Optional<LocalDateTime> lastAwardedTimeOpt = achievementRepository
                .findTopByDefinitionIdOrderByAwardedAtDesc(definition.getId())
                .map(Achievement::getAwardedAt);

        if (lastAwardedTimeOpt.isPresent()) {
            LocalDateTime lastAwarded = lastAwardedTimeOpt.get();
            Duration timeSinceAward = Duration.between(lastAwarded, LocalDateTime.now());
            if (timeSinceAward.compareTo(COOLDOWN) < 0) {
                // Вычисляем оставшееся время до окончания охлаждения
                Duration remaining = COOLDOWN.minus(timeSinceAward);
                long hours = remaining.toHours();
                long minutes = remaining.minusHours(hours).toMinutes();
                log.info("Достижение '{}' находится на охлаждении. Осталось {} часов {} минут до возможности повторной выдачи.",
                        definition.getTitle(), hours, minutes);
                return false;
            }
        }

        String text = message.getText().toLowerCase();
        String keyword = requiredKeyword.toLowerCase();

        // Используем регулярное выражение для поиска целого слова
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(keyword) + "\\b");
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    @Override
    public String getType() {
        return "keyword";
    }
}
