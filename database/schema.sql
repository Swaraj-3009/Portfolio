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
    id TINYINT UNSIGNED NOT NULL,
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

INSERT IGNORE INTO my_profile (id) VALUES (1);