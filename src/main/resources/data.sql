-- MessageCountAchievementStrategy (Счётчик сообщений)
INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('novice_dark_forest', 'Новичок Тёмного Леса', 'Отправь свои первые 10 сообщений в чат, полный опасностей', 'messageCount', 10);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('wanderer_forgotten_lands', 'Странник Забытых Земель', '50 сообщений. Ты начинаешь понимать язык местных', 'messageCount', 50);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('warrior_runestones', 'Воин Рунных Камней', '100 сообщений. Твои слова оставляют след в истории', 'messageCount', 100);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('hero_cursed_wastelands', 'Герой Проклятых Пустошей', '250 сообщений. Ты стал легендой среди авантюристов', 'messageCount', 250);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('legend_eternal_chaos', 'Легенда Вечного Хаоса', '500 сообщений. Твои слова эхом разносятся по всему миру', 'messageCount', 500);

-- KeywordAchievementStrategy (Ключевые слова)
INSERT INTO achievement_definition (name, title, description, type, required_keyword)
VALUES ('bug_hunter', 'Охотник на Багов', 'Произнеси запретное слово "баг" в чате', 'keyword', 'баг');

INSERT INTO achievement_definition (name, title, description, type, required_keyword)
VALUES ('syntax_necromancer', 'Синтаксический Некромант', 'Воскреси "умершую" переменную — скажи "рефакторинг"', 'keyword', 'рефакторинг');

INSERT INTO achievement_definition (name, title, description, type, required_keyword)
VALUES ('hunter_became_prey', 'Охотник стал добычей', 'Упомяни "Высоко-нагруженные приложения." в беседе', 'keyword', 'кабанчик');

INSERT INTO achievement_definition (name, title, description, type, required_keyword)
VALUES ('keeper_secret_keys', 'Хранитель Секретных Ключей', 'Упомяни "API ключ" в беседе', 'keyword', 'API ключ');

INSERT INTO achievement_definition (name, title, description, type, required_keyword)
VALUES ('lord_infinite_loops', 'Повелитель Бесконечных Циклов', 'Произнеси "while True" — и узри вечность', 'keyword', 'while True');

-- ReactionStrategy (Реакции)
INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('soul_collector', 'Собиратель Душ', 'Получи 10 реакций на своё сообщение', 'reaction', 10);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('king_meme_throne', 'Король Мем-Тронов', '25 реакций. Твой мем стал королём чата', 'reaction', 25);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('lord_hungry_eyes', 'Властелин Голодных Глаз', '50 реакций. Твоё сообщение — новый стандарт', 'reaction', 50);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('architect_viral_scrolls', 'Архитектор Вирусных Свитков', '100 реакций. Твой пост стал реликвией гильдии', 'reaction', 100);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('god_likes_dislikes', 'Бог Лайков и Дизлайков', '250 реакций. Твоё слово — закон', 'reaction', 250);

-- MediaStrategy (Медиа)
INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('artifact_collector', 'Собиратель Артефактов', 'Отправь 10 медиафайлов. Твоя коллекция начинает расти', 'media', 10);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('keeper_ancient_scrolls', 'Хранитель Древних Свитков', '50 медиафайлов. Ты — архивариус гильдии', 'media', 50);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('master_multimedia_rituals', 'Мастер Мультимедийных Ритуалов', '100 медиафайлов. Твои вложения — легенда', 'media', 100);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('lord_digital_ruins', 'Повелитель Цифровых Руин', '250 медиафайлов. Ты затопил чат своими сокровищами', 'media', 250);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('legend_media_vaults', 'Легенда Медиа-Хранилищ', '500 медиафайлов. Твоя коллекция — наследие веков', 'media', 500);

-- StickerStrategy (Стикеры)
INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('novice_sticker_battles', 'Новичок Стикерных Битв', 'Отправь 10 стикеров. Ты только начинаешь свой путь', 'sticker', 10);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('master_meme_rituals', 'Мастер Мем-Ритуалов', '50 стикеров. Ты знаешь, как развлечь гильдию', 'sticker', 50);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('king_sticker_kingdoms', 'Король Стикерных Королевств', '100 стикеров. Твоя коллекция впечатляет', 'sticker', 100);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('lord_animated_seals', 'Властелин Анимированных Печатей', '250 стикеров. Ты — живая легенда стикерных войн', 'sticker', 250);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('god_sticker_madness', 'Бог Стикерного Безумия', '500 стикеров. Ты достиг вершины мастерства', 'sticker', 500);