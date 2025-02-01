package com.efr.ITKAchievementBot.service;

import com.efr.ITKAchievementBot.model.Achievement;
import com.efr.ITKAchievementBot.repository.AchievementRepository;
import org.springframework.stereotype.Service;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;

    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    public Achievement saveAchievement(Achievement achievement){
        return achievementRepository.save(achievement);
    }
}
