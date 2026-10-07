package com.sales.dao;

import com.sales.model.Employee;
import com.sales.util.DBConnection;

import java.sql.*;

public class EmployeeDAO {

    private Connection getConn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    public Employee authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM employee WHERE username=? AND password=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee e = new Employee(
                    rs.getInt("employee_id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("username")
                );
                return e;
            }
        }
        return null;
    }

    public boolean addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO employee (name, role, username, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getRole());
            ps.setString(3, e.getUsername());
            ps.setString(4, e.getPassword());
            return ps.executeUpdate() > 0;
        }
    }
}
