package com.sales.view;

import com.sales.controller.OrderController;
import com.sales.model.Employee;
import com.sales.model.Payment;
import com.sales.model.SalesOrder;
import com.sales.util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentForm extends JFrame {

    private final Employee currentUser;
    private final OrderController orderController = new OrderController();

    private JComboBox<String> cmbOrder;
    private JTextField txtAmount;
    private JComboBox<String> cmbMethod;
    private JLabel lblOrderTotal, lblBalance;
    private JTable paymentTable;
    private DefaultTableModel paymentModel;
    private List<SalesOrder> allOrders;

    private static final Color PRIMARY  = com.sales.util.Theme.PRI;
    private static final Color SUCCESS  = com.sales.util.Theme.PRI;
    private static final Color WHITE    = Color.WHITE;
    private static final Color BG       = com.sales.util.Theme.BG;

    private JPanel viewPanel;

    public PaymentForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC & Tech — Payment Settlement");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(getView());
    }

    public JPanel getView() {
        if (viewPanel == null) {
            viewPanel = new JPanel(new BorderLayout());
            viewPanel.add(buildHeader(), BorderLayout.NORTH);
            viewPanel.add(buildCenter(), BorderLayout.CENTER);
            loadOrders();
        }
        return viewPanel;
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(com.sales.util.Theme.CARD);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, com.sales.util.Theme.LINE),
            new EmptyBorder(16, 25, 16, 25)
        ));

        JLabel title = new JLabel("  Payment Settlement", com.sales.util.IconFactory.getPaymentIcon(22, com.sales.util.Theme.PRI), JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(com.sales.util.Theme.INK);

        JLabel sub = new JLabel("Record cash, card or QR payments for orders");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(com.sales.util.Theme.MUTED);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(sub);
        h.add(texts, BorderLayout.WEST);
        return h;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildHistoryPanel());
        split.setDividerLocation(380);
        split.setDividerSize(1);
        split.setBorder(null);
        return split;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(com.sales.util.Theme.CARD);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, com.sales.util.Theme.LINE));

        JLabel title = new JLabel("New Payment");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(com.sales.util.Theme.INK);
        title.setBounds(25, 20, 300, 25);

        JLabel lblOrderLbl = new JLabel("Select Order *");
        lblOrderLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblOrderLbl.setForeground(com.sales.util.Theme.INK);
        lblOrderLbl.setBounds(25, 60, 300, 18);

        cmbOrder = new JComboBox<>();
        com.sales.util.Theme.styleComboBox(cmbOrder);
        cmbOrder.setBounds(25, 82, 320, 36);
        cmbOrder.addActionListener(e -> updateOrderInfo());

        JLabel lblTotalLbl = new JLabel("Order Total:");
        lblTotalLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalLbl.setForeground(com.sales.util.Theme.INK);
        lblTotalLbl.setBounds(25, 134, 150, 18);

        lblOrderTotal = new JLabel("LKR 0.00");
        lblOrderTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblOrderTotal.setForeground(com.sales.util.Theme.INK);
        lblOrderTotal.setBounds(25, 155, 200, 25);

        JLabel lblBalanceLbl = new JLabel("Remaining Balance:");
        lblBalanceLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBalanceLbl.setForeground(com.sales.util.Theme.INK);
        lblBalanceLbl.setBounds(200, 134, 150, 18);

        lblBalance = new JLabel("LKR 0.00");
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblBalance.setForeground(com.sales.util.Theme.BAD);
        lblBalance.setBounds(200, 155, 150, 25);

        JSeparator sep = new JSeparator();
        sep.setBounds(25, 195, 320, 1);
        sep.setForeground(com.sales.util.Theme.LINE);

        JLabel lblAmtLbl = new JLabel("Payment Amount (LKR) *");
        lblAmtLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAmtLbl.setForeground(com.sales.util.Theme.INK);
        lblAmtLbl.setBounds(25, 205, 300, 18);

        txtAmount = new JTextField();
        com.sales.util.Theme.styleTextField(txtAmount);
        txtAmount.setBounds(25, 227, 320, 40);

        JLabel lblMethodLbl = new JLabel("Payment Method *");
        lblMethodLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMethodLbl.setForeground(com.sales.util.Theme.INK);
        lblMethodLbl.setBounds(25, 282, 300, 18);

        String[] methods = {"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Online Payment"};
        cmbMethod = new JComboBox<>(methods);
        com.sales.util.Theme.styleComboBox(cmbMethod);
        cmbMethod.setBounds(25, 304, 320, 36);

        JButton btnRecord = buildButton("\u2714  Record Payment", com.sales.util.Theme.PRI);
        btnRecord.setBounds(25, 365, 320, 46);
        btnRecord.addActionListener(e -> handleRecord());

        JButton btnClear = buildButton("Clear", com.sales.util.Theme.MUTED);
        btnClear.setBounds(25, 422, 320, 38);
        btnClear.addActionListener(e -> clearForm());

        panel.add(title);
        panel.add(lblOrderLbl);
        panel.add(cmbOrder);
        panel.add(lblTotalLbl);
        panel.add(lblOrderTotal);
        panel.add(lblBalanceLbl);
        panel.add(lblBalance);
        panel.add(sep);
        panel.add(lblAmtLbl);
        panel.add(txtAmount);
        panel.add(lblMethodLbl);
        panel.add(cmbMethod);
        panel.add(btnRecord);
        panel.add(btnClear);
        return panel;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(20, 15, 20, 20));

        JLabel title = new JLabel("Payment History");
        title.setFont(new Font("Georgia", Font.BOLD, 16));
        title.setForeground(com.sales.util.Theme.INK);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] cols = {"Payment #", "Order #", "Amount (LKR)", "Method", "Date"};
        paymentModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        paymentTable = new JTable(paymentModel);
        com.sales.util.Theme.styleTable(paymentTable);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        com.sales.util.Theme.styleScrollPane(scrollPane);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void handleRecord() {
        int selectedIndex = cmbOrder.getSelectedIndex();
        if (selectedIndex < 0 || allOrders == null) {
            JOptionPane.showMessageDialog(this, "Please select an order.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!Validator.isPositiveDouble(txtAmount.getText())) {
            JOptionPane.showMessageDialog(this, "Enter a valid payment amount.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double amount = Double.parseDouble(txtAmount.getText().trim());
        SalesOrder order = allOrders.get(selectedIndex);
        if (amount > order.getTotal()) {
            JOptionPane.showMessageDialog(this, "Payment amount cannot exceed order total.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Payment payment = new Payment(order.getOrderId(), amount, (String) cmbMethod.getSelectedItem());
            orderController.recordPayment(payment);
            JOptionPane.showMessageDialog(this, "Payment recorded successfully! Order marked as Paid.", "Success", JOptionPane.INFORMATION_MESSAGE);
            paymentModel.addRow(new Object[]{
                "P-" + System.currentTimeMillis() % 10000,
                "#" + order.getOrderId(),
                String.format("%.2f", amount),
                cmbMethod.getSelectedItem(),
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            });
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrderInfo() {
        int idx = cmbOrder.getSelectedIndex();
        if (idx >= 0 && allOrders != null && idx < allOrders.size()) {
            SalesOrder o = allOrders.get(idx);
            lblOrderTotal.setText(String.format("LKR %.2f", o.getTotal()));
            lblBalance.setText(String.format("LKR %.2f", o.getTotal()));
        }
    }

    private void loadOrders() {
        try {
            allOrders = orderController.getAllOrders();
            cmbOrder.removeAllItems();
            for (SalesOrder o : allOrders) {
                cmbOrder.addItem("Order #" + o.getOrderId() + "  —  " + o.getCustomerName() + "  |  LKR " + String.format("%.2f", o.getTotal()) + "  [" + o.getStatus() + "]");
            }
            updateOrderInfo();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtAmount.setText("");
        cmbOrder.setSelectedIndex(0);
        updateOrderInfo();
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
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
