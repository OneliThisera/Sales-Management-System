package com.sales.view;

import com.sales.controller.ProductController;
import com.sales.model.Category;
import com.sales.model.Employee;
import com.sales.model.Product;
import com.sales.util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.List;

public class ProductForm extends JFrame {

    private final Employee currentUser;
    private final ProductController controller = new ProductController();

    private JTextField txtName, txtDescription, txtPrice, txtStock;
    private JComboBox<Category> cmbCategory;
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedId = -1;

    private static final Color PRIMARY = com.sales.util.Theme.PRI;
    private static final Color WHITE   = Color.WHITE;
    private static final Color BG      = com.sales.util.Theme.BG;

    private JPanel viewPanel;

    public ProductForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC & Tech - Hardware & Product Catalog");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(getView());
    }

    public JPanel getView() {
        if (viewPanel == null) {
            viewPanel = new JPanel(new BorderLayout());
            viewPanel.add(buildHeader(), BorderLayout.NORTH);
            viewPanel.add(buildCenter(), BorderLayout.CENTER);
            loadCategories();
            loadTable();
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

        ImageIcon icon = loadScaledIcon("/images/products_banner.jpg", 55, 42);
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setBorder(new EmptyBorder(0, 0, 0, 15));

        JLabel title = new JLabel("  Hardware & Components Catalog", com.sales.util.IconFactory.getProductsIcon(22, com.sales.util.Theme.PRI), JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(com.sales.util.Theme.INK);

        JLabel sub = new JLabel("Manage computer hardware, components, pricing & stock availability");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(com.sales.util.Theme.MUTED);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setOpaque(false);
        texts.add(title);
        texts.add(Box.createVerticalStrut(4));
        texts.add(sub);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(imgLabel);
        left.add(texts);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    private JSplitPane buildCenter() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
        split.setDividerLocation(360);
        split.setDividerSize(1);
        split.setBorder(null);
        return split;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(com.sales.util.Theme.CARD);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, com.sales.util.Theme.LINE));

        JLabel formTitle = new JLabel("Product Details");
        formTitle.setFont(new Font("Georgia", Font.BOLD, 16));
        formTitle.setForeground(com.sales.util.Theme.INK);
        formTitle.setBounds(25, 20, 280, 25);
        panel.add(formTitle);

        txtName = createField(panel, "Product Name *", 60);
        txtDescription = createField(panel, "Description", 130);
        txtPrice = createField(panel, "Unit Price (Rs.) *", 200);
        txtStock = createField(panel, "Stock Quantity *", 270);

        JLabel lblCat = new JLabel("Category *");
        lblCat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCat.setForeground(com.sales.util.Theme.INK);
        lblCat.setBounds(25, 340, 280, 18);
        panel.add(lblCat);

        cmbCategory = new JComboBox<>();
        com.sales.util.Theme.styleComboBox(cmbCategory);
        cmbCategory.setBounds(25, 362, 300, 36);
        panel.add(cmbCategory);

        JButton btnSave = buildButton("Add Product", com.sales.util.Theme.PRI);
        btnSave.setBounds(25, 420, 140, 40);
        btnSave.addActionListener(e -> handleSave());

        JButton btnUpdate = buildButton("Update", com.sales.util.Theme.PRI_HOVER);
        btnUpdate.setBounds(175, 420, 150, 40);
        btnUpdate.addActionListener(e -> handleUpdate());

        JButton btnClear = buildButton("Clear", com.sales.util.Theme.MUTED);
        btnClear.setBounds(25, 470, 140, 40);
        btnClear.addActionListener(e -> clearForm());

        JButton btnDelete = buildButton("Delete", com.sales.util.Theme.BAD);
        btnDelete.setBounds(175, 470, 150, 40);
        btnDelete.addActionListener(e -> handleDelete());

        panel.add(btnSave);
        panel.add(btnUpdate);
        panel.add(btnClear);
        panel.add(btnDelete);

        if (currentUser.isCashier()) {
            btnSave.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
            JLabel cashierNote = new JLabel("Cashier Mode: Catalog View Only");
            cashierNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            cashierNote.setForeground(com.sales.util.Theme.MUTED);
            cashierNote.setBounds(25, 520, 280, 20);
            panel.add(cashierNote);
        }
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Name", "Description", "Price (LKR)", "Stock", "Category"};
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
                txtDescription.setText(tableModel.getValueAt(row, 2).toString());
                txtPrice.setText(tableModel.getValueAt(row, 3).toString());
                txtStock.setText(tableModel.getValueAt(row, 4).toString());
            }
        });

        JPanel topBar = new JPanel(new BorderLayout(10, 8));
        topBar.setOpaque(false);

        JTextField txtSearch = new JTextField();
        com.sales.util.Theme.styleTextField(txtSearch);
        txtSearch.putClientProperty("JTextField.placeholderText", "Search PC components, laptops & accessories...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadTable(txtSearch.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadTable(txtSearch.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadTable(txtSearch.getText()); }
        });

        JLabel warningLabel = new JLabel("  Items with stock \u2264 10 need restocking", com.sales.util.IconFactory.getAlertIcon(14, com.sales.util.Theme.WARN), JLabel.LEFT);
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        warningLabel.setForeground(com.sales.util.Theme.WARN);

        topBar.add(txtSearch, BorderLayout.CENTER);
        topBar.add(warningLabel, BorderLayout.SOUTH);

        JScrollPane tableScroll = new JScrollPane(table);
        com.sales.util.Theme.styleScrollPane(tableScroll);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        return panel;
    }

    private void handleSave() {
        if (!validateForm()) return;
        try {
            Product p = new Product(0, txtName.getText().trim(), txtDescription.getText().trim(),
                Double.parseDouble(txtPrice.getText().trim()), Integer.parseInt(txtStock.getText().trim()),
                ((Category) cmbCategory.getSelectedItem()).getCategoryId());
            controller.add(p);
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        try {
            Product p = new Product(selectedId, txtName.getText().trim(), txtDescription.getText().trim(),
                Double.parseDouble(txtPrice.getText().trim()), Integer.parseInt(txtStock.getText().trim()),
                ((Category) cmbCategory.getSelectedItem()).getCategoryId());
            controller.update(p);
            JOptionPane.showMessageDialog(this, "Product updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (selectedId < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.delete(selectedId);
                clearForm();
                loadTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (!Validator.isNotEmpty(txtName.getText())) {
            JOptionPane.showMessageDialog(this, "Product name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isPositiveDouble(txtPrice.getText())) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive price.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!Validator.isNonNegativeInt(txtStock.getText())) {
            JOptionPane.showMessageDialog(this, "Stock quantity must be a non-negative integer.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (cmbCategory.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a category.", "Validation", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void loadCategories() {
        try {
            cmbCategory.removeAllItems();
            for (Category c : controller.getAllCategories()) {
                cmbCategory.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading categories.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTable() {
        loadTable("");
    }

    private void loadTable(String query) {
        try {
            tableModel.setRowCount(0);
            String q = query == null ? "" : query.toLowerCase().trim();
            for (Product p : controller.getAll()) {
                if (q.isEmpty() || p.getName().toLowerCase().contains(q) ||
                    (p.getCategoryName() != null && p.getCategoryName().toLowerCase().contains(q)) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(q))) {
                    tableModel.addRow(new Object[]{
                        p.getProductId(), p.getName(), p.getDescription(),
                        String.format("%.2f", p.getPrice()), p.getStockQty(), p.getCategoryName()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading products.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        selectedId = -1;
        table.clearSelection();
    }

    private JTextField createField(JPanel panel, String label, int y) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(com.sales.util.Theme.INK);
        lbl.setBounds(25, y, 300, 18);
        panel.add(lbl);
        JTextField field = new JTextField();
        com.sales.util.Theme.styleTextField(field);
        field.setBounds(25, y + 22, 300, 36);
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

    private ImageIcon loadScaledIcon(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception ignored) {}
        return new ImageIcon();
    }
}
