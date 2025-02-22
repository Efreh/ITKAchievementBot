package com.efr.achievementbot.bot.admin.menu;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.bot.admin.util.AdminKeyboardUtils;
import com.efr.achievementbot.bot.admin.session.AdminMenuState;
import com.efr.achievementbot.bot.admin.session.AdminSession;
import com.efr.achievementbot.bot.admin.achievements.AchievementAdminHandler;
import com.efr.achievementbot.bot.admin.goblins.GoblinAdminHandler;
import com.efr.achievementbot.config.bot.BotProperties;
import com.efr.achievementbot.service.config.BotConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Главный обработчик (диспетчер) админского меню.
 * Делегирует управление узким «подхендлерам» (AchievementAdminHandler, GoblinAdminHandler и т.д.).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminMenuHandler {

    private final BotConfigService botConfigService;
    private final AchievementAdminHandler achievementAdminHandler;
    private final GoblinAdminHandler goblinAdminHandler;

    // Храним сессии для каждого чата (или конкретного администратора)
    private final Map<Long, AdminSession> adminSessions = new ConcurrentHashMap<>();

    /**
     * Точка входа при обработке админ-команды.
     */
    public void handleAdminCommand(Update update, JavaCodeBot bot) {
        if (!update.hasMessage() || update.getMessage().getFrom() == null) {
            return;
        }
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        Long userId = msg.getFrom().getId();

        // Проверяем, действительно ли администратор
        if (!isAdmin(userId)) {
            log.warn("Пользователь ID={} не является админом, но пытается вызвать админскую команду.", userId);
            return;
        }

        // Получаем (или создаём новую) сессию
        AdminSession session = adminSessions.getOrDefault(chatId, new AdminSession());
        String text = msg.getText() != null ? msg.getText() : "";

        // Если введена команда /start, показываем главное меню
        if ("/start".equals(text)) {
            sendMainMenu(bot, chatId);
            session.setState(AdminMenuState.MAIN_MENU);
            adminSessions.put(chatId, session);
            return;
        }

        // Смотрим текущее состояние и решаем, что делать
        switch (session.getState()) {
            // Если мы в главном меню — обрабатываем выбор
            case MAIN_MENU -> handleMainMenuInput(bot, chatId, text, session);

            // Если мы в ветке «достижений»
            case ACHIEVEMENTS_MENU,
                 ACHIEVEMENTS_ADD_TITLE,
                 ACHIEVEMENTS_ADD_DESCRIPTION,
                 ACHIEVEMENTS_ADD_WEIGHT,
                 ACHIEVEMENTS_ADD_TYPE_SELECT,
                 ACHIEVEMENTS_VIEW_LIST,
                 ACHIEVEMENTS_VIEW_DETAILS,
                 ACHIEVEMENTS_EDIT_TITLE,
                 ACHIEVEMENTS_EDIT_DESCRIPTION,
                 ACHIEVEMENTS_EDIT_WEIGHT,
                 ACHIEVEMENTS_EDIT_TYPE,
                 ACHIEVEMENTS_DELETE_CONFIRM,
                 CUSTOM_ACHIEVEMENTS_USER_TAG,
                 CUSTOM_ACHIEVEMENTS_NAME,
                 CUSTOM_ACHIEVEMENTS_TITLE,
                 CUSTOM_ACHIEVEMENTS_DESCRIPTION,
                 CUSTOM_ACHIEVEMENTS_WEIGHT
                    -> achievementAdminHandler.handleAchievementMenu(update, bot, session);

            // Если мы в ветке «гоблинов»
            case GOBLIN_MENU,
                 GOBLIN_ADD_NAME,
                 GOBLIN_ADD_DESCRIPTION,
                 GOBLIN_ADD_BUTTON_TEXT,
                 GOBLIN_ADD_SUCCESS_MESSAGE,
                 GOBLIN_ADD_FAILURE_MESSAGE,
                 GOBLIN_ADD_AWARD_POINTS,
                 GOBLIN_VIEW_LIST,
                 GOBLIN_VIEW_DETAILS,
                 GOBLIN_EDIT_NAME,
                 GOBLIN_EDIT_DESCRIPTION,
                 GOBLIN_EDIT_BUTTON_TEXT,
                 GOBLIN_EDIT_SUCCESS_MESSAGE,
                 GOBLIN_EDIT_FAILURE_MESSAGE,
                 GOBLIN_EDIT_AWARD_POINTS,
                 GOBLIN_DELETE_CONFIRM
                    -> goblinAdminHandler.handleGoblinMenu(update, bot, session);

            // Прочие состояния или «зависшие»
            default -> {
                log.warn("AdminMenuHandler: неизвестное состояние {}, сбрасываемся в MAIN_MENU.", session.getState());
                sendMainMenu(bot, chatId);
                session.setState(AdminMenuState.MAIN_MENU);
            }
        }

        // Обновляем сессию в кеше
        adminSessions.put(chatId, session);
    }

    /**
     * Отображаем главное меню (уровня «Главное меню»).
     */
    private void sendMainMenu(JavaCodeBot bot, Long chatId) {
        AdminKeyboardUtils.sendMenu(
                bot,
                chatId,
                "Добро пожаловать в админ-панель. Выберите опцию:",
                new String[][] {
                        {"Работа с достижениями", "Работа с гоблинами"},
                        {"Отмена"}
                }
        );
    }

    /**
     * Обработка ввода на экране главного меню (MAIN_MENU).
     */
    private void handleMainMenuInput(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        switch (text) {
            case "Работа с достижениями" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
                achievementAdminHandler.sendAchievementMenu(bot, chatId);
            }
            case "Работа с гоблинами" -> {
                session.setState(AdminMenuState.GOBLIN_MENU);
                goblinAdminHandler.sendGoblinMenu(bot, chatId);
            }
            case "Отмена", "Выход" -> {
                // Завершаем сессию
                session.setState(AdminMenuState.IDLE);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Админ-меню закрыто. Введите /start, чтобы открыть снова.");
            }
            default -> {
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Неизвестная команда. Повторите выбор:");
                sendMainMenu(bot, chatId);
            }
        }
    }

    private boolean isAdmin(Long userId) {
        return userId.equals(botConfigService.getConfig().getAdminId());
    }
}