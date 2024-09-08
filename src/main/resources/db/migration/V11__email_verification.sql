CREATE TABLE email_verification
(
    id                UUID PRIMARY KEY NOT NULL,
    verification_code varchar(255)     NOT NULL,
    username          varchar(255)     NOT NULL,
    email             varchar(255)     NOT NULL,
    verified          BOOLEAN          NOT NULL,
    created_date      timestamp        NOT NULL,
    created_by        varchar(255)     NOT NULL,
    version           integer          NOT NULL
);