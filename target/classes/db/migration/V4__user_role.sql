CREATE TABLE AUTHORIZATION_SERVICE.USER_ROLE
(
    id           UUID PRIMARY KEY NOT NULL,
    user_id      UUID             NOT NULL,
    role_id      UUID             NOT NULL,
    created_date timestamp        NOT NULL,
    created_by   varchar(255)     NOT NULL,
    version            integer                  NOT NULL
);