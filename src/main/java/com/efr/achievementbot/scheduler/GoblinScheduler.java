package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.model.BotConfigDB;
import com.efr.achievementbot.service.config.BotConfigService;
import com.efr.achievementbot.service.goblin.GoblinService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoblinScheduler {

    private final BotConfigService botConfigService;
    private final GoblinService goblinService;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final Random random = new Random();

    private ScheduledFuture<?> scheduledTask;

    @PostConstruct
    public void scheduleNextGoblin() {
        scheduleGoblinSpawn();
    }

    private void scheduleGoblinSpawn() {
        BotConfigDB cfg = botConfigService.getConfig();

        if (!Boolean.TRUE.equals(cfg.getGoblinEnabled())) {
            // Если goblinEnabled = false — просто не планируем спавн
            log.debug("Гоблин отключён, пропускаем планирование спавна.");
            return;
        }

        long now = System.currentTimeMillis();

        // Берём min/max из конфигурации
        int daysMin = cfg.getGoblinSpawnDaysMin();
        int daysMax = cfg.getGoblinSpawnDaysMax();
        // Генерируем случайное число дней в диапазоне [daysMin, daysMax]
        int daysInterval = daysMin + random.nextInt(daysMax - daysMin + 1);

        long intervalMillis = daysInterval * 24L * 60 * 60 * 1000;

        // Часы
        int hourStart = cfg.getGoblinSpawnHourStart();
        int hourEnd = cfg.getGoblinSpawnHourEnd();
        // Генерируем случайный час в [hourStart, hourEnd]
        int randomHour = hourStart + random.nextInt(hourEnd - hourStart + 1);

        int randomMinute = random.nextInt(60);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now + intervalMillis);
        calendar.set(Calendar.HOUR_OF_DAY, randomHour);
        calendar.set(Calendar.MINUTE, randomMinute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date nextSpawn = calendar.getTime();
        log.info("Следующий спавн гоблина запланирован на: {}", nextSpawn);

        scheduledTask = taskScheduler.schedule(() -> {
            Long groupId = cfg.getGroupId();
            if (groupId == null) {
                log.debug("Группа не зарегистрирована, пропускаем спавн гоблина");
                return;
            }
            // проверим ещё раз, не выключили ли гоблина
            BotConfigDB freshCfg = botConfigService.getConfig();
            if (!Boolean.TRUE.equals(freshCfg.getGoblinEnabled())) {
                log.debug("Гоблин был отключён, пропускаем спавн.");
                return;
            }

            // Спавним
            goblinService.spawnGoblin(groupId);

            // Снова планируем
            scheduleGoblinSpawn();

        }, nextSpawn);
    }
}
