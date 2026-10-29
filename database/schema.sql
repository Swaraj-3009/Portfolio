CREATE DATABASE IF NOT EXISTS portfolio;

USE portfolio;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(64) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password VARCHAR(255) NOT NULL,
    -- Relationship roles progress from follower to requestedFriends to friends to family.
    -- Keep 'user' for compatibility with older/demo accounts.
    role VARCHAR(32) NOT NULL DEFAULT 'follower',
    PRIMARY KEY (username),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS does not update existing installations. Keep their
-- default role aligned with newly registered accounts as well.
ALTER TABLE users MODIFY COLUMN role VARCHAR(32) NOT NULL DEFAULT 'follower';

CREATE TABLE IF NOT EXISTS admin (
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS my_profile (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL DEFAULT '',
    about_me TEXT,
    email VARCHAR(254) NOT NULL DEFAULT '',
    github_url VARCHAR(2048) DEFAULT '',
    linkedin_url VARCHAR(2048) DEFAULT '',
    address TEXT,
    phone VARCHAR(32) DEFAULT '',
    profile_image VARCHAR(2048) DEFAULT '',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS my_skills (
    id INT NOT NULL AUTO_INCREMENT,
    skill_name VARCHAR(120) NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS my_projects (
    id INT NOT NULL AUTO_INCREMENT,
    project_name VARCHAR(255) NOT NULL,
    project_description TEXT,
    technologies_used TEXT,
    github_url VARCHAR(2048) DEFAULT '',
    live_url VARCHAR(2048) DEFAULT '',
    project_image VARCHAR(2048) DEFAULT '',
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS my_education (
    id INT NOT NULL AUTO_INCREMENT,
    degree VARCHAR(120) NOT NULL DEFAULT '',
    institution VARCHAR(255) NOT NULL DEFAULT '',
    year_of_passing VARCHAR(20) NOT NULL DEFAULT '',
    grade VARCHAR(50) NOT NULL DEFAULT '',
    description TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS portfolio_item_order (
    section_key VARCHAR(24) NOT NULL,
    item_id INT NOT NULL,
    sort_order INT NOT NULL,
    PRIMARY KEY (section_key, item_id),
    KEY idx_portfolio_item_order (section_key, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_user (
    conversation_id INT NOT NULL AUTO_INCREMENT,
    user1 VARCHAR(64) NOT NULL,
    user2 VARCHAR(64) NOT NULL,
    PRIMARY KEY (conversation_id),
    UNIQUE KEY uq_chat_user_pair (user1, user2)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS chat_settings (
    setting_id TINYINT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (setting_id),
    CONSTRAINT chk_chat_settings_singleton CHECK (setting_id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO chat_settings (setting_id, enabled) VALUES (1, TRUE);

CREATE TABLE IF NOT EXISTS chat (
    message_id BIGINT NOT NULL AUTO_INCREMENT,
    conversation_id INT NOT NULL,
    message TEXT NOT NULL,
    sender VARCHAR(64) NOT NULL,
    reciever VARCHAR(64) NOT NULL,
    time DATETIME(6) NOT NULL,
    PRIMARY KEY (message_id),
    KEY idx_chat_conversation_time (conversation_id, time, message_id),
    CONSTRAINT fk_chat_conversation FOREIGN KEY (conversation_id)
        REFERENCES chat_user (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO admin (username, password) VALUES ('admin', 'admin');
INSERT IGNORE INTO users (username, email, password, role) VALUES ('user', 'user@example.com', 'user', 'follower');
UPDATE users SET role = 'follower' WHERE username = 'user' AND role = 'user';
INSERT IGNORE INTO my_profile (id) VALUES (1);
