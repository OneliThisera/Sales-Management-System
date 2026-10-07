package com.sales.controller;

import com.sales.dao.CategoryDAO;
import com.sales.dao.ProductDAO;
import com.sales.exception.InsufficientStockException;
import com.sales.model.Category;
import com.sales.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductController {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public List<Product> getAll() throws SQLException {
        return productDAO.getAllProducts();
    }

    public Product getById(int id) throws SQLException {
        return productDAO.getById(id);
    }

    public void add(Product p) throws SQLException {
        productDAO.addProduct(p);
    }

    public void update(Product p) throws SQLException {
        productDAO.updateProduct(p);
    }

    public void delete(int id) throws SQLException {
        productDAO.deleteProduct(id);
    }

    public void reduceStock(int productId, int qty) throws SQLException, InsufficientStockException {
        productDAO.reduceStock(productId, qty);
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryDAO.getAllCategories();
    }

    public void addCategory(Category c) throws SQLException {
        categoryDAO.addCategory(c);
    }

    public void updateCategory(Category c) throws SQLException {
        categoryDAO.updateCategory(c);
    }

    public void deleteCategory(int id) throws SQLException {
        categoryDAO.deleteCategory(id);
    }

    public List<Product> getLowStockProducts(int threshold) throws SQLException {
        return productDAO.getLowStockProducts(threshold);
    }
}
