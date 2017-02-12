DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE SEQUENCE public.wp_users_id_seq;

CREATE TABLE public.wp_users (
  id BIGINT NOT NULL DEFAULT nextval('public.wp_users_id_seq'),
  user_login VARCHAR(60) NOT NULL,
  user_pass VARCHAR(255) NOT NULL,
  CONSTRAINT id_wp_users PRIMARY KEY (id)

);



ALTER SEQUENCE public.wp_users_id_seq OWNED BY public.wp_users.id;

insert into wp_users (user_login, user_pass) values ('part', '$P$BBZE7jzPjVxY4VnMgsuYuvM9T.EOZD1');