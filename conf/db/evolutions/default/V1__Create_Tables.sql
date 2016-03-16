CREATE TABLE address (
                address_id INT AUTO_INCREMENT NOT NULL,
                street VARCHAR(100) NOT NULL,
                PRIMARY KEY (address_id)
);

CREATE TABLE person (
                person_id INT AUTO_INCREMENT NOT NULL,
                name VARCHAR(50) NOT NULL,
                PRIMARY KEY (person_id)
);

CREATE TABLE phone (
                phone_id INT AUTO_INCREMENT NOT NULL,
                person_id INT NOT NULL,
                number VARCHAR(50) NOT NULL,
                PRIMARY KEY (phone_id, person_id)
);

CREATE TABLE person_address (
                person_id INT NOT NULL,
                address_id INT NOT NULL,
                PRIMARY KEY (person_id, address_id)
);

ALTER TABLE person_address ADD CONSTRAINT address_person_address_fk
FOREIGN KEY (address_id)
REFERENCES address (address_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE person_address ADD CONSTRAINT person_person_address_fk
FOREIGN KEY (person_id)
REFERENCES person (person_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;

ALTER TABLE phone ADD CONSTRAINT person_phone_fk
FOREIGN KEY (person_id)
REFERENCES person (person_id)
ON DELETE NO ACTION
ON UPDATE NO ACTION;