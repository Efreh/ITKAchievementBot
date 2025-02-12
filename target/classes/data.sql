INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('first_message', 'Первое сообщение', 'Напишите первое сообщение', 'messageCount', 1);

INSERT INTO achievement_definition (name, title, description, type, required_value)
VALUES ('five_messages', 'Пять сообщений', 'Напишите 5 сообщений', 'messageCount', 5);

INSERT INTO achievement_definition (name, title, description, type, required_keyword)
VALUES ('keyword_achievement', 'Охотник стал добычей', 'Найдите секретное слово "кабанчик"', 'keyword', 'кабанчик');