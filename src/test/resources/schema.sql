CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       created_date TIMESTAMP NOT NULL
);

CREATE TABLE role (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      role_name VARCHAR(255)
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(user_id),
                            FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE client (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        IDPrefix BIGINT NOT NULL
);

CREATE TABLE casee (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255),
                       client_id BIGINT,
                       IdPrefix BIGINT,
                       FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE case_assignee (
                               user_id BIGINT NOT NULL,
                               case_id BIGINT NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(user_id),
                               FOREIGN KEY (case_id) REFERENCES casee(id)
);

CREATE TABLE todo (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      description VARCHAR(255),
                      case_id BIGINT,
                      start_date TIMESTAMP,
                      end_date TIMESTAMP,
                      priority VARCHAR(255),
                      status VARCHAR(255),
                      archived BOOLEAN,
                      FOREIGN KEY (case_id) REFERENCES casee(id)
);

CREATE TABLE todo_assignee (
                               user_id BIGINT NOT NULL,
                               todo_id BIGINT NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(user_id),
                               FOREIGN KEY (todo_id) REFERENCES todo(id)
);

CREATE TABLE log (
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     actor VARCHAR(255),
                     action VARCHAR(50),
                     details VARCHAR(255),
                     timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
