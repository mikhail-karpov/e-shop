CREATE TABLE category(
    id BIGSERIAL NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    parent_id BIGINT REFERENCES category(id)
);

CREATE TABLE product(
    code VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    quantity INT NOT NULL,
    category_fk BIGINT REFERENCES category(id)
);