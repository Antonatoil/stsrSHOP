-- V3__seed_demo_data.sql
-- Демо-пользователи и тестовая категория для учебного проекта "Shop"

-- 1. Администратор: admin@example.com / admin123
INSERT INTO users (email, password, full_name, role)
VALUES (
    'admin@example.com',
    '$2b$10$KmPA.wF0yTfsi2S2rARFKOdKur3nk3coN4OYcdduPW1ug6oEFMiT2', -- bcrypt("admin123")
    'Admin',
    'ROLE_ADMIN'
)
ON CONFLICT (email) DO NOTHING;

-- 2. Обычный пользователь: user@example.com / user123
INSERT INTO users (email, password, full_name, role)
VALUES (
    'user@example.com',
    '$2b$10$mvIABUBezFgdjojaxzald.KIsjTBYE9Hlf2jQnpmas/PzeHngqAz6', -- bcrypt("user123")
    'Test User',
    'ROLE_USER'
)
ON CONFLICT (email) DO NOTHING;

-- 3. Тестовая категория "Smartphones"
INSERT INTO categories (name, description)
VALUES ('Smartphones', 'Смартфоны и гаджеты')
ON CONFLICT (name) DO NOTHING;
