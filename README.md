# 🏢 Employee Management System
### Java Core + JDBC + MySQL + Spring Boot

---

## 📋 Overview

A production-structured **Employee Management System** REST API built with:

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Database Access | Spring JDBC (JdbcTemplate – **no ORM / JPA**) |
| Database | MySQL 8 |
| Connection Pool | HikariCP |
| Build Tool | Maven |
| Utilities | Lombok |

---

## 🏗️ Project Structure

```
employee-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/ems/
│   │   │   ├── EmployeeManagementApplication.java   ← Main class
│   │   │   ├── model/
│   │   │   │   ├── Employee.java                    ← Employee POJO
│   │   │   │   └── Department.java                  ← Department POJO
│   │   │   ├── dao/
│   │   │   │   ├── EmployeeDAO.java                 ← Interface
│   │   │   │   ├── EmployeeDAOImpl.java             ← JDBC implementation
│   │   │   │   ├── DepartmentDAO.java               ← Interface
│   │   │   │   └── DepartmentDAOImpl.java           ← JDBC implementation
│   │   │   ├── service/
│   │   │   │   ├── EmployeeService.java             ← Business logic
│   │   │   │   └── DepartmentService.java
│   │   │   ├── controller/
│   │   │   │   ├── EmployeeController.java          ← REST endpoints
│   │   │   │   └── DepartmentController.java
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── GlobalExceptionHandler.java      ← @RestControllerAdvice
│   │   │   └── util/
│   │   │       └── ApiResponse.java                 ← Generic response wrapper
│   │   └── resources/
│   │       ├── application.properties               ← DB config, HikariCP
│   │       └── schema.sql                           ← Auto-run on startup
│   └── test/
│       └── java/com/ems/
│           └── EmployeeServiceTest.java             ← Unit tests
└── pom.xml
```

---

## ⚙️ Setup & Run

### 1. Create MySQL Database

```sql
CREATE DATABASE employee_db;
CREATE USER 'ems_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON employee_db.* TO 'ems_user'@'localhost';
```

### 2. Update `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run the JAR
java -jar target/employee-management-system-1.0.0.jar
```

The app starts on **http://localhost:8080** and auto-creates tables + seeds demo data.

---

## 🔌 REST API Endpoints

### 👤 Employees – `/api/employees`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/employees` | Create new employee |
| `GET` | `/api/employees` | Get all employees |
| `GET` | `/api/employees?page=1&size=5` | Paginated list |
| `GET` | `/api/employees/{id}` | Get by ID |
| `GET` | `/api/employees/email/{email}` | Get by email |
| `GET` | `/api/employees/search?q=amit` | Search by name |
| `GET` | `/api/employees/status/ACTIVE` | Filter by status |
| `GET` | `/api/employees/department/{id}` | Filter by department |
| `GET` | `/api/employees/count` | Total count |
| `PUT` | `/api/employees/{id}` | Full update |
| `PATCH` | `/api/employees/{id}/status?status=INACTIVE` | Update status |
| `DELETE` | `/api/employees/{id}` | Delete employee |

### 🏢 Departments – `/api/departments`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/departments` | Create department |
| `GET` | `/api/departments` | Get all departments |
| `GET` | `/api/departments/{id}` | Get by ID |
| `PUT` | `/api/departments/{id}` | Update department |
| `DELETE` | `/api/departments/{id}` | Delete department |

---

## 📡 Sample API Calls (curl)

```bash
# Create Employee
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ravi",
    "lastName":  "Teja",
    "email":     "ravi.teja@ems.com",
    "phone":     "9876543220",
    "salary":    70000,
    "designation": "Software Engineer",
    "departmentId": 1,
    "hireDate":  "2024-01-01",
    "status":    "ACTIVE"
  }'

# Get all employees
curl http://localhost:8080/api/employees

# Paginated
curl "http://localhost:8080/api/employees?page=1&size=3"

# Search
curl "http://localhost:8080/api/employees/search?q=amit"

# Update status
curl -X PATCH "http://localhost:8080/api/employees/1/status?status=ON_LEAVE"

# Delete
curl -X DELETE http://localhost:8080/api/employees/10
```

---

## 📊 Database Schema

```
departments
  id (PK) | name | location | created_at

employees
  id (PK) | first_name | last_name | email | phone |
  salary  | designation | department_id (FK) | hire_date |
  status (ACTIVE | INACTIVE | ON_LEAVE) | created_at | updated_at
```

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 💡 Key Design Decisions

- **No JPA/Hibernate** – All SQL written explicitly via `JdbcTemplate`
- **RowMapper** – Manual mapping from `ResultSet` → POJO for full control
- **Transaction Management** – `@Transactional` at service layer
- **HikariCP** – Default high-performance connection pool
- **DAO Pattern** – Interface + Impl for clean separation and testability
- **Global Exception Handler** – `@RestControllerAdvice` for uniform error JSON
- **Layered Architecture** – Controller → Service → DAO → MySQL
