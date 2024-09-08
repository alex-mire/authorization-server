CREATE TABLE PUBLIC.USER
(
    id                         UUID PRIMARY KEY    NOT NULL,
    username                   varchar(255) UNIQUE NOT NULL,
    password                   varchar(255)        NOT NULL,
    is_account_non_expired     BOOLEAN             NOT NULL,
    is_account_non_locked      BOOLEAN             NOT NULL,
    is_credentials_non_expired BOOLEAN             NOT NULL,
    is_enabled                 BOOLEAN             NOT NULL,
    created_date               timestamp           NOT NULL,
    last_modified_date         timestamp           NOT NULL,
    created_by                 varchar(255)        NOT NULL,
    last_modified_by           varchar(255)        NOT NULL,
    version                    integer             NOT NULL
);