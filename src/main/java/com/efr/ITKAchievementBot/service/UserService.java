package com.efr.ITKAchievementBot.service;

import com.efr.ITKAchievementBot.model.User;
import com.efr.ITKAchievementBot.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User findByChatId (Long chatId){
        return userRepository.findByChatId(chatId);
    }
}
