DROP ALL OBJECTS;

CREATE TABLE sys_param (
                key VARCHAR(50) NOT NULL,
                value VARCHAR(2000),
                CONSTRAINT sys_param_pk PRIMARY KEY (key)
);


CREATE TABLE admin (
                username VARCHAR(256) NOT NULL,
                CONSTRAINT admin_pk PRIMARY KEY (username)
);


CREATE TABLE login_attempt (
                id IDENTITY NOT NULL,
                ip_addr VARCHAR(45),
                username VARCHAR(256),
                time TIMESTAMP,
                successful BOOLEAN,
                CONSTRAINT login_attempt_pk PRIMARY KEY (id)
);


CREATE TABLE transaction (
                id IDENTITY NOT NULL,
                is_paid BOOLEAN NOT NULL,
                time_of_payment TIMESTAMP,
                ip_addr VARCHAR(45),
                init_time TIMESTAMP,
                email_sent BOOLEAN,
                sms_sent BOOLEAN,
                inserter VARCHAR(100),
                bank VARCHAR(20),
                CONSTRAINT id_transaction PRIMARY KEY (id)
);


CREATE TABLE race (
                id IDENTITY NOT NULL,
                beginning DATE NOT NULL,
                finish DATE,
                is_open BOOLEAN,
                race_name VARCHAR(50),
                CONSTRAINT id PRIMARY KEY (id)
);


CREATE TABLE duck_buyer (
                id IDENTITY NOT NULL,
                email VARCHAR(256),
                phone_number VARCHAR(50),
                identification_code VARCHAR(11),
                CONSTRAINT id_buyer PRIMARY KEY (id)
);


CREATE TABLE duck_owner (
                id IDENTITY NOT NULL,
                first_name VARCHAR(100),
                last_name VARCHAR(100),
                phone_number VARCHAR(50),
                CONSTRAINT id_owner PRIMARY KEY (id)
);


CREATE TABLE duck (
                id IDENTITY NOT NULL,
                date_of_purchase DATE,
                owner_id INTEGER NOT NULL,
                buyer_id INTEGER NOT NULL,
                race_id INTEGER NOT NULL,
                serial_number INTEGER,
                time_of_purchase TIMESTAMP,
                price_cents INTEGER,
                transaction_id INTEGER NOT NULL,
                CONSTRAINT id_duck PRIMARY KEY (id)
);


ALTER TABLE duck ADD CONSTRAINT transaction_duck_fk
FOREIGN KEY (transaction_id)
REFERENCES transaction (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE duck ADD CONSTRAINT race_duck_fk
FOREIGN KEY (race_id)
REFERENCES race (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE duck ADD CONSTRAINT buyer_duck_fk
FOREIGN KEY (buyer_id)
REFERENCES duck_buyer (id)
ON DELETE CASCADE
ON UPDATE CASCADE;

ALTER TABLE duck ADD CONSTRAINT owner_duck_fk
FOREIGN KEY (owner_id)
REFERENCES duck_owner (id)
ON DELETE CASCADE
ON UPDATE CASCADE;
