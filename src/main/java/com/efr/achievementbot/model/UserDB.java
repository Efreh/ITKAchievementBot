package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
    @Column
    private String userTag;

    @Column(nullable = false)
    private Long telegramId;

    @Column(nullable = false)
    private Long chatId;

    // Отображаемое имя, которое может быть заполнено из профиля (имя или ник)
    @Column
    private String userName;

    @Column
    private Integer messageCount = 0;

    @Column
    private Integer weeklyMessageCount = 0;

    @Column
    private Integer reactionCount = 0;

    @Column
    private Integer mediaCount = 0;

    @Column
    private Integer stickerCount = 0;

    // Поля для очков достижений
    @Column
    private Integer achievementScore = 0;

    @Column
    private Integer weeklyAchievementScore = 0;

    @Column
    private Integer monthlyAchievementScore = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements = new HashSet<>();
}