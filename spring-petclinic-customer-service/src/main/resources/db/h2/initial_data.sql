SET SCHEMA CUSTOMERS_SCHEMA;

INSERT INTO PET_TYPE VALUES (1, 'cat');
INSERT INTO PET_TYPE VALUES (2, 'dog');
INSERT INTO PET_TYPE VALUES (3, 'lizard');
INSERT INTO PET_TYPE VALUES (4, 'snake');
INSERT INTO PET_TYPE VALUES (5, 'bird');
INSERT INTO PET_TYPE VALUES (6, 'hamster');

UPDATE APP_SEQ_GENERATOR
SET SEQ_VALUE = 7
WHERE SEQ_NAME = 'PET_TYPE_SEQ_PK';

INSERT INTO owner VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT INTO owner VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT INTO owner VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT INTO owner VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT INTO owner VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT INTO owner VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT INTO owner VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT INTO owner VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT INTO owner VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT INTO owner VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

UPDATE APP_SEQ_GENERATOR
SET SEQ_VALUE = 11
WHERE SEQ_NAME = 'OWNER_SEQ_PK';

INSERT INTO pet VALUES (1, 'Leo', '2010-09-07', 1, 1);
INSERT INTO pet VALUES (2, 'Basil', '2012-08-06', 6, 2);
INSERT INTO pet VALUES (3, 'Rosy', '2011-04-17', 2, 3);
INSERT INTO pet VALUES (4, 'Jewel', '2010-03-07', 2, 3);
INSERT INTO pet VALUES (5, 'Iggy', '2010-11-30', 3, 4);
INSERT INTO pet VALUES (6, 'George', '2010-01-20', 4, 5);
INSERT INTO pet VALUES (7, 'Samantha', '2012-09-04', 1, 6);
INSERT INTO pet VALUES (8, 'Max', '2012-09-04', 1, 6);
INSERT INTO pet VALUES (9, 'Lucky', '2011-08-06', 5, 7);
INSERT INTO pet VALUES (10, 'Mulligan', '2007-02-24', 2, 8);
INSERT INTO pet VALUES (11, 'Freddy', '2010-03-09', 5, 9);
INSERT INTO pet VALUES (12, 'Lucky', '2010-06-24', 2, 10);
INSERT INTO pet VALUES (13, 'Sly', '2012-06-08', 1, 10);

UPDATE APP_SEQ_GENERATOR
SET SEQ_VALUE = 14
WHERE SEQ_NAME = 'PET_SEQ_PK';

commit;