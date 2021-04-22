CREATE TABLE orders (
    id uuid PRIMARY KEY NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE address (
    order_id uuid PRIMARY KEY NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    zip VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL
);

CREATE TABLE order_item (
    id uuid PRIMARY KEY NOT NULL,
    order_fk uuid NOT NULL REFERENCES orders(id),
    code VARCHAR(255) NOT NULL,
    quantity INT NOT NULL
);