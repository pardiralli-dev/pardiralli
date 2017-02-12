DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE SEQUENCE public.transaction_id_seq;

CREATE TABLE public.transaction (
  id              INTEGER NOT NULL DEFAULT nextval('public.transaction_id_seq'),
  is_paid         BOOLEAN NOT NULL,
  time_of_payment TIMESTAMP,
  ip_addr         VARCHAR(45),
  init_time       TIMESTAMP,
  CONSTRAINT id_transaction PRIMARY KEY (id)
);


ALTER SEQUENCE public.transaction_id_seq OWNED BY public.transaction.id;

CREATE SEQUENCE public.race_id_seq;

CREATE TABLE public.race (
  id          INTEGER NOT NULL DEFAULT nextval('public.race_id_seq'),
  beginning   DATE NOT NULL,
  finish      DATE NOT NULL,
  is_open     BOOLEAN,
  race_name   VARCHAR(50),
  description VARCHAR(2000),
  CONSTRAINT id_race PRIMARY KEY (id),
  CONSTRAINT check_dates check (beginning < finish)
);


ALTER SEQUENCE public.race_id_seq OWNED BY public.race.id;

CREATE SEQUENCE public.duck_buyer_id_seq;

CREATE TABLE public.duck_buyer (
  id           INTEGER NOT NULL DEFAULT nextval('public.duck_buyer_id_seq'),
  email        VARCHAR(256),
  pi_code      VARCHAR(11),
  CONSTRAINT id_buyer PRIMARY KEY (id)
);


ALTER SEQUENCE public.duck_buyer_id_seq OWNED BY public.duck_buyer.id;

CREATE SEQUENCE public.duck_owner_id_seq;

CREATE TABLE public.duck_owner (
  id           INTEGER NOT NULL DEFAULT nextval('public.duck_owner_id_seq'),
  first_name   VARCHAR(100),
  last_name    VARCHAR(100),
  phone_number VARCHAR(50),
  CONSTRAINT id_owner PRIMARY KEY (id)
);


ALTER SEQUENCE public.duck_owner_id_seq OWNED BY public.duck_owner.id;

CREATE SEQUENCE public.duck_id_seq;

CREATE TABLE public.duck (
  id               INTEGER NOT NULL DEFAULT nextval('public.duck_id_seq'),
  date_of_purchase DATE,
  owner_id         INTEGER NOT NULL,
  buyer_id         INTEGER NOT NULL,
  race_id          INTEGER NOT NULL,
  serial_number    INTEGER,
  time_of_purchase TIMESTAMP,
  price_cents      INTEGER,
  transaction_id   INTEGER NOT NULL,
  CONSTRAINT id_duck PRIMARY KEY (id)
);

ALTER SEQUENCE public.duck_id_seq OWNED BY public.duck.id;

CREATE SEQUENCE public.login_history_id_seq;

CREATE TABLE public.login_history (
  id          INTEGER NOT NULL DEFAULT nextval('public.login_history_id_seq'),
  ip_addr     VARCHAR(45),
  username    VARCHAR(60),
  login_time  TIMESTAMP,
  user_id     BIGINT,
  is_login_successful BOOLEAN,
  CONSTRAINT id_login_history PRIMARY KEY (id)
);


ALTER SEQUENCE public.login_history_id_seq OWNED BY public.login_history.id;

ALTER TABLE public.duck
  ADD CONSTRAINT transaction_duck_fk
FOREIGN KEY (transaction_id)
REFERENCES public.transaction (id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

ALTER TABLE public.duck
  ADD CONSTRAINT race_duck_fk
FOREIGN KEY (race_id)
REFERENCES public.race (id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

ALTER TABLE public.duck
  ADD CONSTRAINT buyer_duck_fk
FOREIGN KEY (buyer_id)
REFERENCES public.duck_buyer (id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

ALTER TABLE public.duck
  ADD CONSTRAINT owner_duck_fk
FOREIGN KEY (owner_id)
REFERENCES public.duck_owner (id)
ON DELETE CASCADE
ON UPDATE CASCADE
NOT DEFERRABLE;

CREATE OR REPLACE FUNCTION prevent_overlapping_races_fun () RETURNS OPAQUE AS '
DECLARE
    myrec RECORD;
BEGIN

    IF TG_OP = ''INSERT'' THEN
        SELECT * INTO myrec FROM race WHERE
            beginning < NEW.finish AND
            finish > NEW.beginning;
        IF FOUND THEN
            RAISE EXCEPTION ''INSERT failed:
            intersection with record % at (%,%)'',
            myrec.id, myrec.beginning, myrec.finish;
        END IF;
    END IF;

IF TG_OP = ''UPDATE'' THEN
SELECT * INTO myrec FROM race WHERE
  beginning < NEW.finish AND
  finish > NEW.beginning AND
  id <> OLD.id;
IF FOUND THEN
RAISE EXCEPTION ''UPDATE failed:
intersection with record % at (%,%)'',
                                   myrec.id, myrec.beginning, myrec.finish;
END IF;
END IF;
RETURN NEW;
END;
' LANGUAGE 'plpgsql';

CREATE TRIGGER add_race_tri BEFORE INSERT OR UPDATE ON race
FOR EACH ROW EXECUTE PROCEDURE prevent_overlapping_races_fun();



CREATE FUNCTION delete_past_races() RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
  DELETE FROM race WHERE finish < NOW();
  RETURN NEW;
END;
$$;

CREATE TRIGGER delete_past_races_trigger
AFTER INSERT OR UPDATE ON race
EXECUTE PROCEDURE delete_past_races();
