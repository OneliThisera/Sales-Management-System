package com.sales.dao;

import com.sales.model.OrderItem;
import com.sales.model.SalesOrder;
import com.sales.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private Connection getConn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    public int createOrder(SalesOrder order) throws SQLException {
        String sql = "INSERT INTO sales_order (customer_id, employee_id, status, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomerId());
            ps.setInt(2, order.getEmployeeId());
            ps.setString(3, order.getStatus());
            ps.setDouble(4, order.getTotal());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public boolean addOrderItem(OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item (order_id, product_id, qty, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, item.getOrderId());
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQty());
            ps.setDouble(4, item.getUnitPrice());
            ps.setDouble(5, item.getSubtotal());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE sales_order SET status=? WHERE order_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<SalesOrder> getAllOrders() throws SQLException {
        List<SalesOrder> list = new ArrayList<>();
        String sql = "SELECT so.*, c.name AS customer_name, e.name AS employee_name " +
                     "FROM sales_order so " +
                     "JOIN customer c ON so.customer_id = c.customer_id " +
                     "JOIN employee e ON so.employee_id = e.employee_id " +
                     "ORDER BY so.order_date DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapOrder(rs));
        }
        return list;
    }

    public SalesOrder getOrderById(int id) throws SQLException {
        String sql = "SELECT so.*, c.name AS customer_name, e.name AS employee_name " +
                     "FROM sales_order so " +
                     "JOIN customer c ON so.customer_id = c.customer_id " +
                     "JOIN employee e ON so.employee_id = e.employee_id " +
                     "WHERE so.order_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapOrder(rs);
        }
        return null;
    }

    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name FROM order_item oi JOIN product p ON oi.product_id = p.product_id WHERE oi.order_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("qty"),
                    rs.getDouble("unit_price")
                );
                item.setItemId(rs.getInt("item_id"));
                item.setOrderId(orderId);
                item.setSubtotal(rs.getDouble("subtotal"));
                items.add(item);
            }
        }
        return items;
    }

    public int getTotalOrdersCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales_order";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public double getTodayRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM sales_order WHERE DATE(order_date) = CURDATE()";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    public List<Object[]> getMonthlySalesSummary() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%b %Y') AS month, SUM(total) AS revenue, COUNT(*) AS orders " +
                     "FROM sales_order GROUP BY YEAR(order_date), MONTH(order_date) ORDER BY YEAR(order_date), MONTH(order_date) LIMIT 6";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{rs.getString("month"), rs.getDouble("revenue"), rs.getInt("orders")});
            }
        }
        return data;
    }

    public List<Object[]> getSalesPerformanceReport(String fromDate, String toDate) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT c.name AS customer_name, e.name AS employee_name, p.name AS product_name, " +
                     "cat.name AS category_name, oi.qty, oi.unit_price, oi.subtotal, so.order_date, so.status " +
                     "FROM sales_order so " +
                     "JOIN customer c ON so.customer_id = c.customer_id " +
                     "JOIN employee e ON so.employee_id = e.employee_id " +
                     "JOIN order_item oi ON so.order_id = oi.order_id " +
                     "JOIN product p ON oi.product_id = p.product_id " +
                     "JOIN category cat ON p.category_id = cat.category_id " +
                     "WHERE DATE(so.order_date) BETWEEN ? AND ? " +
                     "ORDER BY so.order_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getString("customer_name"), rs.getString("employee_name"),
                    rs.getString("product_name"), rs.getString("category_name"),
                    rs.getInt("qty"), rs.getDouble("unit_price"),
                    rs.getDouble("subtotal"), rs.getString("order_date"), rs.getString("status")
                });
            }
        }
        return data;
    }

    public List<Object[]> getDailyRevenueTrend(int days) throws SQLException {
        String sql = "SELECT DATE_FORMAT(order_date, '%d %b') AS day_label, " +
                     "DATE(order_date) AS order_day, " +
                     "COALESCE(SUM(total), 0) AS daily_total, " +
                     "COUNT(*) AS order_count " +
                     "FROM sales_order " +
                     "WHERE order_date >= CURDATE() - INTERVAL ? DAY " +
                     "GROUP BY DATE(order_date), DATE_FORMAT(order_date, '%d %b') " +
                     "ORDER BY order_day ASC";
        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{
                        rs.getString("day_label"),
                        rs.getDouble("daily_total"),
                        rs.getInt("order_count")
                    });
                }
            }
        }
        return result;
    }

    public List<Object[]> getInventoryReportData() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT p.product_id, p.name AS product_name, c.name AS category_name, " +
                     "p.stock_qty, p.price, (p.price * p.stock_qty) AS stock_value " +
                     "FROM product p " +
                     "LEFT JOIN category c ON p.category_id = c.category_id " +
                     "ORDER BY c.name, p.name";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("category_name") != null ? rs.getString("category_name") : "General",
                    rs.getInt("stock_qty"),
                    rs.getDouble("price"),
                    rs.getDouble("stock_value")
                });
            }
        }
        return data;
    }

    public List<Object[]> getTopCustomersReportData() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.name AS customer_name, c.email, c.phone, " +
                     "COUNT(so.order_id) AS total_orders, COALESCE(SUM(so.total), 0) AS total_spent " +
                     "FROM customer c " +
                     "JOIN sales_order so ON c.customer_id = so.customer_id " +
                     "WHERE so.status = 'Completed' " +
                     "GROUP BY c.customer_id, c.name, c.email, c.phone " +
                     "ORDER BY total_spent DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("customer_id"),
                    rs.getString("customer_name"),
                    rs.getString("email") != null ? rs.getString("email") : "-",
                    rs.getString("phone") != null ? rs.getString("phone") : "-",
                    rs.getInt("total_orders"),
                    rs.getDouble("total_spent")
                });
            }
        }
        return data;
    }

    public List<Object[]> getStaffSalesReportData() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.name AS employee_name, e.role, " +
                     "COUNT(so.order_id) AS total_orders, " +
                     "COALESCE(SUM(so.total), 0) AS total_sales " +
                     "FROM employee e " +
                     "LEFT JOIN sales_order so ON e.employee_id = so.employee_id AND so.status = 'Completed' " +
                     "GROUP BY e.employee_id, e.name, e.role " +
                     "ORDER BY total_sales DESC";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getInt("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("role"),
                    rs.getInt("total_orders"),
                    rs.getDouble("total_sales")
                });
            }
        }
        return data;
    }

    public List<Object[]> getDailySalesReportData(String fromDate, String toDate) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(order_date, '%Y-%m-%d') AS sale_date, " +
                     "COUNT(order_id) AS total_orders, " +
                     "AVG(total) AS avg_order_val, " +
                     "SUM(total) AS daily_revenue " +
                     "FROM sales_order " +
                     "WHERE status = 'Completed' " +
                     "  AND (? = '' OR DATE(order_date) >= ?) " +
                     "  AND (? = '' OR DATE(order_date) <= ?) " +
                     "GROUP BY DATE_FORMAT(order_date, '%Y-%m-%d') " +
                     "ORDER BY sale_date DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, fromDate != null ? fromDate : "");
            ps.setString(2, fromDate != null ? fromDate : "");
            ps.setString(3, toDate != null ? toDate : "");
            ps.setString(4, toDate != null ? toDate : "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new Object[]{
                    rs.getString("sale_date"),
                    rs.getInt("total_orders"),
                    rs.getDouble("avg_order_val"),
                    rs.getDouble("daily_revenue")
                });
            }
        }
        return data;
    }

    private SalesOrder mapOrder(ResultSet rs) throws SQLException {
        SalesOrder o = new SalesOrder();
        o.setOrderId(rs.getInt("order_id"));
        o.setCustomerId(rs.getInt("customer_id"));
        o.setEmployeeId(rs.getInt("employee_id"));
        o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
        o.setStatus(rs.getString("status"));
        o.setTotal(rs.getDouble("total"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setEmployeeName(rs.getString("employee_name"));
        return o;
    }
}
