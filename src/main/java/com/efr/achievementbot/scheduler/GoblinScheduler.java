package com.efr.achievementbot.scheduler;

import com.efr.achievementbot.model.BotConfigDB;
import com.efr.achievementbot.service.config.BotConfigService;
import com.efr.achievementbot.service.goblin.GoblinService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoblinScheduler {

    private final BotConfigService botConfigService;
    private final GoblinService goblinService;
    private final ThreadPoolTaskScheduler taskScheduler;

    private ScheduledFuture<?> scheduledTask;
    private final Random random = new Random();

    @PostConstruct
    public void init() {
        scheduleGoblinSpawn();
    }

    /**
     * Стандартная цепочка планирования.
     */
    public void scheduleGoblinSpawn() {
        BotConfigDB cfg = botConfigService.getConfig();

        // Проверяем, включён ли гоблин вообще
        if (!Boolean.TRUE.equals(cfg.getGoblinEnabled())) {
            log.info("Гоблин отключён, не планируем спавн.");
            return;
        }

        if (cfg.getGroupId() == null) {
            log.info("Группа не указана, не планируем спавн.");
            return;
        }

        long now = System.currentTimeMillis();
        // Берём настройки из cfg
        int daysMin = cfg.getGoblinSpawnDaysMin();
        int daysMax = cfg.getGoblinSpawnDaysMax();
        int daysInterval = daysMin + random.nextInt(daysMax - daysMin + 1);
        long intervalMillis = daysInterval * 24L * 60 * 60 * 1000;

        int hourStart = cfg.getGoblinSpawnHourStart();
        int hourEnd = cfg.getGoblinSpawnHourEnd();
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
            BotConfigDB freshCfg = botConfigService.getConfig();
            if (!Boolean.TRUE.equals(freshCfg.getGoblinEnabled())) {
                log.debug("Гоблин отключён, пропускаем спавн.");
                return;
            }
            if (freshCfg.getGroupId() == null) {
                log.debug("Группа не зарегистрирована, пропускаем.");
                return;
            }
            // Спавним
            goblinService.spawnGoblin(freshCfg.getGroupId());

            // Снова планируем
            scheduleGoblinSpawn();
        }, nextSpawn);
    }

    /**
     * Останавливаем текущую задачу (если есть),
     * спавним гоблина сразу (если включён),
     * и заново планируем по цепочке.
     */
    public void restartGoblinSpawnImmediate() {
        // 1. Останавливаем старую задачу
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }

        // 2. Спавним сейчас, если возможно
        BotConfigDB cfg = botConfigService.getConfig();
        if (Boolean.TRUE.equals(cfg.getGoblinEnabled()) && cfg.getGroupId() != null) {
            log.info("Выполняем немедленный спавн гоблина по команде 'Restart goblin'.");
            goblinService.spawnGoblin(cfg.getGroupId());
        } else {
            log.info("Гоблин выключен или группа не зарегистрирована — не спавним.");
        }

        // 3. Сразу планируем следующий спавн
        scheduleGoblinSpawn();
        log.info("Гоблин: расписание перезапущено, следующий спавн назначен.");
    }
}