# Apex PC & Tech — Sales & Inventory Management System
### Enterprise Application Development — Coursework Project

A full-featured desktop Sales and Point of Sale (POS) Management System built with **Java Swing**, **MySQL**, and **JasperReports**.

---

## Prerequisites


| Java JDK |
| MySQL Server |
| NetBeans IDE | 
| JasperReports | 

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
