CREATE TABLE IF NOT EXISTS achievement_definition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    title VARCHAR(255),
    description VARCHAR(255),
    type VARCHAR(50),
    required_value INT,
    required_keyword VARCHAR(255)
);
