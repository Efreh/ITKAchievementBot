package com.efr.achievementbot.service.user;

import com.efr.achievementbot.model.UserDB;
import com.efr.achievementbot.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserActivityServiceTest {

    private UserRepository userRepository;
    private UserActivityService userActivityService;

    @BeforeEach
    public void setUp() {
        // Мокаем репозиторий и создаем UserService и UserActivityService
        userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserService(userRepository);
        userActivityService = new UserActivityService(userService);
    }

    @Test
    public void testUpdateUserActivity_NewUser() {
        // Мокаем объект сообщения
        Message message = Mockito.mock(Message.class);
        Chat chat = Mockito.mock(Chat.class);
        User telegramUser = Mockito.mock(User.class);

        // Настраиваем поведение мока
        when(message.getChatId()).thenReturn(12345L);
        when(message.getFrom()).thenReturn(telegramUser);
        when(telegramUser.getId()).thenReturn(111L);
        when(telegramUser.getUserName()).thenReturn("testuser");

        // Симулируем, что пользователя нет в БД (возвращаем null)
        when(userRepository.findByTelegramIdAndChatId(111L, 12345L)).thenReturn(null);
        // При сохранении возвращаем созданный объект
        when(userRepository.save(any(UserDB.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Вызываем метод обновления активности
        UserDB user = userActivityService.updateUserActivity(message);

        // Проверяем, что пользователь создан и заполнены основные поля
        assertNotNull(user);
        assertEquals(111L, user.getTelegramId());
        assertEquals(12345L, user.getChatId());
        assertEquals("@testuser", user.getUserTag());
        assertEquals(1, user.getMessageCount());
    }
}