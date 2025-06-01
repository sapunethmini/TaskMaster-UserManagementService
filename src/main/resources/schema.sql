-- Create users table
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(255)
);

-- Create employee table (id is NOT auto-increment since it will use users.id)
CREATE TABLE IF NOT EXISTS employee (
                                        id BIGINT PRIMARY KEY,
                                        firstname VARCHAR(255) NOT NULL,
                                        lastname VARCHAR(255) NOT NULL,
                                        email VARCHAR(255) NOT NULL,
                                        department_id VARCHAR(255),
                                        role_id VARCHAR(255),
                                        department_name VARCHAR(255),
                                        FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- Drop existing triggers if they exist
DROP TRIGGER IF EXISTS auto_create_employee_after_user_insert;
DROP TRIGGER IF EXISTS auto_create_employee_after_user_update;

-- Create trigger for INSERT - when new user with ROLE_EMPLOYEE is created
-- NOTE: No DELIMITER needed for Spring Boot schema.sql
CREATE TRIGGER auto_create_employee_after_user_insert
    AFTER INSERT ON users
    FOR EACH ROW
BEGIN
    IF NEW.role = 'ROLE_EMPLOYEE' THEN
        IF NOT EXISTS (SELECT 1 FROM employee WHERE id = NEW.id) THEN
            INSERT INTO employee (
                id,
                firstname,
                lastname,
                email,
                department_id,
                role_id,
                department_name
            ) VALUES (
                         NEW.id,
                         NEW.username,
                         '',
                         CONCAT(NEW.username, '@company.com'),
                         'DEFAULT',
                         '21',
                         'General'
                     );
        END IF;
    END IF;
END;

-- Create trigger for UPDATE - when user role changes to ROLE_EMPLOYEE
CREATE TRIGGER auto_create_employee_after_user_update
    AFTER UPDATE ON users
    FOR EACH ROW
BEGIN
    IF NEW.role = 'ROLE_EMPLOYEE' AND (OLD.role != 'ROLE_EMPLOYEE' OR OLD.role IS NULL) THEN
        IF NOT EXISTS (SELECT 1 FROM employee WHERE id = NEW.id) THEN
            INSERT INTO employee (
                id,
                firstname,
                lastname,
                email,
                department_id,
                role_id,
                department_name
            ) VALUES (
                         NEW.id,
                         NEW.username,
                         '',
                         CONCAT(NEW.username, '@company.com'),
                         'DEFAULT',
                         '21',
                         'General'
                     );
        END IF;
    END IF;
END;