package com.efr.ITKAchievementBot.config;

import com.efr.ITKAchievementBot.bot.ITKAchievementBot;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Getter
public class BotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(ITKAchievementBot myTelegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myTelegramBot);
        return botsApi;
    }
}