-- ==========================================
-- Employee Management System - Schema
-- ==========================================

CREATE DATABASE IF NOT EXISTS employee_db;
USE employee_db;

-- Department Table
CREATE TABLE IF NOT EXISTS departments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    location    VARCHAR(150),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee Table
CREATE TABLE IF NOT EXISTS employees (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(50)  NOT NULL,
    last_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    phone           VARCHAR(20),
    salary          DECIMAL(12, 2) NOT NULL,
    designation     VARCHAR(100),
    department_id   BIGINT,
    hire_date       DATE,
    status          ENUM('ACTIVE','INACTIVE','ON_LEAVE') DEFAULT 'ACTIVE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_dept FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- ==========================================
-- Seed Data
-- ==========================================

INSERT INTO departments (name, location) VALUES
    ('Engineering',  'Bangalore')  ON DUPLICATE KEY UPDATE location = VALUES(location);
INSERT INTO departments (name, location) VALUES
    ('Human Resources', 'Mumbai')  ON DUPLICATE KEY UPDATE location = VALUES(location);
INSERT INTO departments (name, location) VALUES
    ('Finance',      'Pune')       ON DUPLICATE KEY UPDATE location = VALUES(location);
INSERT INTO departments (name, location) VALUES
    ('Marketing',    'Delhi')      ON DUPLICATE KEY UPDATE location = VALUES(location);
INSERT INTO departments (name, location) VALUES
    ('Operations',   'Hyderabad')  ON DUPLICATE KEY UPDATE location = VALUES(location);

INSERT IGNORE INTO employees (first_name, last_name, email, phone, salary, designation, department_id, hire_date, status) VALUES
    ('Amit',    'Sharma',  'amit.sharma@ems.com',   '9876543210', 75000.00, 'Senior Developer',  1, '2020-01-15', 'ACTIVE'),
    ('Priya',   'Verma',   'priya.verma@ems.com',   '9876543211', 65000.00, 'HR Manager',        2, '2019-06-01', 'ACTIVE'),
    ('Rahul',   'Gupta',   'rahul.gupta@ems.com',   '9876543212', 80000.00, 'Lead Engineer',     1, '2018-03-20', 'ACTIVE'),
    ('Sneha',   'Patel',   'sneha.patel@ems.com',   '9876543213', 55000.00, 'Accountant',        3, '2021-07-10', 'ACTIVE'),
    ('Vikram',  'Singh',   'vikram.singh@ems.com',  '9876543214', 90000.00, 'Marketing Head',    4, '2017-11-05', 'ACTIVE'),
    ('Kavita',  'Mehta',   'kavita.mehta@ems.com',  '9876543215', 45000.00, 'Junior Developer',  1, '2022-02-28', 'ACTIVE'),
    ('Arjun',   'Nair',    'arjun.nair@ems.com',    '9876543216', 70000.00, 'Operations Manager',5, '2020-09-14', 'ACTIVE'),
    ('Deepika', 'Reddy',   'deepika.reddy@ems.com', '9876543217', 60000.00, 'Finance Analyst',   3, '2021-04-01', 'ON_LEAVE'),
    ('Rohit',   'Kumar',   'rohit.kumar@ems.com',   '9876543218', 50000.00, 'Marketing Executive',4,'2023-01-10','ACTIVE'),
    ('Anjali',  'Joshi',   'anjali.joshi@ems.com',  '9876543219', 55000.00, 'HR Executive',      2, '2022-08-15', 'INACTIVE');
