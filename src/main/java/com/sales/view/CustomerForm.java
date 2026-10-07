package com.sales.view;

import com.sales.controller.CustomerController;
import com.sales.exception.DuplicateRecordException;
import com.sales.model.Customer;
import com.sales.model.Employee;
import com.sales.util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

public class CustomerForm extends JFrame {

    private final Employee currentUser;
    private final CustomerController controller = new CustomerController();

    private JTextField txtName, txtEmail, txtPhone, txtAddress, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedId = -1;

    private static final Color PRIMARY = com.sales.util.Theme.PRI;
    private static final Color WHITE   = Color.WHITE;
    private static final Color BG      = com.sales.util.Theme.BG;

    public CustomerForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC & Tech — Customer Management");
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(getView());
        loadTable("");
    }

    public JPanel getView() {
        JPanel view = new JPanel(new BorderLayout());
        view.add(buildHeader(), BorderLayout.NORTH);
        view.add(buildCenter(), BorderLayout.CENTER);
        return view;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(com.sales.util.Theme.CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, com.sales.util.Theme.LINE),
            new EmptyBorder(16, 25, 16, 25)
        ));

        JLabel title = new JLabel("  Customer Directory", com.sales.util.IconFactory.getCustomersIcon(22, com.sales.util.Theme.PRI), JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(com.sales.util.Theme.INK);

        JLabel sub = new JLabel("Add, search and maintain customer records");
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

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
        split.setDividerLocation(340);
        split.setDividerSize(1);
        split.setBorder(null);
        return split;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(com.sales.util.Theme.CARD);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, com.sales.util.Theme.LINE));
        panel.setPreferredSize(new Dimension(340, 0));

        JLabel formTitle = new JLabel("Customer Details");
        formTitle.setFont(new Font("Georgia", Font.BOLD, 16));
        formTitle.setForeground(com.sales.util.Theme.INK);
        formTitle.setBounds(25, 20, 250, 25);

        txtName = createField(panel, "Full Name *", 60);
        txtEmail = createField(panel, "Email Address *", 130);
        txtPhone = createField(panel, "Phone Number *", 200);
        txtAddress = createField(panel, "Address", 270);

        JButton btnSave = buildButton("Save Customer", com.sales.util.Theme.PRI);
        btnSave.setBounds(25, 355, 140, 40);
        btnSave.addActionListener(e -> handleSave());

        JButton btnUpdate = buildButton("Update", com.sales.util.Theme.PRI_HOVER);
        btnUpdate.setBounds(175, 355, 130, 40);
        btnUpdate.addActionListener(e -> handleUpdate());

        JButton btnClear = buildButton("Clear Form", com.sales.util.Theme.MUTED);
        btnClear.setBounds(25, 405, 140, 40);
        btnClear.addActionListener(e -> clearForm());

        JButton btnDelete = buildButton("Delete", com.sales.util.Theme.BAD);
        btnDelete.setBounds(175, 405, 130, 40);
        btnDelete.addActionListener(e -> handleDelete());

        if (currentUser != null && currentUser.isCashier()) {
            btnDelete.setEnabled(false);
            btnDelete.setToolTipText("Cashiers cannot delete customer records");
        }

        panel.add(formTitle);
        panel.add(btnSave);
        panel.add(btnUpdate);
        panel.add(btnClear);
        panel.add(btnDelete);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        searchBar.setBorder(new EmptyBorder(0, 0, 12, 0));

        txtSearch = new JTextField();
        com.sales.util.Theme.styleTextField(txtSearch);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search customers...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadTable(txtSearch.getText().trim()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadTable(txtSearch.getText().trim()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadTable(txtSearch.getText().trim()); }
        });

        JButton btnSearch = buildButton("Search", PRIMARY);
        btnSearch.addActionListener(e -> loadTable(txtSearch.getText().trim()));
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(btnSearch, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtEmail.setText(tableModel.getValueAt(row, 2).toString());
                txtPhone.setText(tableModel.getValueAt(row, 3).toString());
                txtAddress.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        JScrollPane customerScroll = new JScrollPane(table);
        com.sales.util.Theme.styleScrollPane(customerScroll);

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(customerScroll, BorderLayout.CENTER);
        return panel;
    }

    private void handleSave() {
        if (!validateForm()) return;
        try {
            Customer c = new Customer(0, txtName.getText().trim(), txtEmail.getText().trim(),
                txtPhone.getText().trim(), txtAddress.getText().trim());
            controller.add(c);
            JOptionPane.showMessageDialog(this, "Customer saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable("");
        } catch (DuplicateRecordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        try {
            Customer c = new Customer(selectedId, txtName.getText().trim(), txtEmail.getText().trim(),
                txtPhone.getText().trim(), txtAddress.getText().trim());
            controller.update(c);
            JOptionPane.showMessageDialog(this, "Customer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable("");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (currentUser != null && currentUser.isCashier()) {
            JOptionPane.showMessageDialog(this, "Access Denied: Cashiers are not authorized to delete customer records.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.delete(selectedId);
                JOptionPane.showMessageDialog(this, "Customer deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadTable("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (!Validator.isNotEmpty(txtName.getText())) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isNotEmpty(txtEmail.getText()) || !Validator.isValidEmail(txtEmail.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isNotEmpty(txtPhone.getText()) || !Validator.isValidPhone(txtPhone.getText())) {
            JOptionPane.showMessageDialog(this, "Phone must be 10 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void loadTable(String search) {
        try {
            tableModel.setRowCount(0);
            List<Customer> list = search.isEmpty() ? controller.getAll() : controller.search(search);
            for (Customer c : list) {
                tableModel.addRow(new Object[]{c.getCustomerId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    private JTextField createField(JPanel panel, String label, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(com.sales.util.Theme.INK);
        lbl.setBounds(25, y, 280, 18);
        panel.add(lbl);

        JTextField field = new JTextField();
        com.sales.util.Theme.styleTextField(field);
        field.setBounds(25, y + 22, 290, 36);
        panel.add(field);
        return field;
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
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
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
