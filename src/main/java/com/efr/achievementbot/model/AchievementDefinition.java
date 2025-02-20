package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class AchievementDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String title;
    private String description;
    private String type;
    private Integer requiredValue;
    private String requiredKeyword;

    // Новое поле для хранения весового значения достижения (например, 5 очков)
    private Integer weight = 1;

    @OneToMany(mappedBy = "definition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Achievement> achievements = new ArrayList<>();
}