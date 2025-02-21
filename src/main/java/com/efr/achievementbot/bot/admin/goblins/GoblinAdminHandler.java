package com.efr.achievementbot.bot.admin.goblins;

import com.efr.achievementbot.bot.JavaCodeBot;
import com.efr.achievementbot.bot.admin.util.AdminKeyboardUtils;
import com.efr.achievementbot.bot.admin.session.AdminMenuState;
import com.efr.achievementbot.bot.admin.session.AdminSession;
import com.efr.achievementbot.model.Goblin;
import com.efr.achievementbot.repository.goblin.GoblinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Обработчик CRUD-операций с гоблинами в админском меню.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoblinAdminHandler {

    private final GoblinRepository goblinRepository;

    /**
     * Отправляет меню по работе с гоблинами.
     */
    public void sendGoblinMenu(JavaCodeBot bot, Long chatId) {
        AdminKeyboardUtils.sendMenu(
                bot,
                chatId,
                "Управление гоблинами. Выберите операцию:",
                new String[][]{
                        {"Добавить гоблина", "Список гоблинов"},
                        {"Назад"}
                }
        );
    }

    /**
     * Этот метод вызывается из AdminMenuHandler, если сессия находится в состоянии,
     * относящемся к гоблинам (GOBLIN_MENU и т.д.).
     */
    public void handleGoblinMenu(Update update, JavaCodeBot bot, AdminSession session) {
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String text = msg.getText() != null ? msg.getText() : "";

        switch (session.getState()) {
            case GOBLIN_MENU -> handleGoblinMainMenu(bot, chatId, text, session);

            case GOBLIN_ADD_NAME -> handleGoblinAddName(bot, chatId, text, session);
            case GOBLIN_ADD_DESCRIPTION -> handleGoblinAddDescription(bot, chatId, text, session);
            case GOBLIN_ADD_BUTTON_TEXT -> handleGoblinAddButtonText(bot, chatId, text, session);
            case GOBLIN_ADD_SUCCESS_MESSAGE -> handleGoblinAddSuccessMessage(bot, chatId, text, session);
            case GOBLIN_ADD_FAILURE_MESSAGE -> handleGoblinAddFailureMessage(bot, chatId, text, session);
            case GOBLIN_ADD_AWARD_POINTS -> handleGoblinAddAwardPoints(bot, chatId, text, session);

            case GOBLIN_VIEW_LIST -> handleGoblinViewList(bot, chatId, text, session);
            case GOBLIN_VIEW_DETAILS -> handleGoblinViewDetails(bot, chatId, text, session);
            case GOBLIN_EDIT_NAME -> handleGoblinEditName(bot, chatId, text, session);
            case GOBLIN_EDIT_DESCRIPTION -> handleGoblinEditDescription(bot, chatId, text, session);
            case GOBLIN_EDIT_BUTTON_TEXT -> handleGoblinEditButtonText(bot, chatId, text, session);
            case GOBLIN_EDIT_SUCCESS_MESSAGE -> handleGoblinEditSuccessMessage(bot, chatId, text, session);
            case GOBLIN_EDIT_FAILURE_MESSAGE -> handleGoblinEditFailureMessage(bot, chatId, text, session);
            case GOBLIN_EDIT_AWARD_POINTS -> handleGoblinEditAwardPoints(bot, chatId, text, session);
            case GOBLIN_DELETE_CONFIRM -> handleGoblinDeleteConfirm(bot, chatId, text, session);

            default -> {
                log.warn("GoblinAdminHandler: неизвестное состояние {}. Возвращаемся в GOBLIN_MENU.", session.getState());
                session.setState(AdminMenuState.GOBLIN_MENU);
                sendGoblinMenu(bot, chatId);
            }
        }
    }

    // ------------------- Главное меню гоблинов -------------------

    private void handleGoblinMainMenu(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        switch (text) {
            case "Добавить гоблина" -> {
                // Создаём пустой объект Goblin для заполнения
                Goblin g = new Goblin();
                session.setTempGoblin(g);

                session.setState(AdminMenuState.GOBLIN_ADD_NAME);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите имя (title) гоблина:");
            }
            case "Список гоблинов" -> {
                session.setState(AdminMenuState.GOBLIN_VIEW_LIST);
                showGoblinList(bot, chatId, session);
            }
            case "Назад" -> {
                // Возвращаемся в главное меню
                session.setState(AdminMenuState.MAIN_MENU);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Возвращаюсь в главное меню. /start");
            }
            default -> {
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Неизвестная команда.");
                sendGoblinMenu(bot, chatId);
            }
        }
    }

    // ------------------- Добавление нового гоблина -------------------

    private void handleGoblinAddName(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.getTempGoblin().setName(text);
        session.setState(AdminMenuState.GOBLIN_ADD_DESCRIPTION);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите описание гоблина:");
    }

    private void handleGoblinAddDescription(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.getTempGoblin().setDescription(text);
        session.setState(AdminMenuState.GOBLIN_ADD_BUTTON_TEXT);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите текст кнопки (buttonText), которая будет выводиться при спавне:");
    }

    private void handleGoblinAddButtonText(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.getTempGoblin().setButtonText(text);
        session.setState(AdminMenuState.GOBLIN_ADD_SUCCESS_MESSAGE);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите сообщение при успехе (successMessage):");
    }

    private void handleGoblinAddSuccessMessage(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.getTempGoblin().setSuccessMessage(text);
        session.setState(AdminMenuState.GOBLIN_ADD_FAILURE_MESSAGE);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите сообщение при провале (failureMessage):");
    }

    private void handleGoblinAddFailureMessage(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        session.getTempGoblin().setFailureMessage(text);
        session.setState(AdminMenuState.GOBLIN_ADD_AWARD_POINTS);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите количество очков, начисляемых за поимку (awardPoints):");
    }

    private void handleGoblinAddAwardPoints(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        int points;
        try {
            points = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Некорректное число. Повторите:");
            return;
        }
        session.getTempGoblin().setAwardPoints(points);

        // Сохраняем в БД
        Goblin newGoblin = goblinRepository.save(session.getTempGoblin());

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                "Новый гоблин успешно сохранён! ID=" + newGoblin.getId());

        session.setTempGoblin(null);
        session.setState(AdminMenuState.GOBLIN_MENU);
        sendGoblinMenu(bot, chatId);
    }

    // ------------------- Список гоблинов -------------------

    private void showGoblinList(JavaCodeBot bot, Long chatId, AdminSession session) {
        List<Goblin> list = goblinRepository.findAll();
        if (list.isEmpty()) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Список гоблинов пуст.");
            session.setState(AdminMenuState.GOBLIN_MENU);
            return;
        }

        StringBuilder sb = new StringBuilder("Список гоблинов:\n");
        for (Goblin g : list) {
            sb.append("• ID=").append(g.getId())
                    .append(" : ").append(g.getName())
                    .append("\n");
        }
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, sb.toString());
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                "Введите ID гоблина, чтобы просмотреть/редактировать, или 'Назад' для возврата.");
    }

    private void handleGoblinViewList(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if ("Назад".equalsIgnoreCase(text)) {
            session.setState(AdminMenuState.GOBLIN_MENU);
            sendGoblinMenu(bot, chatId);
            return;
        }
        try {
            Long id = Long.parseLong(text);
            Goblin goblin = goblinRepository.findById(id).orElse(null);
            if (goblin == null) {
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Нет гоблина с таким ID. Повторите или 'Назад'.");
                return;
            }
            session.setEditingGoblinId(id);
            session.setState(AdminMenuState.GOBLIN_VIEW_DETAILS);

            String info = String.format(
                    "ID=%d\nName=%s\nDescription=%s\nButtonText=%s\nSuccessMessage=%s\nFailureMessage=%s\nAwardPoints=%d",
                    goblin.getId(),
                    goblin.getName(),
                    goblin.getDescription(),
                    goblin.getButtonText(),
                    goblin.getSuccessMessage(),
                    goblin.getFailureMessage(),
                    goblin.getAwardPoints() == null ? 0 : goblin.getAwardPoints()
            );
            AdminKeyboardUtils.sendMenu(bot, chatId,
                    "Детали гоблина:\n\n" + info + "\n\nЧто делаем?",
                    new String[][]{
                            {"Редактировать имя", "Редактировать описание"},
                            {"Редактировать кнопку", "Редактировать успех/провал"},
                            {"Редактировать очки", "Удалить"},
                            {"Назад"}
                    }
            );
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Некорректный ID. Повторите или 'Назад'.");
        }
    }

    // ------------------- Детальный просмотр/редактирование/удаление -------------------

    private void handleGoblinViewDetails(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if (session.getEditingGoblinId() == null) {
            session.setState(AdminMenuState.GOBLIN_MENU);
            sendGoblinMenu(bot, chatId);
            return;
        }
        switch (text) {
            case "Редактировать имя" -> {
                session.setState(AdminMenuState.GOBLIN_EDIT_NAME);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новое имя (title) гоблина:");
            }
            case "Редактировать описание" -> {
                session.setState(AdminMenuState.GOBLIN_EDIT_DESCRIPTION);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новое описание:");
            }
            case "Редактировать кнопку" -> {
                session.setState(AdminMenuState.GOBLIN_EDIT_BUTTON_TEXT);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новый buttonText:");
            }
            case "Редактировать успех/провал" -> {
                // Можем, например, сначала попросить successMessage, потом failureMessage,
                // или разнести на два шага. Ниже — упрощённый пример на два шага (EDIT_SUCCESS_MESSAGE, EDIT_FAILURE_MESSAGE).
                session.setState(AdminMenuState.GOBLIN_EDIT_SUCCESS_MESSAGE);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новое successMessage:");
            }
            case "Редактировать очки" -> {
                session.setState(AdminMenuState.GOBLIN_EDIT_AWARD_POINTS);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новое количество очков:");
            }
            case "Удалить" -> {
                session.setState(AdminMenuState.GOBLIN_DELETE_CONFIRM);
                AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                        "Вы уверены, что хотите удалить гоблина? Введите 'Да' или 'Нет'.");
            }
            case "Назад" -> {
                session.setState(AdminMenuState.GOBLIN_MENU);
                sendGoblinMenu(bot, chatId);
            }
            default -> AdminKeyboardUtils.sendSimpleMessage(bot, chatId,
                    "Непонятная команда. Повторите или 'Назад'.");
        }
    }

    private void handleGoblinEditName(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        Goblin g = getEditingGoblin(session);
        if (g == null) {
            sendGoblinMenu(bot, chatId, session);
            return;
        }
        g.setName(text);
        goblinRepository.save(g);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Имя гоблина обновлено!");
        goBackToGoblinDetails(bot, chatId, g, session);
    }

    private void handleGoblinEditDescription(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        Goblin g = getEditingGoblin(session);
        if (g == null) {
            sendGoblinMenu(bot, chatId, session);
            return;
        }
        g.setDescription(text);
        goblinRepository.save(g);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Описание обновлено!");
        goBackToGoblinDetails(bot, chatId, g, session);
    }

    private void handleGoblinEditButtonText(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        Goblin g = getEditingGoblin(session);
        if (g == null) {
            sendGoblinMenu(bot, chatId, session);
            return;
        }
        g.setButtonText(text);
        goblinRepository.save(g);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "buttonText обновлён!");
        goBackToGoblinDetails(bot, chatId, g, session);
    }

    private void handleGoblinEditSuccessMessage(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        Goblin g = getEditingGoblin(session);
        if (g == null) {
            sendGoblinMenu(bot, chatId, session);
            return;
        }
        g.setSuccessMessage(text);
        goblinRepository.save(g);

        // Переходим к редактированию failureMessage
        session.setState(AdminMenuState.GOBLIN_EDIT_FAILURE_MESSAGE);
        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Введите новое failureMessage:");
    }

    private void handleGoblinEditFailureMessage(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        Goblin g = getEditingGoblin(session);
        if (g == null) {
            sendGoblinMenu(bot, chatId, session);
            return;
        }
        g.setFailureMessage(text);
        goblinRepository.save(g);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Success/Failure обновлены!");
        goBackToGoblinDetails(bot, chatId, g, session);
    }

    private void handleGoblinEditAwardPoints(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        Goblin g = getEditingGoblin(session);
        if (g == null) {
            sendGoblinMenu(bot, chatId, session);
            return;
        }
        int points;
        try {
            points = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Некорректное число. Повторите:");
            return;
        }
        g.setAwardPoints(points);
        goblinRepository.save(g);

        AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Очки обновлены!");
        goBackToGoblinDetails(bot, chatId, g, session);
    }

    private void handleGoblinDeleteConfirm(JavaCodeBot bot, Long chatId, String text, AdminSession session) {
        if (session.getEditingGoblinId() == null) {
            session.setState(AdminMenuState.GOBLIN_MENU);
            sendGoblinMenu(bot, chatId);
            return;
        }
        if ("Да".equalsIgnoreCase(text)) {
            goblinRepository.deleteById(session.getEditingGoblinId());
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Гоблин успешно удалён.");

            session.setEditingGoblinId(null);
            session.setState(AdminMenuState.GOBLIN_MENU);
            sendGoblinMenu(bot, chatId);
        } else if ("Нет".equalsIgnoreCase(text)) {
            Goblin g = getEditingGoblin(session);
            if (g != null) {
                goBackToGoblinDetails(bot, chatId, g, session);
            } else {
                session.setState(AdminMenuState.GOBLIN_MENU);
                sendGoblinMenu(bot, chatId);
            }
        } else {
            AdminKeyboardUtils.sendSimpleMessage(bot, chatId, "Непонятный ответ. Введите 'Да' или 'Нет'.");
        }
    }

    /**
     * Переход обратно к детальному меню конкретного гоблина после редактирования.
     */
    private void goBackToGoblinDetails(JavaCodeBot bot, Long chatId, Goblin g, AdminSession session) {
        session.setState(AdminMenuState.GOBLIN_VIEW_DETAILS);

        String info = String.format(
                "ID=%d\nName=%s\nDescription=%s\nButtonText=%s\nSuccessMessage=%s\nFailureMessage=%s\nAwardPoints=%d",
                g.getId(),
                g.getName(),
                g.getDescription(),
                g.getButtonText(),
                g.getSuccessMessage(),
                g.getFailureMessage(),
                g.getAwardPoints()
        );
        AdminKeyboardUtils.sendMenu(bot, chatId,
                "Детали гоблина:\n\n" + info + "\n\nЧто делаем?",
                new String[][]{
                        {"Редактировать имя", "Редактировать описание"},
                        {"Редактировать кнопку", "Редактировать успех/провал"},
                        {"Редактировать очки", "Удалить"},
                        {"Назад"}
                }
        );
    }

    private Goblin getEditingGoblin(AdminSession session) {
        if (session.getEditingGoblinId() == null) {
            return null;
        }
        return goblinRepository.findById(session.getEditingGoblinId()).orElse(null);
    }

    private void sendGoblinMenu(JavaCodeBot bot, Long chatId, AdminSession session) {
        session.setState(AdminMenuState.GOBLIN_MENU);
        sendGoblinMenu(bot, chatId);
    }
}
