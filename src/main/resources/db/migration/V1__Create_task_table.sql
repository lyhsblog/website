-- Create sequence for task_id with increment 50 to match Hibernate's default
CREATE SEQUENCE task_seq START WITH 1 INCREMENT BY 50;

-- Create task table
CREATE TABLE task (
    task_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    due_date DATE
);

-- Create index on creation_date for better performance
CREATE INDEX idx_task_creation_date ON task(creation_date);

-- Create index on due_date for better performance
CREATE INDEX idx_task_due_date ON task(due_date);
