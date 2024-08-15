CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY_KEY,
                       jwt VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE