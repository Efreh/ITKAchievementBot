package com.efr.achievementbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Goblin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Название гоблина (например, "Гоблин Обманщик")
    private String name;

    // Описание гоблина (подробное описание, которое можно вывести пользователю)
    @Column(length = 1000)
    private String description;

    // Текст на кнопке для ловли (например, "🕵️‍♂️ Разоблачить обман")
    private String buttonText;

    // Сообщение при успешной поимке (с плейсхолдером @userTag)
    @Column(length = 1000)
    private String successMessage;

    // Сообщение, если никто не поймал гоблина
    @Column(length = 1000)
    private String failureMessage;

    // Количество очков, начисляемых за поимку (например, 50)
    private Integer awardPoints;

}
