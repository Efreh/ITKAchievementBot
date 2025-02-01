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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_achievements", joinColumns = @JoinColumn(name = "user_id"))
    private Set<String> achievements = new HashSet<>();
}
