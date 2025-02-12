package com.efr.ITKAchievementBot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "users")
public class UserDB {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegramId; // Уникальный идентификатор пользователя в Telegram
    private Long chatId;     // ID чата (группы или личного чата)
    private String userName;
    private Integer messageCount;

    // Новые поля для статистики
    private Integer reactionCount; // Общее количество реакций на сообщения пользователя
    private Integer mediaCount;    // Общее количество отправленных медиафайлов
    private Integer stickerCount;  // Общее количество отправленных стикеров

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements = new HashSet<>();
}