CREATE TABLE blogs(
    id SERIAL PRIMARY KEY,
    image VARCHAR (250),
    name VARCHAR (50),
    created_at TIMESTAMP NOT NULL DEFAULT now()
)