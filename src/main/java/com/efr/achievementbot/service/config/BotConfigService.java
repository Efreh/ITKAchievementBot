package com.efr.achievementbot.service.config;

import com.efr.achievementbot.model.BotConfigDB;
import com.efr.achievementbot.repository.BotConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotConfigService {

    private final BotConfigRepository botConfigRepository;

    /**
     * Загружаем конфигурацию (или создаём новую, если пусто).
     */
    public BotConfigDB getConfig() {
        return botConfigRepository.findById(1L)
                .orElseGet(() -> {
                    BotConfigDB cfg = new BotConfigDB();
                    // cfg.setId(...) // уже 1L
                    return botConfigRepository.save(cfg);
                });
    }

    /**
     * Сохраняем конфигурацию в БД.
     */
    public BotConfigDB saveConfig(BotConfigDB config) {
        return botConfigRepository.save(config);
    }
}
