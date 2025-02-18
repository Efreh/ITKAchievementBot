package com.efr.achievementbot.service.achievement;

import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.repository.achievement.AchievementDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementDefinitionService {

    private final AchievementDefinitionRepository definitionRepository;

    /**
     * Кэшируем список определений достижений.
     */
    @Cacheable("achievementDefinitions")
    public List<AchievementDefinition> getAllDefinitions() {
        return definitionRepository.findAll();
    }

    /**
     * Обновление или создание определения – очищает кэш, чтобы новые данные были доступны.
     */
    @CacheEvict(value = "achievementDefinitions", allEntries = true)
    public AchievementDefinition saveDefinition(AchievementDefinition definition) {
        return definitionRepository.save(definition);
    }
}
