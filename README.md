```markdown
# ITKAchievementBot

ITKAchievementBot — Telegram-бот, который превращает общение в настоящее приключствие. Проект сочетает элементы геймификации, RPG и IT-тематики, автоматически выдавая достижения пользователям за активность в чате.

---

## Содержание

- Описание
- Ключевые возможности
- Архитектура проекта
- Используемые технологии
- Как это работает
- Тестирование
- Вклад и развитие
- Лицензия

---

## Описание

ITKAchievementBot анализирует активность пользователей (сообщения, медиафайлы, стикеры, реакции) и автоматически выдаёт достижения на основе заданных условий. Достижения начисляются за количественные показатели или особые действия (например, использование ключевых слов с периодом «охлаждения»). Также реализована функция еженедельного награждения самого активного пользователя.

---

## Ключевые возможности

- Автоматическая выдача достижений  
  – Сообщения: Награды за достижение определённого количества сообщений  
  – Ключевые слова: Выдача достижений при использовании специальных слов (с периодом охлаждения)  
  – Реакции, медиа и стикеры: Достижения за получение реакций, отправку медиафайлов или стикеров  
  – Кастомные достижения: Возможность вручную выдавать достижения через админ-панель

- Еженедельное награждение  
  Планировщик автоматически определяет самого активного пользователя недели и выдаёт ему специальное достижение, после чего еженедельный счётчик сбрасывается.

- Асинхронная обработка  
  Долгие операции (генерация изображений, отправка уведомлений) выполняются в отдельных потоках для повышения отзывчивости бота.

- Кэширование  
  Определения достижений кэшируются для снижения нагрузки на базу данных.

- Гибкая конфигурация  
  Параметры изображений (шаблон, цвета, шрифты, позиционирование текста) настраиваются через внешний файл конфигурации.

---

## Архитектура проекта

1. **Bot Layer**  
   ITKAchievementBot получает обновления от Telegram и распределяет их между сервисами. AdminCommandHandler обрабатывает административные команды.

2. **Service Layer**  
   AchievementService проверяет условия и выдаёт достижения.  
   UserActivityService обновляет статистику активности пользователей (общие и еженедельные счётчики).  
   AchievementDefinitionService предоставляет кэшированные данные об определениях достижений.  
   AchievementNotificationService асинхронно отправляет уведомления.  
   Стратегии выдачи (MessageCount, Keyword, Reaction, Media, Sticker) реализуют логику проверки условий.

3. **Scheduler**  
   WeeklyActivityScheduler каждую неделю определяет самого активного пользователя, выдаёт ему специальное достижение и сбрасывает еженедельные счётчики.

4. **Data Layer**  
   Используется Spring Data JPA с H2-базой данных для хранения пользователей, достижений и их определений.

5. **Configuration**  
   Классы для настройки кэширования, асинхронного выполнения, планировщика и параметров бота позволяют гибко управлять приложением через application.properties.

---

## Используемые технологии

- **Язык:** Java 17  
- **Фреймворк:** Spring Boot  
- **База данных:** H2 (с возможностью замены)  
- **ORM:** Spring Data JPA  
- **Асинхронность и планирование:** Spring Async, Spring Scheduling  
- **Кэширование:** Spring Cache (ConcurrentMapCacheManager)  
- **Работа с изображениями:** ImageIO (с поддержкой TwelveMonkeys для JPEG)  
- **Telegram API:** telegrambots

---

## Как это работает

1. **Обработка обновлений:**  
   Бот получает обновления от Telegram, анализирует сообщения и обновляет статистику пользователя (общий и еженедельный счётчики).

2. **Проверка достижений:**  
   На основе заданных определений бот проверяет, выполнены ли условия для выдачи достижения с помощью различных стратегий.

3. **Выдача достижения:**  
   Если условие выполнено, генерируется изображение достижения, и пользователь получает уведомление в Telegram. Выдача происходит асинхронно.

4. **Еженедельное награждение:**  
   Планировщик каждую неделю выбирает пользователя с наибольшей активностью и выдаёт ему специальное достижение, после чего еженедельные счётчики сбрасываются.

5. **Кэширование:**  
   Определения достижений кэшируются для минимизации нагрузки на базу данных.

---

## Тестирование

В проекте реализованы юнит-тесты для основных компонентов:  
– UserActivityServiceTest проверяет обновление статистики пользователя  
– AchievementImageGeneratorTest проверяет генерацию изображений достижений

---

## Вклад и развитие

Проект создан как демонстрация возможностей геймификации в IT-сфере. Если у вас есть предложения или улучшения, создавайте Issue или Pull Request на GitHub.

---

## Лицензия

Проект распространяется под лицензией MIT.
```