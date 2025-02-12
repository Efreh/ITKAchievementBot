package com.efr.ITKAchievementBot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class AchievementDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Уникальный код достижения, например "first_message" */
    private String name;

    /** Название достижения (для отображения) */
    private String title;

    /** Описание достижения */
    private String description;

    /**
     * Тип достижения, определяющий, какая стратегия проверки будет использована.
     * Сейчас, например, "messageCount"
     */
    private String type;

    /**
     * Требуемое значение для данного достижения.
     * Например, для type = "messageCount" – требуемое количество сообщений.
     */
    private Integer requiredValue;

    /**
     * Требуемое ключевое слово для достижений типа "keyword".
     */
    private String requiredKeyword;
}
