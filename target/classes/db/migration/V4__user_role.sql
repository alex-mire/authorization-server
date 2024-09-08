CREATE TABLE user_role
(
    id           UUID PRIMARY KEY     NOT NULL,
    user_id      UUID references public.user NOT NULL,
    role_id      UUID references role NOT NULL,
    created_date timestamp            NOT NULL,
    created_by   varchar(255)         NOT NULL,
    version      integer              NOT NULL
);