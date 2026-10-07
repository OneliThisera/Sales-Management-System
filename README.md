# Apex PC & Tech — Sales & Inventory Management System
### Enterprise Application Development — Coursework Project

A full-featured desktop Sales and Point of Sale (POS) Management System built with **Java Swing**, **MySQL**, and **JasperReports**.

---

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 11 or higher |
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

## Viva Presentation Highlights

- **Embedded Single-Window Navigation**: Smooth in-frame view switching without tab duplication or visual ghosting.
- **POS Quick Checkout**: Live subtotal, tax calculation, payment method selection, cash received, and change due calculator.
- **JasperReports Engine Integration**: Full analytical reporting suite with 5 interactive reports + 1 POS invoice printable receipt:
  1. **Sales Performance & Details Report** (`sales_report.jrxml`): Filterable date-range revenue analytics with customer, employee, and item breakdowns.
  2. **Inventory Stock & Valuation Report** (`inventory_report.jrxml`): Real-time stock counts, product categories, and cumulative stock asset valuation.
  3. **Top Customers & Spend Analysis Report** (`top_customers_report.jrxml`): VIP client rankings, order frequencies, and lifetime value calculations.
  4. **Staff / Cashier Sales Performance Report** (`staff_sales_report.jrxml`): Employee order processing volume and total revenue generated.
  5. **Daily Revenue Breakdown Report** (`daily_sales_report.jrxml`): Date-by-date sales summary with daily order totals and average transaction values.
  6. **Customer Order Invoice / Receipt** (`invoice_report.jrxml`): Formal receipt generated automatically on POS checkout.
- **Dual Presentation**: Every report features both an embedded high-resolution **JasperViewer** with PDF Export / Pop-out, alongside an editable **Tabular Data Grid**.
- **Custom Java 2D Analytics**: Built-in 7-day revenue trend bar chart rendered on the executive dashboard.
- **Real-time Live Filter Search**: Instant search on Customer and Product inventories.

---

*Enterprise Application Development — Sales Management System*
