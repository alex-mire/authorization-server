CREATE TABLE user_group
(
    id           UUID PRIMARY KEY      NOT NULL,
    group_id     UUID references public.group NOT NULL,
    user_id      UUID references public.user  NOT NULL,
    created_date timestamp             NOT NULL,
    created_by   varchar(255)          NOT NULL,
    version      integer               NOT NULL
);