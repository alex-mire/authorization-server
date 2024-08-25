CREATE TABLE authorization_service.group_role
(
    id                 UUID PRIMARY KEY    NOT NULL,
    group_id           UUID                NOT NULL,
    role_id            UUID                NOT NULL,
    created_date       timestamp           NOT NULL,
    created_by         varchar(255)        NOT NULL,
    version            integer             NOT NULL
);