package com.efr.achievementbot.service.config;

import com.efr.achievementbot.model.BotConfigDB;
import com.efr.achievementbot.repository.BotConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotConfigService {

    private final BotConfigRepository botConfigRepository;

    public BotConfigDB getConfig() {
        // Загружаем или создаём запись
        BotConfigDB cfg = botConfigRepository.findById(1L)
                .orElseGet(() -> {
                    BotConfigDB newCfg = new BotConfigDB();
                    return botConfigRepository.save(newCfg);
                });

        // Проставляем дефолты, если поля null
        if (cfg.getCooldown() == null) {
            cfg.setCooldown(8); // 8 часов
        }
        if (cfg.getGoblinEnabled() == null) {
            cfg.setGoblinEnabled(true);
        }
        if (cfg.getGoblinSpawnDaysMin() == null) {
            cfg.setGoblinSpawnDaysMin(2);
        }
        if (cfg.getGoblinSpawnDaysMax() == null) {
            cfg.setGoblinSpawnDaysMax(3);
        }
        if (cfg.getGoblinSpawnHourStart() == null) {
            cfg.setGoblinSpawnHourStart(10);
        }
        if (cfg.getGoblinSpawnHourEnd() == null) {
            cfg.setGoblinSpawnHourEnd(22);
        }
        if (cfg.getAchievementTextColor() == null) {
            cfg.setAchievementTextColor("#FFFFFF");
        }
        if (cfg.getDashboardTextColor() == null) {
            cfg.setDashboardTextColor("#000000");
        }

        return botConfigRepository.save(cfg);
    }

    public BotConfigDB saveConfig(BotConfigDB config) {
        return botConfigRepository.save(config);
    }
}