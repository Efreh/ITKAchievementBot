package com.efr.achievementbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Храним единственную запись в таблице с ID=1 (или UUID),
 * где лежат все нужные поля.
 */
@Entity
@Data
public class BotConfigDB {

    @Id
    private Long id = 1L; // всегда 1, или UUID, как вам удобнее

    private Long adminId;   // ID админа
    private Long groupId;   // ID группы

    // Добавляйте поля по мере необходимости
    // Например, время последнего обновления и т.п.
}
