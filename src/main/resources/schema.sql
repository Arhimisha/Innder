CREATE TABLE IF NOT EXISTS users
(
    id                      INTEGER PRIMARY KEY AUTO_INCREMENT,        
    username                VARCHAR(255) UNIQUE NOT NULL,
    password                VARCHAR(255)        NOT NULL,
    -- стандартные для spring security
    enabled                 BOOLEAN             NOT NULL DEFAULT TRUE, -- включен ли аккаунт
    account_non_expired     BOOLEAN             NOT NULL DEFAULT TRUE, -- время действия аккаунта не истекло
    account_non_locked      BOOLEAN             NOT NULL DEFAULT TRUE, -- аккаунт не заблокирован
    credentials_non_expired BOOLEAN             NOT NULL DEFAULT TRUE  -- не истекло ли время действия пароля
);


CREATE TABLE IF NOT EXISTS authorities
(
    id        INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id   INTEGER      NOT NULL REFERENCES users,
    authority VARCHAR(255) NOT NULL
);


