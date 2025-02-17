package com.efr.achievementbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // Обеспечивает поддержку асинхронных методов
public class ItkAchievementBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItkAchievementBotApplication.class, args);
	}
}
