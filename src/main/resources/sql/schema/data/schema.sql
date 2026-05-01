-- Создание таблицы products

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        title VARCHAR(200) NOT NULL,
    price INTEGER NOT NULL CHECK (price >= 0),
    region VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    condition VARCHAR(50) NOT NULL,
    image_path VARCHAR(500),
    phone VARCHAR(20) NOT NULL,
    description VARCHAR(1000),
    seller_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Создание индексов для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_products_title ON products(title);
CREATE INDEX IF NOT EXISTS idx_products_region ON products(region);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_products_condition ON products(condition);