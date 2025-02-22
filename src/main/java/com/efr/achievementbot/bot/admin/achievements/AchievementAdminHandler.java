package com.efr.achievementbot.bot.admin.achievements;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.bot.admin.util.AdminKeyboardUtils;
import com.efr.achievementbot.bot.admin.session.AdminMenuState;
import com.efr.achievementbot.bot.admin.session.AdminSession;
import com.efr.achievementbot.model.AchievementDefinition;
import com.efr.achievementbot.repository.achievement.AchievementDefinitionRepository;
import com.efr.achievementbot.service.achievement.AchievementService;
import com.efr.achievementbot.service.config.BotConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Обработчик админских операций с "Достижениями" (как базовыми, так и кастомными).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AchievementAdminHandler {

    private final AchievementDefinitionRepository definitionRepository;
    private final AchievementService achievementService;
    private final BotConfigService botConfigService;

    /**
     * Отображает меню по работе с достижениями (базовыми и кастомными).
     */
    public void sendAchievementMenu(JavaCodeBot bot, Long chatId) {
        AdminKeyboardUtils.sendMenu(bot, chatId,
                "Выберите операцию с достижениями:",
                new String[][]{
                        {"Добавить базовое достижение", "Список достижений"},
                        {"Выдать кастомное достижение"},
                        {"Назад"}
                }
        );
    }

    /**
     * Главный метод, который вызывается AdminMenuHandler, если мы находимся в состоянии,
     * относящемся к достижениям (ACHIEVEMENTS_MENU, ...).
     */
    public void handleAchievementMenu(Update update, JavaCodeBot bot, AdminSession session) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String text = msg.getText() != null ? msg.getText() : "";

        switch (session.getState()) {
            // Меню достижений
            case ACHIEVEMENTS_MENU -> handleAchievementsMenu(bot, chatId, text, session);

            // Шаги добавления нового базового достижения
            case ACHIEVEMENTS_ADD_TITLE -> handleAddTitle(bot, chatId, text, session);
            case ACHIEVEMENTS_ADD_DESCRIPTION -> handleAddDescription(bot, chatId, text, session);
            case ACHIEVEMENTS_ADD_WEIGHT -> handleAddWeight(bot, chatId, text, session);
            case ACHIEVEMENTS_ADD_TYPE_SELECT -> handleTypeSelect(bot, chatId, text, session);

            // Просмотр списка достижений
            case ACHIEVEMENTS_VIEW_LIST -> handleViewList(bot, chatId, text, session);

            // Детальный просмотр/редактирование/удаление
            case ACHIEVEMENTS_VIEW_DETAILS -> handleViewDetails(bot, chatId, text, session);
            case ACHIEVEMENTS_EDIT_TITLE -> handleEditTitle(bot, chatId, text, session);
            case ACHIEVEMENTS_EDIT_DESCRIPTION -> handleEditDescription(bot, chatId, text, session);
            case ACHIEVEMENTS_EDIT_WEIGHT -> handleEditWeight(bot, chatId, text, session);
            case ACHIEVEMENTS_EDIT_TYPE -> handleEditType(bot, chatId, text, session);
            case ACHIEVEMENTS_DELETE_CONFIRM -> handleDeleteConfirm(bot, chatId, text, session);

            // Выдача кастомного достижения (поэтапно)
            case CUSTOM_ACHIEVEMENTS_USER_TAG -> handleCustomUserTag(bot, chatId, text, session);
            case CUSTOM_ACHIEVEMENTS_NAME -> handleCustomName(bot, chatId, text, session);
            case CUSTOM_ACHIEVEMENTS_TITLE -> handleCustomTitle(bot, chatId, text, session);
            case CUSTOM_ACHIEVEMENTS_DESCRIPTION -> handleCustomDescription(bot, chatId, text, session);
            case CUSTOM_ACHIEVEMENTS_WEIGHT -> handleCustomWeight(bot, chatId, text, session);

            default -> {
                log.warn("AchievementAdminHandler: неизвестное состояние {}. Возвращаемся в меню.", session.getState());
                session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
                sendAchievementMenu(bot, chatId);
            }
        }
    }

    /**
     * Обработка "верхнего" меню достижений:
     * 1) Добавить базовое достижение
     * 2) Список достижений
     * 3) Выдать кастомное достижение
     * 4) Назад
     */
    private void handleAchievementsMenu(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        switch (text) {
            case "Добавить базовое достижение" -> {
                // Готовим новый объект AchievementDefinition (пока пустой) и начинаем wizard
                AchievementDefinition def = new AchievementDefinition();
                session.setTempAchievementDefinition(def);

                session.setState(AdminMenuState.ACHIEVEMENTS_ADD_TITLE);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите заголовок достижения:");
            }
            case "Список достижений" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_VIEW_LIST);
                showAchievementsList(bot, chatId, session);
            }
            case "Выдать кастомное достижение" -> {
                // Начинаем новый wizard по кастомным достижениям
                session.setCustomUserTag(null);
                session.setCustomName(null);
                session.setCustomTitle(null);
                session.setCustomDescription(null);
                session.setCustomWeight(null);

                session.setState(AdminMenuState.CUSTOM_ACHIEVEMENTS_USER_TAG);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите тег пользователя (@username):");
            }
            case "Назад" -> {
                // Возвращаемся в главное меню
                session.setState(AdminMenuState.MAIN_MENU);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Возвращаюсь в главное меню. /start");
            }
            default -> {
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Неизвестная команда. Повторите:");
                sendAchievementMenu(bot, chatId);
            }
        }
    }

    // ------------------- 1) Wizard по добавлению базового достижения -------------------

    private void handleAddTitle(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        // title
        session.getTempAchievementDefinition().setTitle(text);

        session.setState(AdminMenuState.ACHIEVEMENTS_ADD_DESCRIPTION);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите описание достижения:");
    }

    private void handleAddDescription(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        // description
        session.getTempAchievementDefinition().setDescription(text);

        session.setState(AdminMenuState.ACHIEVEMENTS_ADD_WEIGHT);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите вес (кол-во очков) для достижения (целое число):");
    }

    private void handleAddWeight(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        try {
            int weight = Integer.parseInt(text);
            session.getTempAchievementDefinition().setWeight(weight);
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                    "Некорректное число. Повторите или введите 'Назад'.");
            return;
        }

        session.setState(AdminMenuState.ACHIEVEMENTS_ADD_TYPE_SELECT);
        AdminKeyboardUtils.sendMenu(bot, chatId,
                "Выберите тип (стратегию) достижения:",
                new String[][]{
                        {"message_count", "keyword"},
                        {"media", "sticker", "reaction"},
                        {"Отмена"}
                }
        );
    }

    private void handleTypeSelect(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if ("Отмена".equalsIgnoreCase(text)) {
            session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
            sendAchievementMenu(bot, chatId);
            return;
        }

        // допущение: допустимые значения: message_count, keyword, media, sticker, reaction
        session.getTempAchievementDefinition().setType(text);

        // Сохраняем в БД
        AchievementDefinition saved = definitionRepository.save(session.getTempAchievementDefinition());

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                "Новое достижение успешно сохранено! ID=" + saved.getId());

        // Завершаем wizard: возвращаемся в меню
        session.setTempAchievementDefinition(null);
        session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
        sendAchievementMenu(bot, chatId);
    }

    // ------------------- 2) Просмотр списка достижений -------------------

    private void showAchievementsList(JavaCodeBot bot, Long chatId, AdminSession session) {
        List<AchievementDefinition> list = definitionRepository.findAll();
        if (list.isEmpty()) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Нет ни одного достижения в базе.");
            session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
            return;
        }

        StringBuilder sb = new StringBuilder("Список доступных достижений:\n");
        for (AchievementDefinition def : list) {
            sb.append("• ID=").append(def.getId())
                    .append(" : ").append(def.getTitle())
                    .append(" (type=").append(def.getType()).append(")\n");
        }
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, sb.toString());

        // Переходим в состояние "выберите ID"
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                "Введите ID достижения, чтобы просмотреть/редактировать, или 'Назад' для возврата.");
    }

    private void handleViewList(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if ("Назад".equalsIgnoreCase(text)) {
            session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
            sendAchievementMenu(bot, chatId);
            return;
        }
        // Пытаемся парсить ID
        try {
            Long id = Long.parseLong(text);
            AchievementDefinition def = definitionRepository.findById(id).orElse(null);
            if (def == null) {
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Нет достижения с таким ID. Повторите или введите 'Назад'.");
                return;
            }

            session.setEditingAchievementId(id);
            session.setState(AdminMenuState.ACHIEVEMENTS_VIEW_DETAILS);

            // Показываем детали
            String info = String.format(
                    "ID=%d\nName=%s\nTitle=%s\nDescription=%s\nType=%s\nWeight=%s",
                    def.getId(),
                    def.getName() == null ? "(null)" : def.getName(),
                    def.getTitle(),
                    def.getDescription(),
                    def.getType(),
                    def.getWeight()
            );
            AdminKeyboardUtils.sendMenu(bot, chatId,
                    "Детали достижения:\n\n" + info + "\n\nЧто делаем?",
                    new String[][] {
                            {"Редактировать заголовок", "Редактировать описание"},
                            {"Редактировать вес", "Редактировать тип"},
                            {"Удалить", "Назад"}
                    }
            );
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                    "Некорректный формат ID. Повторите или 'Назад'.");
        }
    }

    // ------------------- 3) Детальный просмотр/редактирование/удаление -------------------

    private void handleViewDetails(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if (session.getEditingAchievementId() == null) {
            session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
            sendAchievementMenu(bot, chatId);
            return;
        }
        switch (text) {
            case "Редактировать заголовок" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_EDIT_TITLE);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новый заголовок:");
            }
            case "Редактировать описание" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_EDIT_DESCRIPTION);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новое описание:");
            }
            case "Редактировать вес" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_EDIT_WEIGHT);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новый вес (целое число):");
            }
            case "Редактировать тип" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_EDIT_TYPE);
                AdminKeyboardUtils.sendMenu(bot, chatId,
                        "Выберите новый тип достижения:",
                        new String[][] {
                                {"message_count", "keyword"},
                                {"media", "sticker", "reaction"},
                                {"Отмена"}
                        }
                );
            }
            case "Удалить" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_DELETE_CONFIRM);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Вы уверены, что хотите удалить достижение? Введите 'Да' или 'Нет'.");
            }
            case "Назад" -> {
                session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
                sendAchievementMenu(bot, chatId);
            }
            default -> AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Непонятная команда. Повторите.");
        }
    }

    private void handleEditTitle(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        AchievementDefinition def = getEditingDefinition(session);
        if (def == null) {
            sendAchievementMenu(bot, chatId, session);
            return;
        }
        def.setTitle(text);
        definitionRepository.save(def);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Заголовок обновлён!");
        goBackToDetails(bot, chatId, def, session);
    }

    private void handleEditDescription(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        AchievementDefinition def = getEditingDefinition(session);
        if (def == null) {
            sendAchievementMenu(bot, chatId, session);
            return;
        }
        def.setDescription(text);
        definitionRepository.save(def);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Описание обновлено!");
        goBackToDetails(bot, chatId, def, session);
    }

    private void handleEditWeight(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        AchievementDefinition def = getEditingDefinition(session);
        if (def == null) {
            sendAchievementMenu(bot, chatId, session);
            return;
        }
        try {
            int newWeight = Integer.parseInt(text);
            def.setWeight(newWeight);
            definitionRepository.save(def);
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Вес обновлён!");
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Некорректное число. Попробуйте снова.");
            return;
        }
        goBackToDetails(bot, chatId, def, session);
    }

    private void handleEditType(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if ("Отмена".equalsIgnoreCase(text)) {
            session.setState(AdminMenuState.ACHIEVEMENTS_VIEW_DETAILS);
            goBackToDetails(bot, chatId, getEditingDefinition(session), session);
            return;
        }

        AchievementDefinition def = getEditingDefinition(session);
        if (def == null) {
            sendAchievementMenu(bot, chatId, session);
            return;
        }
        def.setType(text);
        definitionRepository.save(def);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Тип обновлён!");

        goBackToDetails(bot, chatId, def, session);
    }

    private void handleDeleteConfirm(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if (session.getEditingAchievementId() == null) {
            session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
            sendAchievementMenu(bot, chatId);
            return;
        }

        if ("Да".equalsIgnoreCase(text)) {
            definitionRepository.deleteById(session.getEditingAchievementId());
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Достижение удалено.");

            session.setEditingAchievementId(null);
            session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
            sendAchievementMenu(bot, chatId);
        } else if ("Нет".equalsIgnoreCase(text)) {
            // отказываемся удалять
            AchievementDefinition def = getEditingDefinition(session);
            if (def != null) {
                goBackToDetails(bot, chatId, def, session);
            } else {
                session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
                sendAchievementMenu(bot, chatId);
            }
        } else {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Непонятный ответ. Введите 'Да' или 'Нет'.");
        }
    }

    /**
     * Вспомогательный метод для возвращения к экрану "деталей достижения" после редактирования.
     */
    private void goBackToDetails(JavaCodeBot bot, Long chatId, AchievementDefinition def, AdminSession session) {
        session.setState(AdminMenuState.ACHIEVEMENTS_VIEW_DETAILS);
        String info = String.format(
                "ID=%d\nName=%s\nTitle=%s\nDescription=%s\nType=%s\nWeight=%s",
                def.getId(),
                def.getName(),
                def.getTitle(),
                def.getDescription(),
                def.getType(),
                def.getWeight()
        );
        AdminKeyboardUtils.sendMenu(bot, chatId,
                "Детали достижения:\n\n" + info + "\n\nЧто делаем?",
                new String[][] {
                        {"Редактировать заголовок", "Редактировать описание"},
                        {"Редактировать вес", "Редактировать тип"},
                        {"Удалить", "Назад"}
                }
        );
    }

    /**
     * Получение текущего редактируемого объекта AchievementDefinition из БД.
     */
    private AchievementDefinition getEditingDefinition(AdminSession session) {
        if (session.getEditingAchievementId() == null) {
            return null;
        }
        return definitionRepository.findById(session.getEditingAchievementId()).orElse(null);
    }

    private void sendAchievementMenu(JavaCodeBot bot, Long chatId, AdminSession session) {
        session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
        sendAchievementMenu(bot, chatId);
    }

    // ------------------- 4) Выдача кастомного достижения -------------------
    private void handleCustomUserTag(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if (!text.startsWith("@")) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                    "Тег пользователя должен начинаться с @. Повторите:");
            return;
        }
        session.setCustomUserTag(text);
        session.setState(AdminMenuState.CUSTOM_ACHIEVEMENTS_NAME);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите техническое имя (name) достижения (латиницей, без пробелов):");
    }

    private void handleCustomName(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.setCustomName(text);
        session.setState(AdminMenuState.CUSTOM_ACHIEVEMENTS_TITLE);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите заголовок достижения:");
    }

    private void handleCustomTitle(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.setCustomTitle(text);
        session.setState(AdminMenuState.CUSTOM_ACHIEVEMENTS_DESCRIPTION);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите описание достижения:");
    }

    private void handleCustomDescription(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.setCustomDescription(text);
        session.setState(AdminMenuState.CUSTOM_ACHIEVEMENTS_WEIGHT);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите вес (кол-во очков) для этого достижения:");
    }

    private void handleCustomWeight(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        int weight;
        try {
            weight = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Некорректное число. Повторите ввод или 'Отмена'.");
            return;
        }
        session.setCustomWeight(weight);

        // Выдаём кастомное достижение
        achievementService.awardCustomAchievement(
                session.getCustomUserTag(),
                session.getCustomName(),
                session.getCustomTitle(),
                session.getCustomDescription(),
                session.getCustomWeight(),
                botConfigService.getConfig().getGroupId(),
                bot
        );

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Кастомное достижение успешно выдано!");

        // Сбрасываем поля
        session.setCustomUserTag(null);
        session.setCustomName(null);
        session.setCustomTitle(null);
        session.setCustomDescription(null);
        session.setCustomWeight(null);

        session.setState(AdminMenuState.ACHIEVEMENTS_MENU);
        sendAchievementMenu(bot, chatId);
    }
}
