package com.efr.achievementbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bot_config_db")
public class BotConfigDB {

    @Id
    private Long id = 1L;

    private Long adminId;
    private Long groupId;

    // Параметры "охлаждения" ключевых слов
    private Integer cooldown;

    // Включен ли гоблин вообще (on/off)
    private Boolean goblinEnabled;

    // Настройки для вычисления даты/времени следующего спавна гоблина
    private Integer goblinSpawnDaysMin;
    private Integer goblinSpawnDaysMax;
    private Integer goblinSpawnHourStart;
    private Integer goblinSpawnHourEnd;

    // Цвет текста для ачивок
    private String achievementTextColor;  // например: "#FFFFFF"

    // Цвет текста для дашборда
    private String dashboardTextColor;    // например: "#F0F0F0"
}
