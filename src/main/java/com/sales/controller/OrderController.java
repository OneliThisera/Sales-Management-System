package com.sales.controller;

import com.sales.dao.OrderDAO;
import com.sales.dao.PaymentDAO;
import com.sales.dao.ProductDAO;
import com.sales.exception.InsufficientStockException;
import com.sales.model.OrderItem;
import com.sales.model.Payment;
import com.sales.model.SalesOrder;

import java.sql.SQLException;
import java.util.List;

public class OrderController {

    private final OrderDAO orderDAO = new OrderDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public int createOrder(SalesOrder order) throws SQLException, InsufficientStockException {
        for (OrderItem item : order.getItems()) {
            productDAO.reduceStock(item.getProductId(), item.getQty());
        }
        int orderId = orderDAO.createOrder(order);
        for (OrderItem item : order.getItems()) {
            item.setOrderId(orderId);
            orderDAO.addOrderItem(item);
        }
        return orderId;
    }

    public void recordPayment(Payment payment) throws SQLException {
        paymentDAO.addPayment(payment);
        orderDAO.updateOrderStatus(payment.getOrderId(), "Paid");
    }

    public void updateStatus(int orderId, String status) throws SQLException {
        orderDAO.updateOrderStatus(orderId, status);
    }

    public List<SalesOrder> getAllOrders() throws SQLException {
        return orderDAO.getAllOrders();
    }

    public SalesOrder getOrderById(int id) throws SQLException {
        return orderDAO.getOrderById(id);
    }

    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        return orderDAO.getOrderItems(orderId);
    }

    public int getTotalOrdersCount() throws SQLException {
        return orderDAO.getTotalOrdersCount();
    }

    public double getTodayRevenue() throws SQLException {
        return orderDAO.getTodayRevenue();
    }

    public List<Object[]> getMonthlySales() throws SQLException {
        return orderDAO.getMonthlySalesSummary();
    }

    public List<Object[]> getSalesPerformanceReport(String from, String to) throws SQLException {
        return orderDAO.getSalesPerformanceReport(from, to);
    }

    public List<Object[]> getDailyRevenueTrend(int days) throws SQLException {
        return orderDAO.getDailyRevenueTrend(days);
    }

    public List<Object[]> getInventoryReport() throws SQLException {
        return orderDAO.getInventoryReportData();
    }

    public List<Object[]> getTopCustomersReport() throws SQLException {
        return orderDAO.getTopCustomersReportData();
    }

    public List<Object[]> getStaffSalesReport() throws SQLException {
        return orderDAO.getStaffSalesReportData();
    }

    public List<Object[]> getDailySalesReport(String from, String to) throws SQLException {
        return orderDAO.getDailySalesReportData(from, to);
    }
}
