package com.efr.achievementbot.bot.admin.menu;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.bot.admin.achievements.AchievementAdminHandler;
import com.efr.achievementbot.bot.admin.goblins.GoblinAdminHandler;
import com.efr.achievementbot.bot.admin.session.AdminMenuState;
import com.efr.achievementbot.bot.admin.session.AdminSession;
import com.efr.achievementbot.bot.admin.settings.BotSettingsHandler;
import com.efr.achievementbot.bot.admin.util.AdminKeyboardUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Главный хендлер админ-меню.
 * Распределяет, какой раздел меню показать (достижения, гоблины, настройки и т.п.).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminMenuHandler {

    private final AchievementAdminHandler achievementAdminHandler;
    private final GoblinAdminHandler goblinAdminHandler;

    // Подключаем наш новый BotSettingsHandler
    private final BotSettingsHandler botSettingsHandler;

    public void handleAdminCommand(Update update, JavaCodeBot bot) {
        Message message = update.getMessage();
        if (message == null || message.getText() == null) {
            return;
        }

        Long chatId = message.getChatId();
        String text = message.getText();

        // получаем/создаём AdminSession
        AdminSession session = AdminSession.getSession(chatId);

        // Если /start => главное меню
        if ("/start".equals(text)) {
            session.setState(AdminMenuState.MAIN_MENU);
            sendMainMenu(bot, chatId);
            return;
        }

        // Определяем, какое меню/состояние сейчас
        switch (session.getState()) {
            case MAIN_MENU -> handleMainMenu(bot, chatId, text, session);

            // Достижения
            case ACHIEVEMENTS_MENU, ACHIEVEMENTS_ADD_TITLE, ACHIEVEMENTS_ADD_DESCRIPTION,
                 ACHIEVEMENTS_ADD_WEIGHT, ACHIEVEMENTS_ADD_TYPE_SELECT,
                 ACHIEVEMENTS_VIEW_LIST, ACHIEVEMENTS_VIEW_DETAILS,
                 ACHIEVEMENTS_EDIT_TITLE, ACHIEVEMENTS_EDIT_DESCRIPTION,
                 ACHIEVEMENTS_EDIT_WEIGHT, ACHIEVEMENTS_EDIT_TYPE,
                 ACHIEVEMENTS_DELETE_CONFIRM,
                 CUSTOM_ACHIEVEMENTS_USER_TAG, CUSTOM_ACHIEVEMENTS_NAME,
                 CUSTOM_ACHIEVEMENTS_TITLE, CUSTOM_ACHIEVEMENTS_DESCRIPTION,
                 CUSTOM_ACHIEVEMENTS_WEIGHT
                    -> achievementAdminHandler.handleAchievementMenu(update, bot, session);

            // Гоблины
            case GOBLIN_MENU, GOBLIN_ADD_NAME, GOBLIN_ADD_DESCRIPTION,
                 GOBLIN_ADD_BUTTON_TEXT, GOBLIN_ADD_SUCCESS_MESSAGE, GOBLIN_ADD_FAILURE_MESSAGE,
                 GOBLIN_ADD_AWARD_POINTS, GOBLIN_VIEW_LIST, GOBLIN_VIEW_DETAILS,
                 GOBLIN_EDIT_NAME, GOBLIN_EDIT_DESCRIPTION, GOBLIN_EDIT_BUTTON_TEXT,
                 GOBLIN_EDIT_SUCCESS_MESSAGE, GOBLIN_EDIT_FAILURE_MESSAGE,
                 GOBLIN_EDIT_AWARD_POINTS, GOBLIN_DELETE_CONFIRM
                    -> goblinAdminHandler.handleGoblinMenu(update, bot, session);

            // Настройки бота => перенаправляем в BotSettingsHandler
            case BOT_SETTINGS_MENU,
                 BOT_SETTINGS_EDIT_COOLDOWN,
                 BOT_SETTINGS_EDIT_GOBLIN_ENABLED,
                 BOT_SETTINGS_EDIT_SPAWN_DAYS_MIN,
                 BOT_SETTINGS_EDIT_SPAWN_DAYS_MAX,
                 BOT_SETTINGS_EDIT_SPAWN_HOUR_START,
                 BOT_SETTINGS_EDIT_SPAWN_HOUR_END,
                 BOT_SETTINGS_EDIT_ACHIEVEMENT_COLOR,
                 BOT_SETTINGS_EDIT_DASHBOARD_COLOR
                    -> botSettingsHandler.handleBotSettingsMenu(update, bot, session);

            default -> {
                log.warn("Неизвестное состояние {}. Возвращаемся в MAIN_MENU", session.getState());
                session.setState(AdminMenuState.MAIN_MENU);
                sendMainMenu(bot, chatId);
            }
        }
    }

    private void handleMainMenu(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        switch (text) {
            case "Достижения" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
                achievementAdminHandler.sendAchievementMenu(bot, chatId);
            }
            case "Гоблины" -> {
                session.setState(AdminMenuState.GOBLIN_MENU);
                goblinAdminHandler.sendGoblinMenu(bot, chatId);
            }
            case "Настройки бота" -> {
                session.setState(AdminMenuState.BOT_SETTINGS_MENU);
                // вызываем BotSettingsHandler для отображения меню
                botSettingsHandler.sendBotSettingsMenu(bot, chatId);
            }
            default -> {
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Неизвестная команда. Выберите в меню:");
                sendMainMenu(bot, chatId);
            }
        }
    }

    public void sendMainMenu(JavaCodeBot bot, Long chatId) {
        // Кнопки главного меню
        AdminKeyboardUtils.sendMenu(bot, chatId,
                "Главное меню администратора:",
                new String[][]{
                        {"Достижения", "Гоблины"},
                        {"Настройки бота"}
                }
        );
    }
}