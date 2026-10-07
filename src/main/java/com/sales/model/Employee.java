package com.sales.model;

public class Employee {
    private int employeeId;
    private String name;
    private String role;
    private String username;
    private String password;

    public Employee() {}

    public Employee(int employeeId, String name, String role, String username) {
        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
        this.username = username;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean isManager() {
        return "Manager".equalsIgnoreCase(role) || isAdmin();
    }

    public boolean isCashier() {
        return "Cashier".equalsIgnoreCase(role);
    }

    @Override
    public String toString() { return name + " (" + role + ")"; }
}
