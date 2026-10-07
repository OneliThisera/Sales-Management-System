package com.sales.dao;

import com.sales.model.Payment;
import com.sales.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    private Connection getConn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    public boolean addPayment(Payment p) throws SQLException {
        String sql = "INSERT INTO payment (order_id, amount, method) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, p.getOrderId());
            ps.setDouble(2, p.getAmount());
            ps.setString(3, p.getMethod());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Payment> getPaymentsByOrder(int orderId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE order_id=? ORDER BY payment_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment p = new Payment(rs.getInt("order_id"), rs.getDouble("amount"), rs.getString("method"));
                p.setPaymentId(rs.getInt("payment_id"));
                p.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
                list.add(p);
            }
        }
        return list;
    }

    public double getTotalPaymentsToday() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payment WHERE DATE(payment_date) = CURDATE()";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }
}
