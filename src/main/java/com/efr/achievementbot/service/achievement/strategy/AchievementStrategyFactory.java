package com.efr.achievementbot.service.achievement.strategy;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Фабрика стратегий для проверки условий достижения.
 * Стратегии регистрируются по типу, возвращаемому методом getType().
 */
@Component
public class AchievementStrategyFactory {

    private final Map<String, AchievementStrategy> strategyMap = new HashMap<>();

    public AchievementStrategyFactory(List<AchievementStrategy> strategies) {
        strategies.forEach(strategy -> strategyMap.put(strategy.getType(), strategy));
    }

    /**
     * Возвращает стратегию по заданному типу.
     *
     * @param type тип достижения
     * @return соответствующая стратегия или null, если не найдена
     */
    public AchievementStrategy getStrategy(String type) {
        return strategyMap.get(type);
    }
}