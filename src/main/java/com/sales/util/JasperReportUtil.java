package com.sales.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class JasperReportUtil {

    private static JasperReport compileFromClasspath(String resourcePath) throws JRException {
        InputStream stream = JasperReportUtil.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new JRException("Report template not found: " + resourcePath);
        }
        return JasperCompileManager.compileReport(stream);
    }

    // 1. Sales Performance Report
    public static JasperPrint prepareSalesReport(String fromDate, String toDate) throws Exception {
        JasperReport report = compileFromClasspath("/reports/sales_report.jrxml");
        Map<String, Object> params = new HashMap<>();
        params.put("FROM_DATE", fromDate);
        params.put("TO_DATE", toDate);
        Connection conn = DBConnection.getInstance().getConnection();
        return JasperFillManager.fillReport(report, params, conn);
    }

    public static void showSalesReport(String fromDate, String toDate) throws Exception {
        JasperPrint print = prepareSalesReport(fromDate, toDate);
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle("Sales Performance Report - JasperReports");
        viewer.setVisible(true);
    }

    public static void exportSalesReportPdf(String fromDate, String toDate, String filePath) throws Exception {
        JasperPrint print = prepareSalesReport(fromDate, toDate);
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }

    // 2. Inventory Stock & Valuation Report
    public static JasperPrint prepareInventoryReport() throws Exception {
        JasperReport report = compileFromClasspath("/reports/inventory_report.jrxml");
        Connection conn = DBConnection.getInstance().getConnection();
        return JasperFillManager.fillReport(report, new HashMap<>(), conn);
    }

    public static void showInventoryReport() throws Exception {
        JasperPrint print = prepareInventoryReport();
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle("Inventory Stock Report - JasperReports");
        viewer.setVisible(true);
    }

    public static void exportInventoryReportPdf(String filePath) throws Exception {
        JasperPrint print = prepareInventoryReport();
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }

    // 3. Top Customers Report
    public static JasperPrint prepareTopCustomersReport() throws Exception {
        JasperReport report = compileFromClasspath("/reports/top_customers_report.jrxml");
        Connection conn = DBConnection.getInstance().getConnection();
        return JasperFillManager.fillReport(report, new HashMap<>(), conn);
    }

    public static void showTopCustomersReport() throws Exception {
        JasperPrint print = prepareTopCustomersReport();
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle("Top Customers Report - JasperReports");
        viewer.setVisible(true);
    }

    public static void exportTopCustomersReportPdf(String filePath) throws Exception {
        JasperPrint print = prepareTopCustomersReport();
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }

    // 4. Staff / Cashier Sales Performance Report
    public static JasperPrint prepareStaffSalesReport() throws Exception {
        JasperReport report = compileFromClasspath("/reports/staff_sales_report.jrxml");
        Connection conn = DBConnection.getInstance().getConnection();
        return JasperFillManager.fillReport(report, new HashMap<>(), conn);
    }

    public static void showStaffSalesReport() throws Exception {
        JasperPrint print = prepareStaffSalesReport();
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle("Staff Sales Performance Report - JasperReports");
        viewer.setVisible(true);
    }

    public static void exportStaffSalesReportPdf(String filePath) throws Exception {
        JasperPrint print = prepareStaffSalesReport();
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }

    // 5. Daily Sales Summary Report
    public static JasperPrint prepareDailySalesReport(String fromDate, String toDate) throws Exception {
        JasperReport report = compileFromClasspath("/reports/daily_sales_report.jrxml");
        Map<String, Object> params = new HashMap<>();
        params.put("FROM_DATE", fromDate);
        params.put("TO_DATE", toDate);
        Connection conn = DBConnection.getInstance().getConnection();
        return JasperFillManager.fillReport(report, params, conn);
    }

    public static void showDailySalesReport(String fromDate, String toDate) throws Exception {
        JasperPrint print = prepareDailySalesReport(fromDate, toDate);
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle("Daily Revenue Summary Report - JasperReports");
        viewer.setVisible(true);
    }

    public static void exportDailySalesReportPdf(String fromDate, String toDate, String filePath) throws Exception {
        JasperPrint print = prepareDailySalesReport(fromDate, toDate);
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }

    // 6. Invoice Report
    public static JasperPrint prepareInvoiceReport(int orderId) throws Exception {
        JasperReport report = compileFromClasspath("/reports/invoice_report.jrxml");
        Map<String, Object> params = new HashMap<>();
        params.put("ORDER_ID", orderId);
        Connection conn = DBConnection.getInstance().getConnection();
        return JasperFillManager.fillReport(report, params, conn);
    }

    public static void showInvoiceReport(int orderId) throws Exception {
        JasperPrint print = prepareInvoiceReport(orderId);
        JasperViewer viewer = new JasperViewer(print, false);
        viewer.setTitle("Invoice #" + orderId + " - JasperReports");
        viewer.setVisible(true);
    }

    public static void exportInvoicePdf(int orderId, String filePath) throws Exception {
        JasperPrint print = prepareInvoiceReport(orderId);
        JasperExportManager.exportReportToPdfFile(print, filePath);
    }
}
