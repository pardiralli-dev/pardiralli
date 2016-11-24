DROP SCHEMA IF EXISTS public CASCADE;
CREATE SCHEMA public;

CREATE SEQUENCE public.transaction_id_seq;

CREATE TABLE public.transaction (
  id              INTEGER NOT NULL DEFAULT nextval('public.transaction_id_seq'),
  is_paid         BOOLEAN NOT NULL,
  time_of_payment TIMESTAMP,
  CONSTRAINT id_transaction PRIMARY KEY (id)
);


ALTER SEQUENCE public.transaction_id_seq OWNED BY public.transaction.id;

CREATE SEQUENCE public.race_id_seq;

CREATE TABLE public.race (
  id          INTEGER NOT NULL DEFAULT nextval('public.race_id_seq'),
  beginning   DATE    NOT NULL,
  finish      DATE,
  is_open     BOOLEAN,
  race_name   VARCHAR(50),
  description VARCHAR(2000),
  CONSTRAINT id PRIMARY KEY (id)
);


ALTER SEQUENCE public.race_id_seq OWNED BY public.race.id;

CREATE SEQUENCE public.duck_buyer_id_seq;

CREATE TABLE public.duck_buyer (
  id           INTEGER NOT NULL DEFAULT nextval('public.duck_buyer_id_seq'),
  email        VARCHAR(256),
  phone_number VARCHAR(50),
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

CREATE OR REPLACE FUNCTION fun_add_duck(dateof     DATE, ownerfname VARCHAR, ownerlname VARCHAR,
                                        ownerphone VARCHAR, buyeremail VARCHAR,
                                        buyerphone VARCHAR, raceid INT, timeof TIMESTAMP, price INT,
                                        transid    INT)
  RETURNS INT AS $counter$
DECLARE   counter INT;
  DECLARE max     INT;
  DECLARE ownerid INT;
  DECLARE buyerid INT;

BEGIN
  counter := 0;
  max := (SELECT max(serial_number)
          FROM duck
          WHERE race_id = raceid);

  WHILE counter <= max LOOP
    counter := counter + 1;
    EXIT WHEN (NOT EXISTS(SELECT serial_number
                          FROM duck
                          WHERE serial_number = counter AND race_id = raceid));
  END LOOP;

  INSERT INTO duck_owner (first_name, last_name, phone_number) VALUES (ownerfname, ownerlname, ownerphone)
  RETURNING id
    INTO ownerid;
  INSERT INTO duck_buyer (email, phone_number) VALUES (buyeremail, buyerphone)
  RETURNING id
    INTO
      buyerid;

  INSERT INTO duck (date_of_purchase, owner_id, buyer_id, race_id, serial_number, time_of_purchase,
                    price_cents, transaction_id)
  VALUES (dateof, ownerid, buyerid, raceid, counter, timeof, price, transid);
  RETURN counter;
END;
$counter$ LANGUAGE plpgsql;

