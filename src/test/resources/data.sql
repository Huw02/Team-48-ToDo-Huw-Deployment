INSERT INTO role (id, role_name) VALUES
                                     (1, 'ADMIN'),
                                     (2, 'PARTNER'),
                                     (3, 'SAGSBEHANDLER'),
                                     (4, 'JURIST');


INSERT INTO users (user_id, username, name, email, password, created_date) VALUES
                                                                               (1, 'jdoe', 'John Doe', 'john@example.com', 'password', CURRENT_TIMESTAMP),
                                                                               (2, 'asmith', 'Alice Smith', 'alice@example.com', 'password', CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role_id) VALUES
                                              (1, 1),
                                              (2, 2);

INSERT INTO client (id, name, IDPrefix) VALUES
                                            (1, 'Client A', 99000),
                                            (2, 'Client B', 99001);

INSERT INTO casee (id, name, client_id, IdPrefix) VALUES
                                                      (1, 'Case X', 1, 99100),
                                                      (2, 'Case Y', 1, 99101),
                                                      (3, 'Case Z', 2, 99102);

INSERT INTO case_assignee (user_id, case_id) VALUES
                                                 (1, 1),
                                                 (1, 2),
                                                 (2, 3);

INSERT INTO todo (id, description, case_id, start_date, end_date, priority, status, archived)
VALUES
    (1, 'Draft legal document', 1, CURRENT_TIMESTAMP, NULL, 'HIGH', 'OPEN', FALSE),
    (2, 'Review contract', 2, CURRENT_TIMESTAMP, NULL, 'MEDIUM', 'IN_PROGRESS', FALSE);

INSERT INTO todo_assignee (user_id, todo_id) VALUES
                                                 (1, 1),
                                                 (2, 2);

INSERT INTO log (id, actor, action, details, timestamp)
VALUES
    (1, 'SYSTEM', 'CREATED_USER', 'User John Doe added to system', CURRENT_TIMESTAMP),
    (2, 'SYSTEM', 'CREATED_CASE', 'Case X created for Client A', CURRENT_TIMESTAMP);
