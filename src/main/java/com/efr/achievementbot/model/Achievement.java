package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDB user;

    @ManyToOne
    @JoinColumn(name = "definition_id")
    private AchievementDefinition definition;

    private LocalDateTime awardedAt; // Время выдачи достижения
}