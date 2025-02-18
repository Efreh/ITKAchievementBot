package com.efr.achievementbot.repository.achievement;

import com.efr.achievementbot.model.Achievement;
import com.efr.achievementbot.model.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    @Query("SELECT a FROM Achievement a JOIN FETCH a.definition WHERE a.user = :user")
    List<Achievement> findByUserWithDefinition(@Param("user") UserDB user);

    // Новый метод для получения последнего достижения по определению
    Optional<Achievement> findTopByDefinitionIdOrderByAwardedAtDesc(Long definitionId);
}