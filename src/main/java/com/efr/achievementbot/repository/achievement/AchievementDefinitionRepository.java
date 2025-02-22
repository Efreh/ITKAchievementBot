package com.efr.achievementbot.repository.achievement;

import com.efr.achievementbot.model.AchievementDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementDefinitionRepository extends JpaRepository<AchievementDefinition, Long> {
}
