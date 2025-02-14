package com.efr.achievementbot.service.achievement.strategy;

import com.efr.achievementbot.service.achievement.strategy.impl.*;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AchievementStrategyFactory {

    private final Map<String, AchievementStrategy> strategyMap = new HashMap<>();

    public AchievementStrategyFactory(List<AchievementStrategy> strategies) {
        strategies.forEach(strategy -> {
            if (strategy instanceof MessageCountAchievementStrategy) {
                strategyMap.put("messageCount", strategy);
            } else if (strategy instanceof KeywordAchievementStrategy) {
                strategyMap.put("keyword", strategy);
            } else if (strategy instanceof ReactionStrategy) {
                strategyMap.put("reaction", strategy);
            } else if (strategy instanceof MediaStrategy) {
                strategyMap.put("media", strategy);
            } else if (strategy instanceof StickerStrategy) {
                strategyMap.put("sticker", strategy);
            }
        });
    }

    public AchievementStrategy getStrategy(String type) {
        return strategyMap.get(type);
    }
}