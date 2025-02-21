-- MessageCountAchievementStrategy (Счётчик сообщений)
INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('novice_dark_forest', 'Новичок Тёмного Леса', 'Отправь свои первые 10 сообщений в чат, полный опасностей', 'messageCount', 10, 5);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('wanderer_forgotten_lands', 'Странник Забытых Земель', '50 сообщений. Ты начинаешь понимать язык местных', 'messageCount', 50, 10);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('warrior_runestones', 'Воин Рунных Камней', '100 сообщений. Твои слова оставляют след в истории', 'messageCount', 100, 15);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('hero_cursed_wastelands', 'Герой Проклятых Пустошей', '250 сообщений. Ты стал легендой среди авантюристов', 'messageCount', 250, 20);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('legend_eternal_chaos', 'Легенда Вечного Хаоса', '500 сообщений. Твои слова эхом разносятся по всему миру', 'messageCount', 500, 25);

-- KeywordAchievementStrategy (Ключевые слова)
INSERT INTO achievement_definition (name, title, description, type, required_keyword, weight)
VALUES ('bug_hunter', 'Охотник на Багов', 'Произнеси запретное слово "баг" в чате', 'keyword', 'баг', 5);

INSERT INTO achievement_definition (name, title, description, type, required_keyword, weight)
VALUES ('syntax_necromancer', 'Синтаксический Некромант', 'Воскреси "умершую" переменную — скажи "рефакторинг"', 'keyword', 'рефакторинг', 5);

INSERT INTO achievement_definition (name, title, description, type, required_keyword, weight)
VALUES ('hunter_became_prey', 'Охотник стал добычей', 'Упомяни "Высоко-нагруженные приложения." в беседе', 'keyword', 'кабанчик', 5);

INSERT INTO achievement_definition (name, title, description, type, required_keyword, weight)
VALUES ('keeper_secret_keys', 'Хранитель Секретных Ключей', 'Упомяни "API ключ" в беседе', 'keyword', 'API ключ', 5);

INSERT INTO achievement_definition (name, title, description, type, required_keyword, weight)
VALUES ('lord_infinite_loops', 'Повелитель Бесконечных Циклов', 'Произнеси "while True" — и узри вечность', 'keyword', 'while True', 5);

-- ReactionStrategy (Реакции)
INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('soul_collector', 'Собиратель Душ', 'Получи 10 реакций на своё сообщение', 'reaction', 10, 5);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('king_meme_throne', 'Король Мем-Тронов', '25 реакций. Твой мем стал королём чата', 'reaction', 25, 10);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('lord_hungry_eyes', 'Властелин Голодных Глаз', '50 реакций. Твоё сообщение — новый стандарт', 'reaction', 50, 15);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('architect_viral_scrolls', 'Архитектор Вирусных Свитков', '100 реакций. Твой пост стал реликвией гильдии', 'reaction', 100, 20);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('god_likes_dislikes', 'Бог Лайков и Дизлайков', '250 реакций. Твоё слово — закон', 'reaction', 250, 25);

-- MediaStrategy (Медиа)
INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('artifact_collector', 'Собиратель Артефактов', 'Отправь 10 медиафайлов. Твоя коллекция начинает расти', 'media', 10, 5);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('keeper_ancient_scrolls', 'Хранитель Древних Свитков', '50 медиафайлов. Ты — архивариус гильдии', 'media', 50, 10);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('master_multimedia_rituals', 'Мастер Мультимедийных Ритуалов', '100 медиафайлов. Твои вложения — легенда', 'media', 100, 15);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('lord_digital_ruins', 'Повелитель Цифровых Руин', '250 медиафайлов. Ты затопил чат своими сокровищами', 'media', 250, 20);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('legend_media_vaults', 'Легенда Медиа-Хранилищ', '500 медиафайлов. Твоя коллекция — наследие веков', 'media', 500, 25);

-- StickerStrategy (Стикеры)
INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('novice_sticker_battles', 'Новичок Стикерных Битв', 'Отправь 10 стикеров. Ты только начинаешь свой путь', 'sticker', 10, 5);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('master_meme_rituals', 'Мастер Мем-Ритуалов', '50 стикеров. Ты знаешь, как развлечь гильдию', 'sticker', 50, 10);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('king_sticker_kingdoms', 'Король Стикерных Королевств', '100 стикеров. Твоя коллекция впечатляет', 'sticker', 100, 15);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('lord_animated_seals', 'Властелин Анимированных Печатей', '250 стикеров. Ты — живая легенда стикерных войн', 'sticker', 250, 20);

INSERT INTO achievement_definition (name, title, description, type, required_value, weight)
VALUES ('god_sticker_madness', 'Бог Стикерного Безумия', '500 стикеров. Ты достиг вершины мастерства', 'sticker', 500, 25);

INSERT INTO goblin (name, description, button_text, success_message, failure_message, award_points) VALUES
('Гоблин Обманщик',
 'Этот хитрый гоблин маскируется под дружелюбного HR-менеджера, но его цель – запутать вас ложными обещаниями и исчезнуть, когда дело доходит до реального оффера. Поймайте его, пока он не сбежал с вашими надеждами!',
 '🕵️‍♂️ Разоблачить обман',
 'Вы разоблачаете гоблина, забираете оффер и получаете гоблинское зелье: +50 очков восхождения, +20% к проницательности при общении с HR на 7 дней.',
 'Гоблин ускользает в портал, оставляя вам письмо с отказом и спам о "вакансиях вашей мечты".',
 50),
('Гоблин Некромант',
 'Этот древний гоблин-некромант оживляет устаревшие системы и создаёт големов из плохой документации. Его логово – лабиринт забытого кода и недописанных инструкций.',
 '🧙‍♂️ Разрушить голема',
 'Вы побеждаете гоблина, забираете оффер и получаете гоблинское зелье: +50 очков восхождения, +15% к способности разбираться в старом коде и документации на 10 дней.',
 'Гоблин уходит в портал, оставляя вам ошибку 404 и оффер, погребённый под слоем недокументированного хаоса.',
 50),
('Гоблин Банкир',
 'Этот гоблин охраняет банковскую сферу разработки, где безопасность и строгость превыше всего. Он спрятал оффер в сейфе, защищённом сложными алгоритмами.',
 '🔐 Взломать сейф',
 'Вы обезвреживаете гоблина, открываете сейф, забираете оффер и получаете гоблинское зелье: +50 очков восхождения, +25% к навыкам в области безопасности на 5 дней.',
 'Гоблин исчезает в портал, унося оффер и оставляя уведомление о блокировке аккаунта.',
 50),
('Гоблин Корпорат',
 'Этот массивный гоблин символизирует крупные российские IT-корпорации, такие как Яндекс. Он прячет оффер в лабиринте отделов и бюрократических процессов.',
 '🏢 Пробиться через бюрократию',
 'Вы находите гоблина, забираете оффер и получаете гоблинское зелье: +50 очков восхождения, +20% к навигации в больших организациях на 7 дней.',
 'Гоблин уходит в портал, оставляя вам бесконечную цепочку согласований и оффер, застрявший в "рассмотрении".',
 50),
('Гоблин Тень',
 'Этот призрачный гоблин блуждает по лабиринтам документации и внутренних wiki, утаскивая оффер в тени долгих процессов онбординга.',
 '👻 Найти путь',
 'Вы ловите гоблина, забираете оффер и получаете гоблинское зелье: +50 очков восхождения, +30% к скорости адаптации на новом проекте на 10 дней.',
 'Гоблин растворяется в портал, унося оффер и оставляя вам только устаревшую документацию.',
 50);