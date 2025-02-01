package com.efr.ITKAchievementBot.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
