package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(exclude = "definition")
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