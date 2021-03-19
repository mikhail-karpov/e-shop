INSERT INTO category(id, title, description, parent_id) VALUES
    (1, 'Electronics', 'Electronics & Computers', NULL),
    (2, 'Software', 'Software and games', NULL),
    (3, 'Computers', 'Computers and peripherals', 1),
    (4, 'Camera & Photo', 'Camera, photo, accessories and other equipment', 1),
    (5, 'Antivirus', 'Antivirus and security', 2),
    (6, 'Business', 'Business and office', 2);