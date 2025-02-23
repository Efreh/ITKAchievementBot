# 📘 Руководство по установке JavaCodeBot

Это руководство описывает, как установить и запустить JavaCodeBot с использованием Docker Compose. Следуйте пошаговым инструкциям для корректной настройки и запуска бота.

---

## 📋 1. Предварительные требования

- **Docker** и **Docker Compose** должны быть установлены на вашей машине или сервере.  
  📄 [Инструкция по установке Docker](https://docs.docker.com/engine/install/)  
  📄 [Инструкция по установке Docker Compose](https://docs.docker.com/compose/install/)

- Зарегистрируйте бота в Telegram через **BotFather** (подробности ниже).

---

## 🤖 2. Регистрация бота в Telegram через BotFather

1. **Найдите BotFather**  
   Откройте Telegram и найдите пользователя [@BotFather](https://t.me/BotFather).

2. **Создайте нового бота**  
   Отправьте команду:

   ```
   /newbot
   ```

   Следуйте инструкциям: укажите имя и юзернейм для вашего бота. После успешного создания BotFather пришлёт вам **токен** для доступа к API.

3. **Отключите режим конфиденциальности**  
   Чтобы бот мог получать все сообщения (например, для работы в группах), необходимо отключить режим конфиденциальности. Для этого отправьте команду:

   ```
   /setprivacy
   ```

   Выберите вашего бота и выберите опцию **Disable**. Это позволит боту видеть все сообщения в группе.

---

## 📁 3. Подготовка файлов проекта

### 3.1. 📄 Создание файла .env

В корневой папке проекта создайте файл `.env` и укажите в нём следующие переменные:

```
BOT_TOKEN=ВАШ_ТОКЕН_ОТ_BOTFATHER
BOT_USERNAME=ЮЗЕРНЕЙМ_ВАШЕГО_БОТА
BOT_SECRET_KEY=ВАШ_СЕКРЕТНЫЙ_КЛЮЧ
```

*Примечание:* Замените значения на реальные данные вашего бота.

### 3.2. 🐳 Dockerfile и docker-compose.yml

**Пример `docker-compose.yml`:**

```yaml
version: "3.8"

services:
  postgres:
    image: postgres:15-alpine
    container_name: itkachievementbot_db
    environment:
      POSTGRES_DB: itkach
      POSTGRES_USER: itkach
      POSTGRES_PASSWORD: itkach
    volumes:
      - db_data:/var/lib/postgresql/data
      - ./sql:/docker-entrypoint-initdb.d
    ports:
      - "5432:5432"

  bot:
    build: .
    container_name: itkachievementbot_app
    depends_on:
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/itkach
      SPRING_DATASOURCE_USERNAME: itkach
      SPRING_DATASOURCE_PASSWORD: itkach
      SPRING_JPA_HIBERNATE_DDL_AUTO: none
      bot.token: ${BOT_TOKEN}
      bot.username: ${BOT_USERNAME}
      bot.secretKey: ${BOT_SECRET_KEY}
    ports:
      - "8080:8080"

volumes:
  db_data:
```

*Примечание:* Убедитесь, что SQL-скрипты для инициализации базы данных находятся в директории `./sql`.

---

## 🚀 4. Сборка и запуск проекта

1. **Сборка Docker-контейнеров:**

   ```bash
   docker-compose build
   ```

2. **Запуск контейнеров в фоне:**

   ```bash
   docker-compose up -d
   ```

3. **Проверка работы бота:**

   Посмотрите логи для проверки:

   ```bash
   docker-compose logs -f bot
   ```

---

## 🛡️ 5. Регистрация администратора и чата

### 5.1. 👤 Регистрация администратора

1. Откройте личный чат с ботом.
2. Отправьте команду:

   ```
   /register_admin <ВАШ_СЕКРЕТНЫЙ_КЛЮЧ>
   ```

   *Примечание:* Ключ должен совпадать с `BOT_SECRET_KEY` из `.env` файла.

### 5.2. 💬 Регистрация чата

1. **Добавьте бота в группу или канал.**
2. **Дайте боту права на чтение и удаление сообщений.**
3. В чате с ботом отправьте команду:

   ```
   /register_chat
   ```
### 5.3. 💬 Запуск панели администратора 

1. В чате с ботом отправьте команду:

   ```
   /start
   ```

---

## 🔄 6. Обновление и перезапуск

1. **Внесите необходимые изменения в код или конфигурации.**
2. **Пересоберите контейнеры:**

   ```bash
   docker-compose up --build
   ```

3. **Перезапустите только контейнер бота:**

   ```bash
   docker-compose restart bot
   ```

---

## ✅ Заключение

Поздравляем! 🎉 Вы успешно развернули JavaCodeBot. Теперь бот готов к работе и может быть добавлен в любые чаты или группы для взаимодействия. Если возникнут ошибки — проверьте логи контейнера и убедитесь в правильности настроек.

**Удачного использования!** 🚀

