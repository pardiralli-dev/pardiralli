INSERT INTO duck_owner (first_name, last_name, phone_number) VALUES ('Volde', 'Tort', '666666');
INSERT INTO duck_owner (first_name, last_name, phone_number) VALUES ('Marilyn', 'Monroe', '23123123');
INSERT INTO duck_buyer (email, pi_code) VALUES ('garripotter@gogwarts.ru', '41111111111');
INSERT INTO duck_buyer (email, pi_code) VALUES ('jetfuel@cantmelt.stealbeams', '51212121212');
INSERT INTO race (beginning, finish, is_open, race_name, description)
VALUES ('2016-09-28T11:18:06+00:00', '2016-09-29T10:55:08+00:00', FALSE, 'Nimi1', 'Kirjeldus1');
INSERT INTO race (beginning, finish, is_open, race_name, description)
VALUES ('2015-10-29T10:33:06+00:00', '2015-12-01T12:44:08+00:00', TRUE, 'Nimi2', 'Kirjeldus2');
INSERT INTO transaction (is_paid, time_of_payment, ip_addr, init_time) VALUES (TRUE, TIMESTAMP '2009-04-04 09:01:32', '234.345.341.234', TIMESTAMP '2009-04-04 09:10:05');
INSERT INTO transaction (is_paid, time_of_payment, ip_addr, init_time) VALUES (FALSE, TIMESTAMP '2011-07-05 12:34:22', '567:e345:1x445:423', TIMESTAMP '2011-07-05 12:40:44');
INSERT INTO duck (date_of_purchase, owner_id, buyer_id, race_id, serial_number, time_of_purchase, price_cents, transaction_id)
VALUES (TIMESTAMP '2011-05-16 15:36:38', 1, 1, 1, 1, TIMESTAMP '2008-06-11 03:38:38', 500, 1);
INSERT INTO duck (date_of_purchase, owner_id, buyer_id, race_id, serial_number, time_of_purchase, price_cents, transaction_id)
VALUES (TIMESTAMP '2012-06-10 15:44:10', 1, 1, 1, 2, TIMESTAMP '2011-05-16 15:36:39', 2500, 1);
INSERT INTO duck (date_of_purchase, owner_id, buyer_id, race_id, serial_number, time_of_purchase, price_cents, transaction_id)
VALUES (TIMESTAMP '2016-06-10 02:50:00', 2, 2, 2, 3, TIMESTAMP '2016-04-07 12:32:40', 10000, 2);

