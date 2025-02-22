package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность пользователя Telegram для хранения статистики и достижений.
 */
@Getter
@Setter
@Entity(name = "users")
@EqualsAndHashCode(exclude = {"achievements"})
public class UserDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Telegram username (с тегом @) может отсутствовать, поэтому используем отдельное поле для отображаемого имени
    private String userTag;
    private Long telegramId;
    private Long chatId;

    // Отображаемое имя, которое может быть заполнено из профиля (имя или ник)
    private String userName;
    private Integer messageCount = 0;
    private Integer weeklyMessageCount = 0;
    private Integer reactionCount = 0;
    private Integer mediaCount = 0;
    private Integer stickerCount = 0;

    // Поля для очков достижений
    private Integer achievementScore = 0;
    private Integer weeklyAchievementScore = 0;
    private Integer monthlyAchievementScore = 0;

    private LocalDateTime lastActivity;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements = new HashSet<>();
}