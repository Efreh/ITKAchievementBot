CREATE TABLE IF NOT EXISTS achievement_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    title VARCHAR(255),
    description VARCHAR(255),
    type VARCHAR(50),
    required_value INT,
    required_keyword VARCHAR(255),
    weight INT
);

CREATE TABLE IF NOT EXISTS goblin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    button_text VARCHAR(255),
    success_message VARCHAR(1000),
    failure_message VARCHAR(1000),
    award_points INT
);