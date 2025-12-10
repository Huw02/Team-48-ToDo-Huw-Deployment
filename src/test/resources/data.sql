INSERT INTO role (role_name)
VALUES ('ADMIN'),
       ('PARTNER'),
       ('SAGSBEHANDLER'),
       ('JURIST');

INSERT INTO users (username, name, email, password, created_date)
VALUES ('admin', 'System Admin', 'admin@example.com',
        '$2b$10$EeZg8T58OWK0avOXdKTUd.6Y..m1RRZgzJdOLq1XJUhWs8cXmBtDm', '2024-01-01'),
       ('partner01', 'Partner One', 'partner01@example.com',
        '$2b$10$sNAEc88AUC2MP4q7DeT2eOXsobi7enuU1KgEmO8KyIlC30dhPIm.K', '2024-01-05'),
       ('worker01', 'Case Worker', 'worker01@example.com',
        '$2b$10$.6TqdMZDbXmx.Y/wy9ZYf.9IShXFr8u3vk7LoIJiINrApanl0Bavu', '2024-01-10'),
       ('jurist01', 'Legal Jurist', 'jurist01@example.com',
        '$2b$10$EeZg8T58OWK0avOXdKTUd.6Y..m1RRZgzJdOLq1XJUhWs8cXmBtDm', '2024-01-15');

INSERT INTO user_roles (role_id, user_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4);

INSERT INTO client (name, idprefix)
VALUES ('Kromann Reumert', 1000),
       ('AlphaSolution', 2000);

INSERT INTO casee (name, client_id, id_prefix, responsible_user_user_id, created)
VALUES ('Contract Review', 1, 1100, 1, '2025-12-09'),
       ('System Rollout', 2, 2200, 1, '2025-12-09' );

INSERT INTO to_do ( name, description, case_id, created, start_date, end_date, archived, priority, status) VALUES
        ('NDA', 'Draft NDA', 1,'2024-01-15 10:00:00', '2024-02-01', '2024-02-05', FALSE, 'HIGH', 'NOT_STARTED'),
        ('Implementation plan','Prepare implementation plan', 2, '2024-01-20 11:00:00', '2024-02-10', '2024-02-15', FALSE, 'MEDIUM', 'IN_PROGRESS'),
        ('Old task - archived','This task was completed earlier', 1, '2023-12-01 09:00:00', '2023-12-05', '2023-12-07', TRUE, 'LOW', 'DONE');

INSERT INTO logging (actor, action, details, timestamp)
VALUES ('admin', 'CREATE_CLIENT', 'Created client Kromann Reumert', '2024-02-01 09:00:00'),
       ('partner01', 'CREATE_CASE', 'Created Contract Review for client 1', '2024-02-01 10:00:00'),
       ('worker01', 'ASSIGN_TODO', 'Assigned ToDo 1 to worker01', '2024-02-02 11:00:00');

INSERT INTO client_assignee (user_id, client_id)
VALUES (2, 1),
       (2, 2);

INSERT INTO case_assignee (case_id, user_id)
VALUES (1, 2),
       (1, 3),
       (1, 4);

INSERT INTO todo_assignee (todo_id, user_id)
VALUES (1, 3),
       (2, 2),
       (1,4);
