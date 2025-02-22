package com.efr.achievementbot.repository;

import com.efr.achievementbot.model.BotConfigDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotConfigRepository extends JpaRepository<BotConfigDB, Long> {}
