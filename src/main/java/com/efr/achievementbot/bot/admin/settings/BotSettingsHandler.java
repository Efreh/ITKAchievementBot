package com.efr.achievementbot.bot.admin.settings;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.bot.admin.session.AdminMenuState;
import com.efr.achievementbot.bot.admin.session.AdminSession;
import com.efr.achievementbot.bot.admin.util.AdminKeyboardUtils;
import com.efr.achievementbot.model.BotConfigDB;
import com.efr.achievementbot.scheduler.GoblinScheduler;
import com.efr.achievementbot.service.config.BotConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик меню "Настройки бота" (BotSettings).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BotSettingsHandler {

    private final BotConfigService botConfigService;
    private final GoblinScheduler goblinScheduler;

    /**
     * Отображает меню настроек бота (параметры, которые мы вынесли в BotConfigDB).
     */
    public void sendBotSettingsMenu(JavaCodeBot bot, Long chatId) {
        BotConfigDB cfg = botConfigService.getConfig();

        // Собираем текущее значение всех основных полей:
        String info = String.format(
                """
                Текущие настройки бота:
                1) Cooldown (часы): %d
                2) Goblin Enabled: %s
                3) Goblin Spawn Days Min: %d
                4) Goblin Spawn Days Max: %d
                5) Goblin Spawn Hour Start: %d
                6) Goblin Spawn Hour End: %d
                7) Achievement Text Color: %s
                8) Dashboard Text Color: %s
                """,
                cfg.getCooldown(),
                cfg.getGoblinEnabled(),
                cfg.getGoblinSpawnDaysMin(),
                cfg.getGoblinSpawnDaysMax(),
                cfg.getGoblinSpawnHourStart(),
                cfg.getGoblinSpawnHourEnd(),
                cfg.getAchievementTextColor(),
                cfg.getDashboardTextColor()
        );

        AdminKeyboardUtils.sendMenu(bot, chatId,
                "Меню настроек бота:\n\n" + info + "\n\nВыберите, что изменить:",
                new String[][] {
                        {"Cooldown", "Goblin on/off"},
                        {"Goblin Days min", "Goblin Days max"},
                        {"Goblin Hour start", "Goblin Hour end"},
                        {"Achievement color", "Dashboard color"},
                        {"Restart goblin", "Назад"}
                }
        );
    }

    /**
     * Главный метод для обработки состояний, связанных с настройками бота.
     */
    public void handleBotSettingsMenu(Update update, JavaCodeBot bot, AdminSession session) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String text = msg.getText() != null ? msg.getText().trim() : "";

        switch (session.getState()) {

            case BOT_SETTINGS_MENU -> {
                switch (text) {
                    case "Cooldown" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_COOLDOWN);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите новое значение (целое число — часы кулдауна):");
                    }
                    case "Goblin on/off" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_GOBLIN_ENABLED);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Включить гоблина? Введите 'on' или 'off':");
                    }
                    case "Goblin Days min" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_SPAWN_DAYS_MIN);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите минимальное кол-во дней между спавнами:");
                    }
                    case "Goblin Days max" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_SPAWN_DAYS_MAX);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите максимальное кол-во дней между спавнами:");
                    }
                    case "Goblin Hour start" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_SPAWN_HOUR_START);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите начальный час (0-23) для случайного спавна:");
                    }
                    case "Goblin Hour end" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_SPAWN_HOUR_END);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите конечный час (0-23) для случайного спавна:");
                    }
                    case "Achievement color" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_ACHIEVEMENT_COLOR);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите новый цвет (HEX), например #FF00FF:");
                    }
                    case "Dashboard color" -> {
                        session.setState(AdminMenuState.BOT_SETTINGS_EDIT_DASHBOARD_COLOR);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Введите новый цвет (HEX), например #00FFFF:");
                    }
                    case "Restart goblin" -> {
                        goblinScheduler.restartGoblinSpawnImmediate();
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Гоблин сразу заспавнен (если включен) и новое расписание запущено.");
                        sendBotSettingsMenu(bot, chatId);
                    }
                    case "Назад" -> {
                        // Возврат в главное меню
                        session.setState(AdminMenuState.MAIN_MENU);
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Возвращаюсь в главное меню. /start");
                    }
                    default -> {
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Неизвестный пункт. Повторите.");
                        sendBotSettingsMenu(bot, chatId);
                    }
                }
            }

            // ---- Редактирование cooldown
            case BOT_SETTINGS_EDIT_COOLDOWN -> {
                try {
                    int newCooldown = Integer.parseInt(text);
                    BotConfigDB cfg = botConfigService.getConfig();
                    cfg.setCooldown(newCooldown);
                    botConfigService.saveConfig(cfg);

                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Cooldown обновлён: " + newCooldown);
                } catch (NumberFormatException e) {
                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Некорректный формат числа. Повторите ввод:");
                    return;
                }
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование goblinEnabled
            case BOT_SETTINGS_EDIT_GOBLIN_ENABLED -> {
                boolean enable = "on".equalsIgnoreCase(text);
                BotConfigDB cfg = botConfigService.getConfig();
                cfg.setGoblinEnabled(enable);
                botConfigService.saveConfig(cfg);

                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Гоблин теперь " + (enable ? "включен" : "выключен") + ".");
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование goblinSpawnDaysMin
            case BOT_SETTINGS_EDIT_SPAWN_DAYS_MIN -> {
                try {
                    int minDays = Integer.parseInt(text);
                    BotConfigDB cfg = botConfigService.getConfig();
                    cfg.setGoblinSpawnDaysMin(minDays);
                    botConfigService.saveConfig(cfg);

                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Минимальные дни спавна: " + minDays);
                } catch (NumberFormatException e) {
                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Некорректное число. Повторите:");
                    return;
                }
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование goblinSpawnDaysMax
            case BOT_SETTINGS_EDIT_SPAWN_DAYS_MAX -> {
                try {
                    int maxDays = Integer.parseInt(text);
                    BotConfigDB cfg = botConfigService.getConfig();
                    cfg.setGoblinSpawnDaysMax(maxDays);
                    botConfigService.saveConfig(cfg);

                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Максимальные дни спавна: " + maxDays);
                } catch (NumberFormatException e) {
                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Некорректное число. Повторите:");
                    return;
                }
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование goblinSpawnHourStart
            case BOT_SETTINGS_EDIT_SPAWN_HOUR_START -> {
                try {
                    int startHour = Integer.parseInt(text);
                    if (startHour < 0 || startHour > 23) {
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Значение должно быть от 0 до 23. Повторите:");
                        return;
                    }
                    BotConfigDB cfg = botConfigService.getConfig();
                    cfg.setGoblinSpawnHourStart(startHour);
                    botConfigService.saveConfig(cfg);

                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Начальный час спавна: " + startHour);
                } catch (NumberFormatException e) {
                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Некорректный формат. Повторите:");
                    return;
                }
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование goblinSpawnHourEnd
            case BOT_SETTINGS_EDIT_SPAWN_HOUR_END -> {
                try {
                    int endHour = Integer.parseInt(text);
                    if (endHour < 0 || endHour > 23) {
                        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                                "Значение должно быть от 0 до 23. Повторите:");
                        return;
                    }
                    BotConfigDB cfg = botConfigService.getConfig();
                    cfg.setGoblinSpawnHourEnd(endHour);
                    botConfigService.saveConfig(cfg);

                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Конечный час спавна: " + endHour);
                } catch (NumberFormatException e) {
                    AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                            "Некорректный формат. Повторите:");
                    return;
                }
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование цвета для Achievement
            case BOT_SETTINGS_EDIT_ACHIEVEMENT_COLOR -> {
                BotConfigDB cfg = botConfigService.getConfig();
                cfg.setAchievementTextColor(text);
                botConfigService.saveConfig(cfg);

                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Цвет текста для ачивок обновлён: " + text);
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            // ---- Редактирование цвета для Dashboard
            case BOT_SETTINGS_EDIT_DASHBOARD_COLOR -> {
                BotConfigDB cfg = botConfigService.getConfig();
                cfg.setDashboardTextColor(text);
                botConfigService.saveConfig(cfg);

                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Цвет текста для дашборда обновлён: " + text);
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }

            default -> {
                log.warn("Неизвестное состояние (Bot Settings): {}", session.getState());
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                sendBotSettingsMenu(bot, chatId);
            }
        }
    }
}