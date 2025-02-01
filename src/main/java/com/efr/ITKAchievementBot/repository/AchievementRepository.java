package com.efr.ITKAchievementBot.repository;

import com.efr.ITKAchievementBot.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
