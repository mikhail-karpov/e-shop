INSERT INTO category(id, title, description, parent_id) VALUES
    (1, 'Electronics', 'Electronics & Computers', NULL),
    (2, 'Books', 'Books', NULL),
    (3, 'Computers', 'Computers and peripherals', 1),
    (4, 'Camera & Photo', 'Camera, photo, accessories and other equipment', 1);

SELECT setval('category_id_seq', 5);

INSERT INTO product(code, title, description, price, quantity, category_fk) VALUES
    ('macbook', 'MacBook', 'MacBook Pro 2020', 200000, 3, 3),
    ('dell', 'DELL UP3221Q UltraSharp', '31.5 inch monitor', 350000, 2, 3),
    ('fuji', 'Fujifilm XT-1', 'Fuju camera', 190000, 7, NULL),
    ('canon', 'Canon PIXMA', 'Canon printer', 45000, 2, NULL);