CREATE TABLE role (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      role_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_date DATE NOT NULL
);

CREATE TABLE user_roles (
                            role_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            PRIMARY KEY (role_id, user_id)
);

CREATE TABLE client (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        idprefix BIGINT
);

CREATE TABLE casee (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      client_id BIGINT NOT NULL,
                      id_prefix BIGINT,
                      responsible_user_user_id BIGINT
);

CREATE TABLE to_do (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(255) NOT NULL,
                       case_id BIGINT,
                       created TIMESTAMP,
                       start_date DATE NOT NULL,
                       end_date DATE,
                       archived BOOLEAN,
                       priority VARCHAR(50),
                       status VARCHAR(50)
);

CREATE TABLE logging (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         actor VARCHAR(255) NOT NULL,
                         action VARCHAR(255) NOT NULL,
                         details VARCHAR(2000),
                         timestamp TIMESTAMP NOT NULL
);

CREATE TABLE client_assignee (
                                 user_id BIGINT NOT NULL,
                                 client_id BIGINT NOT NULL,
                                 PRIMARY KEY (user_id, client_id)
);

CREATE TABLE case_assignee (
                               case_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               PRIMARY KEY (case_id, user_id),
                               FOREIGN KEY (case_id) REFERENCES casee(id),
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE todo_assignee (
                               todo_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               PRIMARY KEY (todo_id,user_id)
);
