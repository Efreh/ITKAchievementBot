package com.efr.ITKAchievementBot.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Код достижения (соответствует полю name в AchievementDefinition),
     * чтобы можно было однозначно понять, какое достижение выдано.
     */
    private String name;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserDB user;
}