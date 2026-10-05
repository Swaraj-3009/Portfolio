CREATE DATABASE IF NOT EXISTS portfolio;

USE portfolio;

CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(64) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'user',
    PRIMARY KEY (username),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

INSERT IGNORE INTO admin (username, password) VALUES ('admin', 'admin');
INSERT IGNORE INTO users (username, email, password, role) VALUES ('user', 'user@example.com', 'user', 'user');
INSERT IGNORE INTO my_profile (id) VALUES (1);
