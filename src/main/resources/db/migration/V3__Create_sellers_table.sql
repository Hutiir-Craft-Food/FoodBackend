CREATE TABLE sellers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    seller_name VARCHAR(100),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    creation_date TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_seller_name ON sellers(seller_name);