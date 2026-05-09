-- Создание таблицы статусов модерации
CREATE TABLE IF NOT EXISTS transfer_statuses (
                                                id BIGSERIAL PRIMARY KEY,
                                                name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO transfer_statuses (id, name) VALUES (1, 'PENDING') ON CONFLICT (name) DO NOTHING;
INSERT INTO transfer_statuses (id, name) VALUES (2, 'APPROVED') ON CONFLICT (name) DO NOTHING;
INSERT INTO transfer_statuses (id, name) VALUES (3, 'REJECTED') ON CONFLICT (name) DO NOTHING;

-- Создание таблицы состояний товара (Новое / Б/у)
CREATE TABLE IF NOT EXISTS transfer_conditions (
                                                  id   BIGSERIAL PRIMARY KEY,
                                                  name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO transfer_conditions (id, name) VALUES (1, 'Легионер') ON CONFLICT (name) DO NOTHING;
INSERT INTO transfer_conditions (id, name) VALUES (2, 'Местный')  ON CONFLICT (name) DO NOTHING;

-- Создание таблицы products с внешними ключами на статусы и состояния
CREATE TABLE IF NOT EXISTS transfers (
                                        id BIGSERIAL PRIMARY KEY,
                                        title VARCHAR(200) NOT NULL,
                                        price INTEGER NOT NULL,
                                        region VARCHAR(100) NOT NULL,
                                        category VARCHAR(100) NOT NULL,
                                        condition_id BIGINT NOT NULL,
                                        image_path VARCHAR(500),
                                        phone VARCHAR(20) NOT NULL,
                                        description VARCHAR(1000),
                                        seller_name VARCHAR(100),
                                        status_id BIGINT NOT NULL DEFAULT 1,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        user_id BIGINT NOT NULL,
                                        CONSTRAINT fk_transfers_status FOREIGN KEY (status_id) REFERENCES transfer_statuses (id),
                                        CONSTRAINT fk_transfers_condition FOREIGN KEY (condition_id) REFERENCES transfer_conditions (id)
);

-- Создание индексов для ускорения поиска
CREATE INDEX IF NOT EXISTS idx_transfers_title ON transfers(title);
CREATE INDEX IF NOT EXISTS idx_transfers_region ON transfers(region);
CREATE INDEX IF NOT EXISTS idx_transfers_category ON transfers(category);
CREATE INDEX IF NOT EXISTS idx_transfers_price ON transfers(price);
CREATE INDEX IF NOT EXISTS idx_transfers_condition ON transfers(condition_id);
CREATE INDEX IF NOT EXISTS idx_transfers_status ON transfers(status_id);


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