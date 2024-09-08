CREATE TABLE role
(
    id UUID PRIMARY KEY NOT NULL,
    code               varchar(255) UNIQUE      NOT NULL,
    description        varchar(255)             NOT NULL,
    created_date       timestamp                NOT NULL,
    last_modified_date timestamp                NOT NULL,
    created_by         varchar(255)             NOT NULL,
    last_modified_by   varchar(255)             NOT NULL,
    version            integer                  NOT NULL
);