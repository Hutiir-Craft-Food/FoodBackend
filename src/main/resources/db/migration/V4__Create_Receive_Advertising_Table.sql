CREATE TABLE receive_advertising(
    id SERIAL PRIMARY KEY,
    receiveAdvertising BOOLEAN,
    category VARCHAR(40),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);