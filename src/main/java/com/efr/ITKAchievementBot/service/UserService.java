package com.efr.ITKAchievementBot.service;

import com.efr.ITKAchievementBot.model.UserDB;
import com.efr.ITKAchievementBot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDB saveUser(UserDB user){
        return userRepository.save(user);
    }

    public UserDB findByTelegramId(Long telegramId){
        return userRepository.findByTelegramId(telegramId);
    }
    public UserDB findByTelegramIdAndChatId(Long telegramId, Long chatId){
        return userRepository.findByTelegramIdAndChatId(telegramId, chatId);
    }
}
