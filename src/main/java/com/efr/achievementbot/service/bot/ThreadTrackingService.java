package com.efr.achievementbot.service.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ThreadTrackingService {

    private final int STANDARD_THREAD = 0;

    // Храним треды (ключ — threadId, значение — время последнего использования)
    private final Map<Integer, Instant> activeThreads = new ConcurrentHashMap<>();

    // Добавляет тред или обновляет его "время последнего использования"
    public void registerThread(Integer threadId) {
        if (threadId != null) {
            activeThreads.put(threadId, Instant.now());
            log.info("Добавлен/обновлён тред ID: {}", threadId);
        }
    }

    // Выбирает случайный активный тред
    public Integer getRandomThread() {
        List<Integer> threadList = new ArrayList<>(activeThreads.keySet());
        if (threadList.isEmpty()) {
            log.warn("Нет доступных тредов! Установлен стандартный тред = 0");
            return STANDARD_THREAD;
        }
        return threadList.get(new Random().nextInt(threadList.size()));
    }

    // Удаляет треды, которые не использовались более 7 дней
    @Scheduled(cron = "0 0 0 * * *") // Запуск каждый день в полночь
    public void cleanupOldThreads() {
        Instant cutoffTime = Instant.now().minusSeconds(7 * 24 * 60 * 60); // 7 дней назад
        List<Integer> toRemove = activeThreads.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(cutoffTime))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        toRemove.forEach(activeThreads::remove);
        if (!toRemove.isEmpty()) {
            log.info("Удалены устаревшие треды: {}", toRemove);
        }
    }

    public Set<Integer> getAllThreads() {
        return activeThreads.keySet();
    }
}
