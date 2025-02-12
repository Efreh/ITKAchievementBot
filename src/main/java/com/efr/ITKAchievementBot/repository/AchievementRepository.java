package com.efr.ITKAchievementBot.repository;

import com.efr.ITKAchievementBot.model.Achievement;
import com.efr.ITKAchievementBot.model.AchievementDefinition;
import com.efr.ITKAchievementBot.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    @Query("SELECT a FROM Achievement a JOIN FETCH a.definition WHERE a.user = :user")
    List<Achievement> findByUserWithDefinition(@Param("user") UserDB user);
}