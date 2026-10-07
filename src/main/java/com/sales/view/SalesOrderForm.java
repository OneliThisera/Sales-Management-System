package com.sales.view;

import com.sales.controller.CustomerController;
import com.sales.controller.OrderController;
import com.sales.controller.ProductController;
import com.sales.exception.InsufficientStockException;
import com.sales.model.*;
import com.sales.util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesOrderForm extends JFrame {

    private final Employee currentUser;
    private final OrderController orderController = new OrderController();
    private final CustomerController customerController = new CustomerController();
    private final ProductController productController = new ProductController();

    private JComboBox<Customer> cmbCustomer;
    private JComboBox<Product> cmbProduct;
    private JTextField txtQty;
    private JLabel lblUnitPrice, lblSubtotal, lblOrderTotal;
    private JTable itemsTable, ordersTable;
    private DefaultTableModel itemsModel, ordersModel;

    private final List<OrderItem> currentItems = new ArrayList<>();
    private double orderTotal = 0.0;

    private static final Color PRIMARY = com.sales.util.Theme.PRI;
    private static final Color DARK    = com.sales.util.Theme.INK;
    private static final Color WHITE   = Color.WHITE;
    private static final Color BG      = com.sales.util.Theme.BG;

    private JPanel viewPanel;

    public SalesOrderForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC & Tech - Sales Order & POS");
        setSize(1200, 740);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(getView());
    }

    public JPanel getView() {
        if (viewPanel == null) {
            viewPanel = new JPanel(new BorderLayout());
            viewPanel.add(buildHeader(), BorderLayout.NORTH);
            JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildOrderPanel(), buildAllOrdersPanel());
            center.setDividerLocation(560);
            center.setDividerSize(2);
            center.setBorder(null);
            viewPanel.add(center, BorderLayout.CENTER);
            loadCombos();
            loadAllOrders();
        }
        return viewPanel;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(com.sales.util.Theme.CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, com.sales.util.Theme.LINE),
            new EmptyBorder(16, 25, 16, 25)
        ));

        JLabel title = new JLabel("  New Sale / Order Processing", com.sales.util.IconFactory.getCartIcon(22, com.sales.util.Theme.PRI), JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(com.sales.util.Theme.INK);

        JLabel sub = new JLabel("Select customer  \u2192  Add PC components & hardware  \u2192  Complete sale & print invoice");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(com.sales.util.Theme.MUTED);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(sub);
        header.add(texts, BorderLayout.WEST);
        return header;
    }

    private JPanel buildOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(com.sales.util.Theme.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, com.sales.util.Theme.LINE),
            new EmptyBorder(20, 20, 20, 15)
        ));

        JPanel top = new JPanel(null);
        top.setBackground(com.sales.util.Theme.CARD);
        top.setPreferredSize(new Dimension(0, 280));

        JLabel lblSection = new JLabel("Order Details");
        lblSection.setFont(new Font("Georgia", Font.BOLD, 15));
        lblSection.setForeground(com.sales.util.Theme.INK);
        lblSection.setBounds(0, 0, 300, 22);

        JLabel lblCust = new JLabel("Customer *");
        lblCust.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCust.setForeground(com.sales.util.Theme.INK);
        lblCust.setBounds(0, 32, 200, 18);

        cmbCustomer = new JComboBox<>();
        com.sales.util.Theme.styleComboBox(cmbCustomer);
        cmbCustomer.setBounds(0, 53, 510, 36);

        JSeparator sep = new JSeparator();
        sep.setBounds(0, 100, 510, 1);
        sep.setForeground(com.sales.util.Theme.LINE);

        JLabel lblProd = new JLabel("Add PC Component / Product");
        lblProd.setFont(new Font("Georgia", Font.BOLD, 15));
        lblProd.setForeground(com.sales.util.Theme.INK);
        lblProd.setBounds(0, 110, 300, 22);

        JLabel lblProdCmb = new JLabel("Select Product *");
        lblProdCmb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblProdCmb.setForeground(com.sales.util.Theme.INK);
        lblProdCmb.setBounds(0, 138, 200, 18);

        cmbProduct = new JComboBox<>();
        com.sales.util.Theme.styleComboBox(cmbProduct);
        cmbProduct.setBounds(0, 158, 310, 36);
        cmbProduct.addActionListener(e -> updatePriceLabel());

        JLabel lblQtyLbl = new JLabel("Qty *");
        lblQtyLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQtyLbl.setForeground(com.sales.util.Theme.INK);
        lblQtyLbl.setBounds(322, 138, 80, 18);

        txtQty = new JTextField("1");
        com.sales.util.Theme.styleTextField(txtQty);
        txtQty.setBounds(322, 158, 80, 36);
        txtQty.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void recalc() { updatePriceLabel(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
        });

        JLabel lblPriceLbl = new JLabel("Unit Price:");
        lblPriceLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPriceLbl.setForeground(com.sales.util.Theme.INK);
        lblPriceLbl.setBounds(415, 138, 90, 18);

        lblUnitPrice = new JLabel("LKR 0.00");
        lblUnitPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUnitPrice.setForeground(com.sales.util.Theme.PRI);
        lblUnitPrice.setBounds(415, 158, 100, 36);

        JLabel lblSubtotalLbl = new JLabel("Subtotal:");
        lblSubtotalLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSubtotalLbl.setForeground(com.sales.util.Theme.INK);
        lblSubtotalLbl.setBounds(322, 207, 80, 18);

        lblSubtotal = new JLabel("LKR 0.00");
        lblSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSubtotal.setForeground(com.sales.util.Theme.OK);
        lblSubtotal.setBounds(322, 228, 200, 22);

        JButton btnAdd = buildButton("\u002B  Add Item", com.sales.util.Theme.PRI);
        btnAdd.setBounds(0, 228, 150, 38);
        btnAdd.addActionListener(e -> addItemToOrder());

        top.add(lblSection);
        top.add(lblCust);
        top.add(cmbCustomer);
        top.add(sep);
        top.add(lblProd);
        top.add(lblProdCmb);
        top.add(cmbProduct);
        top.add(lblQtyLbl);
        top.add(txtQty);
        top.add(lblPriceLbl);
        top.add(lblUnitPrice);
        top.add(lblSubtotalLbl);
        top.add(lblSubtotal);
        top.add(btnAdd);

        String[] itemCols = {"#", "Product", "Qty", "Unit Price", "Subtotal", ""};
        itemsModel = new DefaultTableModel(itemCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        itemsTable = new JTable(itemsModel);
        styleTable(itemsTable);
        itemsTable.getColumnModel().getColumn(0).setMaxWidth(30);
        itemsTable.getColumnModel().getColumn(5).setMaxWidth(50);

        JScrollPane itemScroll = new JScrollPane(itemsTable);
        com.sales.util.Theme.styleScrollPane(itemScroll);
        javax.swing.border.TitledBorder tb = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(com.sales.util.Theme.LINE), "  Order Items  ");
        tb.setTitleColor(com.sales.util.Theme.INK);
        tb.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        itemScroll.setBorder(tb);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(com.sales.util.Theme.CARD);
        bottomBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, com.sales.util.Theme.LINE),
            new EmptyBorder(12, 12, 12, 12)
        ));

        JButton btnRemove = buildButton("Remove Item", com.sales.util.Theme.BAD);
        btnRemove.setPreferredSize(new Dimension(110, 36));
        btnRemove.addActionListener(e -> removeSelectedItem());

        lblOrderTotal = new JLabel("Total:  LKR 0.00");
        lblOrderTotal.setFont(new Font("Georgia", Font.BOLD, 17));
        lblOrderTotal.setForeground(com.sales.util.Theme.INK);

        JLabel lblCash = new JLabel("Cash (LKR):");
        lblCash.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCash.setForeground(com.sales.util.Theme.MUTED);

        JTextField txtCash = new JTextField(6);
        com.sales.util.Theme.styleTextField(txtCash);

        JLabel lblChange = new JLabel("Change: LKR 0.00");
        lblChange.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblChange.setForeground(com.sales.util.Theme.PRI);

        txtCash.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void updateChange() {
                try {
                    double cash = Double.parseDouble(txtCash.getText().trim());
                    double change = cash - orderTotal;
                    lblChange.setText(String.format("Change: LKR %.2f", Math.max(0, change)));
                    lblChange.setForeground(change >= 0 ? com.sales.util.Theme.PRI : com.sales.util.Theme.BAD);
                } catch (Exception ex) {
                    lblChange.setText("Change: LKR 0.00");
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateChange(); }
        });

        JButton btnSaveOrder = buildButton("Complete Sale", com.sales.util.Theme.PRI);
        btnSaveOrder.setPreferredSize(new Dimension(140, 38));
        btnSaveOrder.addActionListener(e -> handleSaveOrder());

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottomRight.setOpaque(false);
        bottomRight.add(lblOrderTotal);
        bottomRight.add(lblCash);
        bottomRight.add(txtCash);
        bottomRight.add(lblChange);
        bottomRight.add(btnSaveOrder);

        bottomBar.add(btnRemove, BorderLayout.WEST);
        bottomBar.add(bottomRight, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(itemScroll, BorderLayout.CENTER);
        panel.add(bottomBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildAllOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 15, 20, 20));

        JLabel title = new JLabel("Recent Orders");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(com.sales.util.Theme.INK);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] cols = {"#ID", "Customer", "Sales Rep", "Date", "Total", "Status"};
        ordersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(ordersModel);
        styleTable(ordersTable);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        statusBar.setBackground(com.sales.util.Theme.CARD);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, com.sales.util.Theme.LINE));

        JLabel lblStatus = new JLabel("Change Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(com.sales.util.Theme.INK);
        String[] statuses = {"Pending", "Confirmed", "Delivered", "Paid", "Cancelled"};
        JComboBox<String> cmbStatus = new JComboBox<>(statuses);
        com.sales.util.Theme.styleComboBox(cmbStatus);
        cmbStatus.setPreferredSize(new Dimension(115, 32));

        JButton btnChangeStatus = buildButton("Apply", com.sales.util.Theme.PRI_HOVER);
        btnChangeStatus.setPreferredSize(new Dimension(80, 32));
        btnChangeStatus.addActionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an order first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderId = Integer.parseInt(ordersModel.getValueAt(row, 0).toString().replace("#", ""));
            try {
                orderController.updateStatus(orderId, (String) cmbStatus.getSelectedItem());
                loadAllOrders();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnInvoice = buildButton("Invoice (Jasper)", com.sales.util.Theme.PRI);
        btnInvoice.setPreferredSize(new Dimension(150, 32));
        btnInvoice.addActionListener(e -> {
            int row = ordersTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select an order to print invoice.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int orderId = Integer.parseInt(ordersModel.getValueAt(row, 0).toString().replace("#", ""));
            try {
                com.sales.util.JasperReportUtil.showInvoiceReport(orderId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating invoice: " + ex.getMessage(), "JasperReports Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        statusBar.add(lblStatus);
        statusBar.add(cmbStatus);
        statusBar.add(btnChangeStatus);
        statusBar.add(btnInvoice);

        JScrollPane ordersScroll = new JScrollPane(ordersTable);
        com.sales.util.Theme.styleScrollPane(ordersScroll);

        panel.add(title, BorderLayout.NORTH);
        panel.add(ordersScroll, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);
        return panel;
    }

    private void addItemToOrder() {
        Product selected = (Product) cmbProduct.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Validator.isPositiveInt(txtQty.getText())) {
            JOptionPane.showMessageDialog(this, "Quantity must be a positive number.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qty = Integer.parseInt(txtQty.getText().trim());
        if (qty > selected.getStockQty()) {
            JOptionPane.showMessageDialog(this, "Requested qty exceeds available stock (" + selected.getStockQty() + ").", "Stock Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        OrderItem item = new OrderItem(selected.getProductId(), selected.getName(), qty, selected.getPrice());
        currentItems.add(item);
        itemsModel.addRow(new Object[]{
            currentItems.size(), item.getProductName(), item.getQty(),
            String.format("%.2f", item.getUnitPrice()),
            String.format("%.2f", item.getSubtotal()), "\u2716"
        });
        orderTotal += item.getSubtotal();
        lblOrderTotal.setText(String.format("Total:  LKR %.2f", orderTotal));
        txtQty.setText("1");
    }

    private void removeSelectedItem() {
        int row = itemsTable.getSelectedRow();
        if (row < 0) return;
        orderTotal -= currentItems.get(row).getSubtotal();
        currentItems.remove(row);
        itemsModel.removeRow(row);
        lblOrderTotal.setText(String.format("Total:  LKR %.2f", orderTotal));
    }

    private void handleSaveOrder() {
        if (cmbCustomer.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (currentItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one product to the order.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Customer customer = (Customer) cmbCustomer.getSelectedItem();
            SalesOrder order = new SalesOrder();
            order.setCustomerId(customer.getCustomerId());
            order.setEmployeeId(currentUser.getEmployeeId());
            order.setStatus("Pending");
            order.setTotal(orderTotal);
            order.setItems(new ArrayList<>(currentItems));

            int orderId = orderController.createOrder(order);
            int choice = JOptionPane.showConfirmDialog(this,
                "Order #" + orderId + " created successfully!\nTotal: LKR " + String.format("%.2f", orderTotal) + "\n\nWould you like to view/print the Jasper invoice now?",
                "Order Created", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    com.sales.util.JasperReportUtil.showInvoiceReport(orderId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error generating invoice: " + ex.getMessage(), "JasperReports Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            currentItems.clear();
            itemsModel.setRowCount(0);
            orderTotal = 0.0;
            lblOrderTotal.setText("Total:  LKR 0.00");
            loadAllOrders();
        } catch (InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Insufficient Stock", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePriceLabel() {
        Product p = (Product) cmbProduct.getSelectedItem();
        if (p != null) {
            lblUnitPrice.setText(String.format("LKR %.2f", p.getPrice()));
            try {
                int qty = Integer.parseInt(txtQty.getText().trim());
                if (qty > 0) {
                    double subtotal = qty * p.getPrice();
                    lblSubtotal.setText(String.format("LKR %.2f", subtotal));
                    lblSubtotal.setForeground(com.sales.util.Theme.OK);
                } else {
                    lblSubtotal.setText("LKR 0.00");
                }
            } catch (NumberFormatException e) {
                lblSubtotal.setText("LKR 0.00");
            }
        } else {
            lblUnitPrice.setText("LKR 0.00");
            lblSubtotal.setText("LKR 0.00");
        }
    }

    private void loadCombos() {
        try {
            cmbCustomer.removeAllItems();
            for (Customer c : customerController.getAll()) cmbCustomer.addItem(c);
            cmbProduct.removeAllItems();
            for (Product p : productController.getAll()) cmbProduct.addItem(p);
            updatePriceLabel();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllOrders() {
        try {
            ordersModel.setRowCount(0);
            for (SalesOrder o : orderController.getAllOrders()) {
                ordersModel.addRow(new Object[]{
                    "#" + o.getOrderId(), o.getCustomerName(), o.getEmployeeName(),
                    o.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    String.format("%.2f", o.getTotal()), o.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void styleTable(JTable t) {
        com.sales.util.Theme.styleTable(t);
    }
}
