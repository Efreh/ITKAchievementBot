-- =========================================
-- ACHIEVEMENT_DEFINITION
-- =========================================
CREATE TABLE achievement_definition (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255),
  title VARCHAR(255) NOT NULL,
  description TEXT,
  type VARCHAR(255),
  required_value INTEGER,
  required_keyword VARCHAR(255),
  weight INTEGER
);

-- =========================================
-- USERS (UserDB)
-- в коде @Entity(name = "users"), поэтому таблица называется "users"
-- =========================================
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  user_tag VARCHAR(255),
  telegram_id BIGINT,
  chat_id BIGINT,
  user_name VARCHAR(255),
  message_count INT,
  weekly_message_count INT,
  reaction_count INT,
  media_count INT,
  sticker_count INT,
  achievement_score INT,
  weekly_achievement_score INT,
  monthly_achievement_score INT,
  last_activity TIMESTAMP
);

-- =========================================
-- ACHIEVEMENT
-- Содержит внешние ключи на users и achievement_definition
-- =========================================
CREATE TABLE achievement (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT,
  definition_id BIGINT,
  awarded_at TIMESTAMP,

  CONSTRAINT fk_achievement_user
    FOREIGN KEY (user_id) 
      REFERENCES users (id),

  CONSTRAINT fk_achievement_definition
    FOREIGN KEY (definition_id) 
      REFERENCES achievement_definition (id)
);

-- =========================================
-- BOT_CONFIG_DB
-- У вас @Id private Long id = 1L по умолчанию
-- Поэтому делаем id обычным BIGINT primary key (без autoincrement)
-- =========================================
CREATE TABLE bot_config_db (
  id BIGINT PRIMARY KEY,  -- ожидается одна запись с id=1
  admin_id BIGINT,
  group_id BIGINT,

  cooldown INT,
  goblin_enabled BOOLEAN,

  goblin_spawn_days_min INT,
  goblin_spawn_days_max INT,
  goblin_spawn_hour_start INT,
  goblin_spawn_hour_end INT,

  achievement_text_color VARCHAR(255),
  dashboard_text_color VARCHAR(255)
);

-- =========================================
-- GOBLIN
-- =========================================
CREATE TABLE goblin (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  button_text VARCHAR(255),
  success_message TEXT,
  failure_message TEXT,
  award_points INT
);