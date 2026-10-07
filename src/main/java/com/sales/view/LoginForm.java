package com.sales.view;

import com.sales.dao.EmployeeDAO;
import com.sales.model.Employee;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    private static final Color PRIMARY = com.sales.util.Theme.PRI;
    private static final Color ACCENT  = new Color(205, 120, 48);
    private static final Color WHITE   = Color.WHITE;

    public LoginForm() {
        setTitle("Apex PC & Tech - Login");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(buildLeftPanel());
        root.add(buildRightPanel());
        setContentPane(root);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            private Image bgImage = loadImage("/images/login_bg.jpg");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(11, 15, 25, 235));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel overlay = new JPanel();
        overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));
        overlay.setOpaque(false);
        overlay.setBorder(new EmptyBorder(60, 40, 60, 40));

        ImageIcon logoIcon = loadScaledIcon("/images/logo.jpg", 100, 100);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Apex PC");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sales & Order Management");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(com.sales.util.Theme.MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='text-align:center;'>Manage orders, inventory &amp;<br>reports with elegance.</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(com.sales.util.Theme.MUTED);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        tagline.setHorizontalAlignment(SwingConstants.CENTER);

        overlay.add(Box.createVerticalGlue());
        overlay.add(logoLabel);
        overlay.add(Box.createVerticalStrut(20));
        overlay.add(title);
        overlay.add(subtitle);
        overlay.add(Box.createVerticalStrut(20));
        overlay.add(tagline);
        overlay.add(Box.createVerticalGlue());

        panel.add(overlay, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(com.sales.util.Theme.BG);

        JLabel headerLabel = new JLabel("Welcome Back");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        headerLabel.setForeground(com.sales.util.Theme.INK);
        headerLabel.setBounds(60, 95, 300, 40);

        JLabel subHeader = new JLabel("Sign in to your account");
        subHeader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeader.setForeground(com.sales.util.Theme.MUTED);
        subHeader.setBounds(60, 140, 300, 25);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(com.sales.util.Theme.INK);
        lblUser.setBounds(60, 205, 200, 20);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(60, 230, 310, 42);
        txtUsername.setBackground(com.sales.util.Theme.CARD);
        txtUsername.setForeground(com.sales.util.Theme.INK);
        txtUsername.setCaretColor(com.sales.util.Theme.INK);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(com.sales.util.Theme.LINE, 1, true),
            new EmptyBorder(5, 12, 5, 12)
        ));

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(com.sales.util.Theme.INK);
        lblPass.setBounds(60, 290, 200, 20);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(60, 315, 310, 42);
        txtPassword.setBackground(com.sales.util.Theme.CARD);
        txtPassword.setForeground(com.sales.util.Theme.INK);
        txtPassword.setCaretColor(com.sales.util.Theme.INK);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(com.sales.util.Theme.LINE, 1, true),
            new EmptyBorder(5, 12, 5, 12)
        ));

        lblError = new JLabel("");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(com.sales.util.Theme.BAD);
        lblError.setBounds(60, 365, 310, 20);

        btnLogin = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(com.sales.util.Theme.PRI.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(com.sales.util.Theme.PRI_HOVER);
                } else {
                    g2.setColor(com.sales.util.Theme.PRI);
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btnLogin.setBounds(60, 395, 310, 46);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(e -> handleLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) handleLogin();
            }
        });

        JLabel demoHint = new JLabel("Users: admin (admin123) | cashier (cashier123)");
        demoHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        demoHint.setForeground(com.sales.util.Theme.MUTED);
        demoHint.setBounds(60, 452, 310, 20);

        JLabel footer = new JLabel("© 2026 Apex PC & Tech | NIBM EAD");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(com.sales.util.Theme.MUTED);
        footer.setBounds(60, 480, 310, 20);

        panel.add(headerLabel);
        panel.add(subHeader);
        panel.add(lblUser);
        panel.add(txtUsername);
        panel.add(lblPass);
        panel.add(txtPassword);
        panel.add(lblError);
        panel.add(btnLogin);
        panel.add(demoHint);
        panel.add(footer);
        return panel;
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            lblError.setText("Please enter username and password.");
            return;
        }
        try {
            EmployeeDAO dao = new EmployeeDAO();
            Employee emp = dao.authenticate(user, pass);
            if (emp != null) {
                dispose();
                new DashboardForm(emp).setVisible(true);
            } else {
                lblError.setText("Invalid username or password.");
                txtPassword.setText("");
            }
        } catch (SQLException ex) {
            lblError.setText("Database error: " + ex.getMessage());
        }
    }

    private Image loadImage(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) return new ImageIcon(url).getImage();
        } catch (Exception ignored) {}
        return null;
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
