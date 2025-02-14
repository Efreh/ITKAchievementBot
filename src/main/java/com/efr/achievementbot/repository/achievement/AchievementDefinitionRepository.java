package com.efr.achievementbot.repository.achievement;

import com.efr.achievementbot.model.AchievementDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinition, Long> {
    // При необходимости можно добавить выборку по типу:
    List<AchievementDefinition> findByType(String type);
}
