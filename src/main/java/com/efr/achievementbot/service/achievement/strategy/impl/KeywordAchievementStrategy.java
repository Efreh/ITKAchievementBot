package com.efr.achievementbot.service.achievement.strategy.impl;

import com.efr.achievementbot.model.Achievement;
import com.efr.achievementbot.service.achievement.strategy.AchievementStrategy;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.achievement.AchievementRepository;
import com.efr.achievementbot.service.config.BotConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class KeywordAchievementStrategy implements AchievementStrategy {

    private final BotConfigService botConfigService;
    private final AchievementRepository achievementRepository;

    @Override
    public boolean isSatisfied(UserDB user, AchievementDefinition definition, Message message) {
        if (message == null || !message.hasText()) return false;

        String requiredKeyword = definition.getRequiredKeyword();
        if (requiredKeyword == null || requiredKeyword.isEmpty()) return false;

        // Проверяем, не находится ли данное достижение в периоде охлаждения
        Optional<LocalDateTime> lastAwardedTimeOpt = achievementRepository
                .findTopByDefinitionIdOrderByAwardedAtDesc(definition.getId())
                .map(Achievement::getAwardedAt);

        Duration cooldownHour = Duration.ofHours(botConfigService.getConfig().getCooldown());

        if (lastAwardedTimeOpt.isPresent()) {
            LocalDateTime lastAwarded = lastAwardedTimeOpt.get();
            Duration timeSinceAward = Duration.between(lastAwarded, LocalDateTime.now());
            if (timeSinceAward.compareTo(cooldownHour) < 0) {
                // Вычисляем оставшееся время до окончания охлаждения
                Duration remaining = cooldownHour.minus(timeSinceAward);
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
