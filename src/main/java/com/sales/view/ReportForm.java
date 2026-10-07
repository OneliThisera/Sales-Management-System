package com.sales.view;

import com.sales.controller.OrderController;
import com.sales.model.Employee;
import com.sales.util.IconFactory;
import com.sales.util.JasperReportUtil;
import com.sales.util.Theme;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportForm extends JFrame {

    private final Employee currentUser;
    private final OrderController orderController = new OrderController();

    private JComboBox<String> cmbReportType;
    private JTextField txtFrom, txtTo;
    private JLabel lblFrom, lblTo;
    private JPanel jasperContainer;
    private JTable reportTable;
    private DefaultTableModel reportModel;
    private JLabel lblTotalRevenue, lblTotalQty, lblTotalOrders;
    private JTabbedPane tabbedPane;

    private static final Color PRIMARY = Theme.PRI;
    private static final Color WHITE   = Color.WHITE;
    private static final Color BG      = Theme.BG;

    private JPanel viewPanel;

    public ReportForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC & Tech — Sales & Analytics Reports");
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(getView());
    }

    public JPanel getView() {
        if (viewPanel == null) {
            if (currentUser != null && currentUser.isCashier()) {
                viewPanel = new JPanel(new GridBagLayout());
                viewPanel.setBackground(BG);
                JLabel msg = new JLabel("Access Restricted: Sales reports and financial analytics are reserved for Managers and Administrators.", IconFactory.getAlertIcon(24, Theme.BAD), JLabel.CENTER);
                msg.setFont(new Font("Georgia", Font.BOLD, 15));
                msg.setForeground(Theme.BAD);
                viewPanel.add(msg);
                return viewPanel;
            }
            viewPanel = new JPanel(new BorderLayout());
            viewPanel.add(buildHeader(), BorderLayout.NORTH);
            viewPanel.add(buildCenter(), BorderLayout.CENTER);
            generateReport();
        }
        return viewPanel;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE),
            new EmptyBorder(16, 25, 16, 25)
        ));

        JLabel title = new JLabel("  Sales & Analytics Reports", IconFactory.getReportIcon(22, Theme.PRI), JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(Theme.INK);

        JLabel sub = new JLabel("Official JasperReports suite: Sales Performance, Inventory Status, Top Customers, Staff Sales, and Daily Revenue");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(Theme.MUTED);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(sub);
        header.add(texts, BorderLayout.WEST);
        return header;
    }

    private JPanel buildCenter() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);

        panel.add(buildFilterBar(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Theme.CARD);
        tabbedPane.setForeground(Theme.INK);

        jasperContainer = new JPanel(new BorderLayout());
        jasperContainer.setBackground(BG);
        JLabel lblLoading = new JLabel("Select a report and click Generate Report to render Jasper Report", JLabel.CENTER);
        lblLoading.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLoading.setForeground(Theme.MUTED);
        jasperContainer.add(lblLoading, BorderLayout.CENTER);

        tabbedPane.addTab("Jasper Report (Official)", jasperContainer);
        tabbedPane.addTab("Tabular Data Grid", buildTableArea());

        panel.add(tabbedPane, BorderLayout.CENTER);
        panel.add(buildSummaryBar(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        bar.setBackground(Theme.CARD);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE));

        JLabel lblType = new JLabel("Report Type:");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setForeground(Theme.INK);

        String[] reportOptions = {
            "1. Sales Performance Report",
            "2. Inventory & Stock Valuation",
            "3. Top Customers & Spend Analysis",
            "4. Staff Sales Performance",
            "5. Daily Revenue Breakdown"
        };
        cmbReportType = new JComboBox<>(reportOptions);
        com.sales.util.Theme.styleComboBox(cmbReportType);
        cmbReportType.setPreferredSize(new Dimension(230, 36));
        cmbReportType.addActionListener(e -> onReportTypeChanged());

        lblFrom = new JLabel("From:");
        lblFrom.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFrom.setForeground(Theme.INK);

        txtFrom = new JTextField(LocalDate.now().minusDays(30).toString());
        com.sales.util.Theme.styleTextField(txtFrom);
        txtFrom.setPreferredSize(new Dimension(105, 36));

        lblTo = new JLabel("To:");
        lblTo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTo.setForeground(Theme.INK);

        txtTo = new JTextField(LocalDate.now().plusDays(1).toString());
        com.sales.util.Theme.styleTextField(txtTo);
        txtTo.setPreferredSize(new Dimension(105, 36));

        JButton btnGenerate = buildButton("Generate Report", Theme.PRI);
        btnGenerate.setPreferredSize(new Dimension(135, 36));
        btnGenerate.addActionListener(e -> generateReport());

        JButton btnExportPdf = buildButton("Export PDF", Theme.PRI_HOVER);
        btnExportPdf.setPreferredSize(new Dimension(110, 36));
        btnExportPdf.addActionListener(e -> exportJasperPdf());

        JButton btnPopout = buildButton("Pop-out", new Color(0x1E, 0x29, 0x4B));
        btnPopout.setPreferredSize(new Dimension(95, 36));
        btnPopout.addActionListener(e -> openJasperReport());

        bar.add(lblType);
        bar.add(cmbReportType);
        bar.add(lblFrom);
        bar.add(txtFrom);
        bar.add(lblTo);
        bar.add(txtTo);
        bar.add(btnGenerate);
        bar.add(btnExportPdf);
        bar.add(btnPopout);
        return bar;
    }

    private void onReportTypeChanged() {
        int index = cmbReportType.getSelectedIndex();
        boolean needsDate = (index == 0 || index == 4);
        lblFrom.setEnabled(needsDate);
        txtFrom.setEnabled(needsDate);
        lblTo.setEnabled(needsDate);
        txtTo.setEnabled(needsDate);
        generateReport();
    }

    private JPanel buildTableArea() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));

        reportModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable = new JTable(reportModel);
        com.sales.util.Theme.styleTable(reportTable);
        reportTable.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(reportTable);
        com.sales.util.Theme.styleScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSummaryBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 14));
        bar.setBackground(Theme.CARD);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.LINE));

        lblTotalOrders = new JLabel("Records: 0");
        lblTotalOrders.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalOrders.setForeground(Theme.MUTED);

        lblTotalQty = new JLabel("Units: 0");
        lblTotalQty.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalQty.setForeground(Theme.MUTED);

        lblTotalRevenue = new JLabel("Total Value: Rs. 0.00");
        lblTotalRevenue.setFont(new Font("Georgia", Font.BOLD, 16));
        lblTotalRevenue.setForeground(Theme.PRI);

        bar.add(lblTotalOrders);
        bar.add(new JSeparator(SwingConstants.VERTICAL));
        bar.add(lblTotalQty);
        bar.add(new JSeparator(SwingConstants.VERTICAL));
        bar.add(lblTotalRevenue);
        return bar;
    }

    private JasperPrint createJasperPrintForCurrentSelection() throws Exception {
        int index = cmbReportType.getSelectedIndex();
        String from = txtFrom.getText().trim();
        String to   = txtTo.getText().trim();

        switch (index) {
            case 0:
                validateDates(from, to);
                return JasperReportUtil.prepareSalesReport(from, to);
            case 1:
                return JasperReportUtil.prepareInventoryReport();
            case 2:
                return JasperReportUtil.prepareTopCustomersReport();
            case 3:
                return JasperReportUtil.prepareStaffSalesReport();
            case 4:
                validateDates(from, to);
                return JasperReportUtil.prepareDailySalesReport(from, to);
            default:
                return JasperReportUtil.prepareSalesReport(from, to);
        }
    }

    private void validateDates(String from, String to) {
        if (!from.matches("\\d{4}-\\d{2}-\\d{2}") || !to.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Please enter dates in YYYY-MM-DD format.");
        }
    }

    private void generateReport() {
        try {
            JasperPrint print = createJasperPrintForCurrentSelection();
            JRViewer viewer = new JRViewer(print);
            jasperContainer.removeAll();
            jasperContainer.add(viewer, BorderLayout.CENTER);
            jasperContainer.revalidate();
            jasperContainer.repaint();
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        } catch (Exception ex) {
            jasperContainer.removeAll();
            JLabel err = new JLabel("Error rendering Jasper Report: " + ex.getMessage(), JLabel.CENTER);
            err.setForeground(Theme.BAD);
            jasperContainer.add(err, BorderLayout.CENTER);
            jasperContainer.revalidate();
            jasperContainer.repaint();
        }

        loadTabularData();
    }

    private void loadTabularData() {
        int index = cmbReportType.getSelectedIndex();
        String from = txtFrom.getText().trim();
        String to   = txtTo.getText().trim();

        try {
            switch (index) {
                case 0: // Sales Performance
                    String[] cols0 = {"Customer", "Sales Rep", "Product", "Category", "Qty", "Unit Price (Rs.)", "Subtotal (Rs.)", "Order Date", "Status"};
                    reportModel.setDataVector(new Object[][]{}, cols0);
                    List<Object[]> data0 = orderController.getSalesPerformanceReport(from, to);
                    double totalRev0 = 0;
                    int totalQty0 = 0;
                    for (Object[] row : data0) {
                        reportModel.addRow(new Object[]{
                            row[0], row[1], row[2], row[3], row[4],
                            String.format("%.2f", row[5]),
                            String.format("%.2f", row[6]),
                            row[7], row[8]
                        });
                        totalRev0 += (double) row[6];
                        totalQty0 += (int) row[4];
                    }
                    lblTotalOrders.setText("Rows: " + data0.size());
                    lblTotalQty.setText("Total Qty Sold: " + totalQty0);
                    lblTotalRevenue.setText(String.format("Total Revenue: LKR %.2f", totalRev0));
                    break;

                case 1: // Inventory
                    String[] cols1 = {"ID", "Product Name", "Category", "Stock Qty", "Unit Price (Rs.)", "Total Value (Rs.)"};
                    reportModel.setDataVector(new Object[][]{}, cols1);
                    List<Object[]> data1 = orderController.getInventoryReport();
                    double totalVal1 = 0;
                    int totalStock1 = 0;
                    for (Object[] row : data1) {
                        reportModel.addRow(new Object[]{
                            row[0], row[1], row[2], row[3],
                            String.format("%.2f", row[4]),
                            String.format("%.2f", row[5])
                        });
                        totalStock1 += (int) row[3];
                        totalVal1 += (double) row[5];
                    }
                    lblTotalOrders.setText("Products: " + data1.size());
                    lblTotalQty.setText("Total Units in Stock: " + totalStock1);
                    lblTotalRevenue.setText(String.format("Total Valuation: LKR %.2f", totalVal1));
                    break;

                case 2: // Top Customers
                    String[] cols2 = {"Customer ID", "Customer Name", "Email", "Phone", "Total Orders", "Total Spent (Rs.)"};
                    reportModel.setDataVector(new Object[][]{}, cols2);
                    List<Object[]> data2 = orderController.getTopCustomersReport();
                    double totalSpent2 = 0;
                    int totalOrders2 = 0;
                    for (Object[] row : data2) {
                        reportModel.addRow(new Object[]{
                            row[0], row[1], row[2], row[3], row[4],
                            String.format("%.2f", row[5])
                        });
                        totalOrders2 += (int) row[4];
                        totalSpent2 += (double) row[5];
                    }
                    lblTotalOrders.setText("Customers: " + data2.size());
                    lblTotalQty.setText("Orders Placed: " + totalOrders2);
                    lblTotalRevenue.setText(String.format("Cumulative Spend: LKR %.2f", totalSpent2));
                    break;

                case 3: // Staff Performance
                    String[] cols3 = {"Staff ID", "Employee Name", "Role", "Orders Processed", "Sales Total (Rs.)"};
                    reportModel.setDataVector(new Object[][]{}, cols3);
                    List<Object[]> data3 = orderController.getStaffSalesReport();
                    double totalSales3 = 0;
                    int totalOrders3 = 0;
                    for (Object[] row : data3) {
                        reportModel.addRow(new Object[]{
                            row[0], row[1], row[2], row[3],
                            String.format("%.2f", row[4])
                        });
                        totalOrders3 += (int) row[3];
                        totalSales3 += (double) row[4];
                    }
                    lblTotalOrders.setText("Staff Members: " + data3.size());
                    lblTotalQty.setText("Total Orders: " + totalOrders3);
                    lblTotalRevenue.setText(String.format("Total Sales: LKR %.2f", totalSales3));
                    break;

                case 4: // Daily Revenue
                    String[] cols4 = {"Date", "Orders Count", "Average Order Value (Rs.)", "Total Revenue (Rs.)"};
                    reportModel.setDataVector(new Object[][]{}, cols4);
                    List<Object[]> data4 = orderController.getDailySalesReport(from, to);
                    double totalRev4 = 0;
                    int totalOrders4 = 0;
                    for (Object[] row : data4) {
                        reportModel.addRow(new Object[]{
                            row[0], row[1],
                            String.format("%.2f", row[2]),
                            String.format("%.2f", row[3])
                        });
                        totalOrders4 += (int) row[1];
                        totalRev4 += (double) row[3];
                    }
                    lblTotalOrders.setText("Days Recorded: " + data4.size());
                    lblTotalQty.setText("Total Orders: " + totalOrders4);
                    lblTotalRevenue.setText(String.format("Period Revenue: LKR %.2f", totalRev4));
                    break;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error querying database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openJasperReport() {
        try {
            int index = cmbReportType.getSelectedIndex();
            String from = txtFrom.getText().trim();
            String to   = txtTo.getText().trim();
            switch (index) {
                case 0:
                    validateDates(from, to);
                    JasperReportUtil.showSalesReport(from, to);
                    break;
                case 1:
                    JasperReportUtil.showInventoryReport();
                    break;
                case 2:
                    JasperReportUtil.showTopCustomersReport();
                    break;
                case 3:
                    JasperReportUtil.showStaffSalesReport();
                    break;
                case 4:
                    validateDates(from, to);
                    JasperReportUtil.showDailySalesReport(from, to);
                    break;
            }
        } catch (IllegalArgumentException iae) {
            JOptionPane.showMessageDialog(this, iae.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error opening Jasper Report: " + ex.getMessage(), "JasperReports Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportJasperPdf() {
        int index = cmbReportType.getSelectedIndex();
        String from = txtFrom.getText().trim();
        String to   = txtTo.getText().trim();

        String defaultFileName;
        switch (index) {
            case 0:
                defaultFileName = "SalesReport_" + from + "_to_" + to + ".pdf";
                break;
            case 1:
                defaultFileName = "InventoryStockReport_" + LocalDate.now() + ".pdf";
                break;
            case 2:
                defaultFileName = "TopCustomersReport_" + LocalDate.now() + ".pdf";
                break;
            case 3:
                defaultFileName = "StaffPerformanceReport_" + LocalDate.now() + ".pdf";
                break;
            case 4:
                defaultFileName = "DailyRevenueReport_" + from + "_to_" + to + ".pdf";
                break;
            default:
                defaultFileName = "Report_" + LocalDate.now() + ".pdf";
                break;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(defaultFileName));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            try {
                switch (index) {
                    case 0:
                        validateDates(from, to);
                        JasperReportUtil.exportSalesReportPdf(from, to, path);
                        break;
                    case 1:
                        JasperReportUtil.exportInventoryReportPdf(path);
                        break;
                    case 2:
                        JasperReportUtil.exportTopCustomersReportPdf(path);
                        break;
                    case 3:
                        JasperReportUtil.exportStaffSalesReportPdf(path);
                        break;
                    case 4:
                        validateDates(from, to);
                        JasperReportUtil.exportDailySalesReportPdf(from, to, path);
                        break;
                }
                JOptionPane.showMessageDialog(this, "PDF Exported successfully to:\n" + path, "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting PDF: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton buildButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.darker() : color);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
