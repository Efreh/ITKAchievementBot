package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "users")
@EqualsAndHashCode(exclude = {"achievements"})
public class UserDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userTag;  // Формат @username
    private Long telegramId;
    private Long chatId;     // ID чата
    private String userName; // Имя пользователя

    private Integer messageCount = 0;
    private Integer weeklyMessageCount = 0;
    private Integer reactionCount = 0;
    private Integer mediaCount = 0;
    private Integer stickerCount = 0;

    // Новые поля для очков достижений
    private Integer achievementScore = 0;
    private Integer weeklyAchievementScore = 0;
    private Integer monthlyAchievementScore = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements = new HashSet<>();
}
