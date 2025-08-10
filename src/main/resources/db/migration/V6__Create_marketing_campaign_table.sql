CREATE TABLE marketing_campaign(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    subscribed BOOLEAN,
    category VARCHAR(40),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);