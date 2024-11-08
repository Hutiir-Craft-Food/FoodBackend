CREATE TABLE marketing_campaign(
    id SERIAL PRIMARY KEY,
    subscribed BOOLEAN,
    category VARCHAR(40),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);