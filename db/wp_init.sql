DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE SEQUENCE public.wp_users_id_seq;
CREATE SEQUENCE public.wp_usermeta_id_seq;

CREATE TABLE public.wp_users (
  id         BIGINT       NOT NULL DEFAULT nextval('public.wp_users_id_seq'),
  user_login VARCHAR(60)  NOT NULL,
  user_pass  VARCHAR(255) NOT NULL,
  CONSTRAINT id_wp_users PRIMARY KEY (id)

);

CREATE TABLE public.wp_usermeta (
  id         BIGINT       NOT NULL DEFAULT nextval('public.wp_usermeta_id_seq'),
  user_id    BIGINT       NOT NULL,
  meta_key   VARCHAR(255) NOT NULL,
  meta_value VARCHAR(255) NOT NULL,
  CONSTRAINT pk_wp_usermeta PRIMARY KEY (user_id, meta_key, meta_value)
);


ALTER SEQUENCE public.wp_users_id_seq OWNED BY public.wp_users.id;
ALTER SEQUENCE public.wp_usermeta_id_seq OWNED BY public.wp_usermeta.id;

ALTER TABLE public.wp_usermeta ADD CONSTRAINT usermeta_user_fk
FOREIGN KEY (user_id)
REFERENCES public.wp_users (id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;


INSERT INTO wp_users (user_login, user_pass) VALUES ('part', '$P$BBZE7jzPjVxY4VnMgsuYuvM9T.EOZD1');
INSERT INTO wp_usermeta (user_id, meta_key, meta_value)
VALUES (1, 'wp_capabilities', 'a:1:{s:13:"administrator";b:1;}');

INSERT INTO wp_users (user_login, user_pass) VALUES ('mart', '$P$BBZE7jzPjVxY4VnMgsuYuvM9T.EOZD1');
INSERT INTO wp_usermeta (user_id, meta_key, meta_value)
VALUES (2, 'wp_capabilities', 'a:1:{s:8:"customer";b:1;}');
