package com.sales.dao;

import com.sales.exception.InsufficientStockException;
import com.sales.model.Product;
import com.sales.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private Connection getConn() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id ORDER BY p.name";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Product p = mapRow(rs);
                p.setCategoryName(rs.getString("category_name"));
                list.add(p);
            }
        }
        return list;
    }

    public Product getById(int id) throws SQLException {
        String sql = "SELECT p.*, c.name AS category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id WHERE p.product_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = mapRow(rs);
                p.setCategoryName(rs.getString("category_name"));
                return p;
            }
        }
        return null;
    }

    public boolean addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO product (name, description, price, stock_qty, category_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQty());
            ps.setInt(5, p.getCategoryId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProduct(Product p) throws SQLException {
        String sql = "UPDATE product SET name=?, description=?, price=?, stock_qty=?, category_id=? WHERE product_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStockQty());
            ps.setInt(5, p.getCategoryId());
            ps.setInt(6, p.getProductId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public void reduceStock(int productId, int qty) throws SQLException, InsufficientStockException {
        String check = "SELECT stock_qty FROM product WHERE product_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(check)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int current = rs.getInt("stock_qty");
                if (current < qty) {
                    throw new InsufficientStockException("Not enough stock. Available: " + current);
                }
            }
        }
        String update = "UPDATE product SET stock_qty = stock_qty - ? WHERE product_id=?";
        try (PreparedStatement ps = getConn().prepareStatement(update)) {
            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public List<Product> getLowStockProducts(int threshold) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name AS category_name FROM product p LEFT JOIN category c ON p.category_id = c.category_id WHERE p.stock_qty <= ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, threshold);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = mapRow(rs);
                p.setCategoryName(rs.getString("category_name"));
                list.add(p);
            }
        }
        return list;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("stock_qty"),
            rs.getInt("category_id")
        );
    }
}
