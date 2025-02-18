package com.efr.achievementbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Конфигурация асинхронного выполнения задач.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Настраивает пул потоков для асинхронных задач.
     *
     * @return Executor для выполнения задач
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);         // Базовое количество потоков
        executor.setMaxPoolSize(5);          // Максимальное количество потоков
        executor.setQueueCapacity(100);      // Вместимость очереди задач
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();
        return executor;
    }
}
