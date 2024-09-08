CREATE TABLE public.group
(
    id                         UUID PRIMARY KEY NOT NULL,
    name                       varchar(255) UNIQUE      NOT NULL,
    created_date               timestamp                NOT NULL,
    last_modified_date         timestamp                NOT NULL,
    created_by                 varchar(255)             NOT NULL,
    last_modified_by           varchar(255)             NOT NULL,
    version                    integer                  NOT NULL
);