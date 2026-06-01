INSERT INTO orders (order_status, total_price) VALUES
                                                   ('PENDING', 100.50),
                                                   ('CONFIRMED', 200.75),
                                                   ('PENDING', 150.25),
                                                   ('CANCELLED', 120.00),
                                                   ('CONFIRMED', 350.75),
                                                   ('PENDING', 180.20),
                                                   ('CANCELLED', 250.40),
                                                   ('CONFIRMED', 410.90),
                                                   ('PENDING', 95.50),
                                                   ('CONFIRMED', 520.00);

INSERT INTO order_item (product_id, quantity, order_id) VALUES
                                                            (1, 2, 1),
                                                            (2, 1, 1),

                                                            (3, 1, 2),
                                                            (4, 3, 2),

                                                            (5, 2, 3),

                                                            (6, 1, 4),
                                                            (7, 2, 4),

                                                            (8, 1, 5),
                                                            (9, 4, 5),

                                                            (10, 2, 6),

                                                            (11, 1, 7),

                                                            (12, 2, 8),
                                                            (13, 1, 8),

                                                            (14, 3, 9),

                                                            (15, 2, 10),
                                                            (16, 1, 10);