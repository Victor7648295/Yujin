-- Создание таблицы статусов модерации
CREATE TABLE IF NOT EXISTS product_statuses (
                                                id BIGSERIAL PRIMARY KEY,
                                                name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO product_statuses (id, name) VALUES (1, 'PENDING') ON CONFLICT (name) DO NOTHING;
INSERT INTO product_statuses (id, name) VALUES (2, 'APPROVED') ON CONFLICT (name) DO NOTHING;
INSERT INTO product_statuses (id, name) VALUES (3, 'REJECTED') ON CONFLICT (name) DO NOTHING;

-- Создание таблицы products с внешним ключом на статусы
CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        title VARCHAR(200) NOT NULL,
                                        price INTEGER NOT NULL,
                                        region VARCHAR(100) NOT NULL,
                                        category VARCHAR(100) NOT NULL,
                                        condition VARCHAR(50) NOT NULL,
                                        image_path VARCHAR(500),
                                        phone VARCHAR(20) NOT NULL,
                                        description VARCHAR(1000),
                                        seller_name VARCHAR(100),
                                        status_id BIGINT NOT NULL DEFAULT 1,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        user_id BIGINT NOT NULL,
                                        CONSTRAINT fk_products_status FOREIGN KEY (status_id) REFERENCES product_statuses(id)
);

-- Создание индексов для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_products_title ON products(title);
CREATE INDEX IF NOT EXISTS idx_products_region ON products(region);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_condition ON products(condition);
CREATE INDEX IF NOT EXISTS idx_products_status ON products(status_id);


CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_name ON categories(name);

-- Создание таблицы users
CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Создание индекса для email
CREATE INDEX idx_users_email ON users(email);