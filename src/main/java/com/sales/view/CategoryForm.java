package com.sales.view;

import com.sales.controller.ProductController;
import com.sales.model.Category;
import com.sales.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class CategoryForm extends JFrame {

    private final Employee currentUser;
    private final ProductController controller = new ProductController();

    private JTextField txtName, txtDescription;
    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedId = -1;

    private static final Color PRIMARY = com.sales.util.Theme.PRI;
    private static final Color WHITE   = Color.WHITE;

    public CategoryForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC & Tech — Category Management");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        initUI();
        loadTable();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(com.sales.util.Theme.CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, com.sales.util.Theme.LINE),
            new EmptyBorder(16, 25, 16, 25)
        ));
        JLabel title = new JLabel("  Product Categories", com.sales.util.IconFactory.getProductsIcon(22, com.sales.util.Theme.PRI), JLabel.LEFT);
        title.setFont(new Font("Georgia", Font.BOLD, 20));
        title.setForeground(com.sales.util.Theme.INK);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildFormPanel(), buildTablePanel());
        split.setDividerLocation(320);
        split.setDividerSize(1);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(com.sales.util.Theme.CARD);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, com.sales.util.Theme.LINE));

        JLabel lbl1 = new JLabel("Category Name *");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl1.setForeground(com.sales.util.Theme.INK);
        lbl1.setBounds(25, 30, 250, 18);
        panel.add(lbl1);

        txtName = new JTextField();
        com.sales.util.Theme.styleTextField(txtName);
        txtName.setBounds(25, 52, 265, 36);
        panel.add(txtName);

        JLabel lbl2 = new JLabel("Description");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl2.setForeground(com.sales.util.Theme.INK);
        lbl2.setBounds(25, 102, 250, 18);
        panel.add(lbl2);

        txtDescription = new JTextField();
        com.sales.util.Theme.styleTextField(txtDescription);
        txtDescription.setBounds(25, 124, 265, 36);
        panel.add(txtDescription);

        JButton btnAdd = buildButton("Add", com.sales.util.Theme.PRI);
        btnAdd.setBounds(25, 185, 120, 38);
        btnAdd.addActionListener(e -> {
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                controller.addCategory(new Category(0, txtName.getText().trim(), txtDescription.getText().trim()));
                clearForm(); loadTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnUpdate = buildButton("Update", com.sales.util.Theme.PRI_HOVER);
        btnUpdate.setBounds(155, 185, 135, 38);
        btnUpdate.addActionListener(e -> {
            if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a category.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            try {
                controller.updateCategory(new Category(selectedId, txtName.getText().trim(), txtDescription.getText().trim()));
                clearForm(); loadTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnDelete = buildButton("Delete", com.sales.util.Theme.BAD);
        btnDelete.setBounds(25, 235, 265, 38);
        btnDelete.addActionListener(e -> {
            if (selectedId < 0) { JOptionPane.showMessageDialog(this, "Select a category.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this category?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try { controller.deleteCategory(selectedId); clearForm(); loadTable(); }
                catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
            }
        });

        JButton btnClear = buildButton("Clear Form", com.sales.util.Theme.MUTED);
        btnClear.setBounds(25, 285, 265, 38);
        btnClear.addActionListener(e -> clearForm());

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnClear);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(com.sales.util.Theme.BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] cols = {"ID", "Category Name", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        com.sales.util.Theme.styleTable(table);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtDescription.setText(tableModel.getValueAt(row, 2).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        com.sales.util.Theme.styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            for (Category c : controller.getAllCategories()) {
                tableModel.addRow(new Object[]{c.getCategoryId(), c.getName(), c.getDescription()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtName.setText(""); txtDescription.setText(""); selectedId = -1; table.clearSelection();
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
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
