package com.efr.achievementbot.model;

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

    private String userTag;  // Формат @username
    private Long telegramId;
    private Long chatId;     // ID чата
    private String userName; // Имя пользователя

    private Integer messageCount;
    private Integer reactionCount;
    private Integer mediaCount;
    private Integer stickerCount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Achievement> achievements = new HashSet<>();
}
