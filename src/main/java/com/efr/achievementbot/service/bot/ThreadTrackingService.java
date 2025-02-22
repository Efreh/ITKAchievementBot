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

    private static final int STANDARD_THREAD = 0;

    // Храним треды (ключ — threadId, значение — время последнего использования)
    private final Map<Integer, Instant> activeThreads = new ConcurrentHashMap<>();

    /**
     * Добавляет тред или обновляет его "время последнего использования".
     */
    public void registerThread(Integer threadId) {
        if (threadId != null) {
            activeThreads.put(threadId, Instant.now());
            log.info("Добавлен/обновлён тред ID: {}", threadId);
        }
    }

    /**
     * Возвращает случайный активный тред, учитывая и "нулевой" (GENERAL).
     * Если activeThreads пуст, всё равно возвращаем 0.
     * Если есть другие треды, 0 участвует наравне со всеми.
     */
    public Integer getRandomThread() {
        // Создаём множество всех тредов (активных + нулевой "GENERAL")
        Set<Integer> possibleThreads = new HashSet<>(activeThreads.keySet());
        possibleThreads.add(STANDARD_THREAD);  // гарантированно включаем 0

        // Преобразуем в список для случайного выбора
        List<Integer> threadList = new ArrayList<>(possibleThreads);
        // Случайно выбираем индекс
        int randIndex = new Random().nextInt(threadList.size());
        Integer chosen = threadList.get(randIndex);

        log.info("Выбран случайный тред: {}", chosen);
        return chosen;
    }

    /**
     * Удаляет треды, которые не использовались более 7 дней.
     */
    @Scheduled(cron = "0 0 0 * * *") // Запуск каждый день в полночь
    public void cleanupOldThreads() {
        Instant cutoffTime = Instant.now().minusSeconds(7L * 24 * 60 * 60); // 7 дней
        List<Integer> toRemove = activeThreads.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(cutoffTime))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        toRemove.forEach(activeThreads::remove);
        if (!toRemove.isEmpty()) {
            log.info("Удалены устаревшие треды: {}", toRemove);
        }
    }

    /**
     * Возвращает все текущие (активные) треды.
     */
    public Set<Integer> getAllThreads() {
        return activeThreads.keySet();
    }
}