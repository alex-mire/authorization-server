CREATE TABLE user_email
(
    id                 UUID PRIMARY KEY            NOT NULL,
    email              varchar(255) UNIQUE         NOT NULL,
    user_id            UUID references public.user NOT NULL,
    is_default         BOOLEAN                     NOT NULL,
    created_date       timestamp                   NOT NULL,
    last_modified_date timestamp                   NOT NULL,
    created_by         varchar(255)                NOT NULL,
    last_modified_by   varchar(255)                NOT NULL,
    version            integer                     NOT NULL
);