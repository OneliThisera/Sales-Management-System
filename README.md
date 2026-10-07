# Apex PC & Tech — Sales & Inventory Management System
### Enterprise Application Development — Coursework Project

A full-featured desktop Sales and Point of Sale (POS) Management System built with **Java Swing**, **MySQL**, and **JasperReports**.

---

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK |
| MySQL Server | 8.0+ |
| NetBeans IDE / IntelliJ IDEA | Any modern Java IDE |
| Apache Maven | 3.6+ |

---

## Setup Instructions

### Step 1 — Database Configuration

1. Open MySQL Workbench or any MySQL client
2. Execute `db/setup.sql` (or `db/seed_apex_data.sql` to populate high-performance hardware inventory and sample sales data)

### Step 2 — Verify Database Connection Settings

Open `src/main/java/com/sales/util/DBConnection.java` to verify credentials:
Default configuration:
- Host: `localhost:3306`
- Database: `sales_db`
- Username: `root`
- Password: `root123`

### Step 3 — Build and Run

Run using Maven or directly from your IDE:
```bash
mvn clean compile
```

Run application:
```bash
mvn exec:java -Dexec.mainClass="com.sales.Main"
```

Or build executable JAR:
```bash
mvn clean package
java -jar target/SalesManagementSystem.jar
```

---

## User Accounts & Role Permissions

| Username | Password | Role | Permissions Summary |
|---|---|---|---|
| `admin` | `admin123` | Admin | Full unrestricted access across all modules, inventory management, reports, and database seeder |
| `cashier` | `cashier123` | Cashier | Streamlined POS checkout, payment collection, customer registration, catalog search; restricted from delete actions and executive reports |

---

## Core System Architecture & Design Patterns

| Pattern | Implementation |
|---|---|
| **Singleton Pattern** | `DBConnection.java` ensures a single shared database connection pool |
| **DAO Pattern** | `dao/` package separates database queries from business rules |
| **MVC Pattern** | Structured separation between `model/`, `dao/`, `controller/`, and `view/` |
| **Role-Based Access Control (RBAC)** | Dynamic UI permission checks per user role |
| **Vector Icon Rendering** | `IconFactory.java` provides resolution-independent Java 2D vector icons |

---



*Enterprise Application Development — Sales Management System*
