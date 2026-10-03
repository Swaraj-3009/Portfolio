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
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL DEFAULT '',
    about_me TEXT,
    email VARCHAR(254) NOT NULL DEFAULT '',
    github_url VARCHAR(2048) NOT NULL DEFAULT '',
    linkedin_url VARCHAR(2048) NOT NULL DEFAULT '',
    address TEXT,
    phone VARCHAR(32) NOT NULL DEFAULT '',
    profile_image TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS my_skills (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    skill_name VARCHAR(120) NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS my_projects (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
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
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    degree VARCHAR(120) NOT NULL DEFAULT '',
    institution VARCHAR(255) NOT NULL DEFAULT '',
    year_of_passing VARCHAR(20) NOT NULL DEFAULT '',
    grade VARCHAR(50) NOT NULL DEFAULT '',
    description TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO my_profile (id) VALUES (1);