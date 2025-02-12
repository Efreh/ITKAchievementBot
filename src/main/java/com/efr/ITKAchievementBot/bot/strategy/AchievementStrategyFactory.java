package com.efr.ITKAchievementBot.bot.strategy;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AchievementStrategyFactory {

    private final Map<String, AchievementStrategy> strategyMap = new HashMap<>();

    // Автоматически внедряем все бины, реализующие AchievementStrategy
    public AchievementStrategyFactory(List<AchievementStrategy> strategies) {
        strategies.forEach(strategy -> {
            if (strategy instanceof MessageCountAchievementStrategy) {
                strategyMap.put("messageCount", strategy);
            } else if (strategy instanceof KeywordAchievementStrategy) {
                strategyMap.put("keyword", strategy);
            }
        });
    }

    /**
     * Возвращает стратегию для данного типа достижения.
     *
     * @param type тип достижения, например "messageCount"
     * @return стратегия или null, если для типа нет стратегии
     */
    public AchievementStrategy getStrategy(String type) {
        return strategyMap.get(type);
    }
}
