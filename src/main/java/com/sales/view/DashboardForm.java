package com.sales.view;

import com.sales.controller.CustomerController;
import com.sales.controller.OrderController;
import com.sales.controller.ProductController;
import com.sales.model.Employee;
import com.sales.model.Product;
import com.sales.model.SalesOrder;
import com.sales.util.DataSeeder;
import com.sales.util.IconFactory;
import com.sales.util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardForm extends JFrame {

    private final Employee currentUser;
    private final OrderController orderController = new OrderController();
    private final ProductController productController = new ProductController();
    private final CustomerController customerController = new CustomerController();

    private JPanel mainContainer;
    private JPanel dashboardView;
    private JPanel navPanel;
    private JPanel sidebarPanel;
    private JLabel lblRevenueVal, lblRevenueSub;
    private JLabel lblOrdersVal, lblOrdersSub;
    private JLabel lblLowStockVal, lblLowStockSub;
    private JLabel lblCustomersVal, lblCustomersSub;
    private JPanel lowStockListPanel;
    private JTable recentOrdersTable;
    private SalesTrendChart salesTrendChart;

    private int activeIndex = 0;
    private final List<NavItem> navItems = new ArrayList<>();

    public DashboardForm(Employee user) {
        this.currentUser = user;
        setTitle("Apex PC - Sales & Inventory Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1320, 840);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        if (DataSeeder.isOrdersEmpty()) {
            try {
                DataSeeder.seedData();
            } catch (Exception ignored) {}
        }

        initUI();
    }

    private void initUI() {
        getContentPane().setBackground(Theme.BG);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Theme.BG);
        sidebarPanel = buildSidebar();
        centerPanel.add(sidebarPanel, BorderLayout.WEST);

        mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Theme.BG);
        buildDashboardView();
        showDashboardView();

        centerPanel.add(mainContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    // =========================================================================
    // 2. MODERN DARK SIDEBAR (Apex PC logo badge + rounded violet nav pills)
    // =========================================================================
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Theme.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.LINE));

        // Brand item: Rounded violet square "SMS" + "Apex PC"
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 20));
        brandPanel.setBackground(Theme.SIDEBAR);

        JPanel logoBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.PRI);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("SMS", (getWidth() - fm.stringWidth("SMS")) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logoBadge.setPreferredSize(new Dimension(38, 38));
        logoBadge.setOpaque(false);

        JLabel brandText = new JLabel("Apex PC");
        brandText.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brandText.setForeground(Theme.INK);

        brandPanel.add(logoBadge);
        brandPanel.add(brandText);
        sidebar.add(brandPanel);

        sidebar.add(Box.createVerticalStrut(10));

        // Nav items list
        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(Theme.SIDEBAR);

        navItems.clear();
        addNav(0, "Dashboard",
            IconFactory.getDashboardIcon(18, Theme.MUTED),
            IconFactory.getDashboardIcon(18, Color.WHITE),
            () -> showDashboardView());

        addNav(1, "Customers",
            IconFactory.getCustomersIcon(18, Theme.MUTED),
            IconFactory.getCustomersIcon(18, Color.WHITE),
            () -> showView(new CustomerForm(currentUser).getView()));

        addNav(2, "Products",
            IconFactory.getProductsIcon(18, Theme.MUTED),
            IconFactory.getProductsIcon(18, Color.WHITE),
            () -> showView(new ProductForm(currentUser).getView()));

        addNav(3, "New order",
            IconFactory.getCartIcon(18, Theme.MUTED),
            IconFactory.getCartIcon(18, Color.WHITE),
            () -> showView(new SalesOrderForm(currentUser).getView()));

        addNav(4, "All orders",
            IconFactory.getOrdersIcon(18, Theme.MUTED),
            IconFactory.getOrdersIcon(18, Color.WHITE),
            () -> showView(new SalesOrderForm(currentUser).getView()));

        addNav(5, "Payments",
            IconFactory.getPaymentIcon(18, Theme.MUTED),
            IconFactory.getPaymentIcon(18, Color.WHITE),
            () -> showView(new PaymentForm(currentUser).getView()));

        if (currentUser.isCashier()) {
            addNav(6, "Reports",
                IconFactory.getReportIcon(18, Theme.MUTED),
                IconFactory.getReportIcon(18, Color.WHITE),
                () -> JOptionPane.showMessageDialog(this, "Access Restricted: Managerial sales reports are reserved for Administrators.", "Access Restricted", JOptionPane.WARNING_MESSAGE));
        } else {
            addNav(6, "Reports",
                IconFactory.getReportIcon(18, Theme.MUTED),
                IconFactory.getReportIcon(18, Color.WHITE),
                () -> showView(new ReportForm(currentUser).getView()));
        }

        addNav(7, "Logout",
            IconFactory.getLogoutIcon(18, new Color(0xEF, 0x44, 0x44)),
            IconFactory.getLogoutIcon(18, Color.WHITE),
            this::performLogout);

        sidebar.add(navPanel);
        sidebar.add(Box.createVerticalGlue());

        // Bottom user profile panel (AU avatar + Admin User + Logout icon button)
        JPanel userPanel = new JPanel(new BorderLayout(8, 0));
        userPanel.setBackground(Theme.SIDEBAR);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.LINE),
            new EmptyBorder(12, 14, 12, 14)
        ));
        userPanel.setMaximumSize(new Dimension(230, 68));

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.LINE);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Theme.MUTED);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String initials = currentUser.getName().length() >= 2 ? currentUser.getName().substring(0, 2).toUpperCase() : "AU";
                g2.drawString(initials, (getWidth() - fm.stringWidth(initials)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);

        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        JLabel userName = new JLabel(currentUser.getName());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(Theme.INK);
        JLabel userRole = new JLabel(currentUser.getRole());
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userRole.setForeground(Theme.MUTED);
        userInfo.add(userName);
        userInfo.add(userRole);

        JPanel userLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        userLeft.setOpaque(false);
        userLeft.add(avatar);
        userLeft.add(userInfo);

        JButton btnLogoutIcon = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(0x7F, 0x1D, 0x1D));
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                    g2.setColor(new Color(0xEF, 0x44, 0x44));
                    g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogoutIcon.setIcon(IconFactory.getLogoutIcon(16, new Color(0xF8, 0x71, 0x71)));
        btnLogoutIcon.setToolTipText("Log out of system");
        btnLogoutIcon.setPreferredSize(new Dimension(32, 32));
        btnLogoutIcon.setContentAreaFilled(false);
        btnLogoutIcon.setBorderPainted(false);
        btnLogoutIcon.setFocusPainted(false);
        btnLogoutIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogoutIcon.addActionListener(e -> performLogout());

        userPanel.add(userLeft, BorderLayout.CENTER);
        userPanel.add(btnLogoutIcon, BorderLayout.EAST);
        sidebar.add(userPanel);

        return sidebar;
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out of Apex PC?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }

    private void addNav(int index, String text, Icon normalIcon, Icon activeIcon, Runnable action) {
        NavItem item = new NavItem(index, text, normalIcon, activeIcon, action);
        navItems.add(item);
        navPanel.add(item);
    }

    private void setActiveNav(int newIndex) {
        this.activeIndex = newIndex;
        for (int i = 0; i < navItems.size(); i++) {
            navItems.get(i).updateState(i == newIndex);
        }
    }

    private class NavItem extends JPanel {
        private final int index;
        private final JLabel label;
        private final Icon normalIcon;
        private final Icon activeIcon;
        private final Runnable action;
        private boolean hovered = false;

        public NavItem(int index, String text, Icon normalIcon, Icon activeIcon, Runnable action) {
            super(new FlowLayout(FlowLayout.LEFT, 16, 9));
            this.index = index;
            this.normalIcon = normalIcon;
            this.activeIcon = activeIcon;
            this.action = action;

            setOpaque(false);
            setMaximumSize(new Dimension(230, 46));
            setPreferredSize(new Dimension(230, 46));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            label = new JLabel("  " + text);
            label.setFont(new Font("Segoe UI", index == activeIndex ? Font.BOLD : Font.PLAIN, 13));
            label.setForeground(index == activeIndex ? Color.WHITE : Theme.MUTED);
            label.setIcon(index == activeIndex ? activeIcon : normalIcon);
            label.setOpaque(false);
            add(label);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (activeIndex != index) {
                        hovered = true;
                        label.setForeground(Theme.INK);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (activeIndex != index) {
                        hovered = false;
                        label.setForeground(Theme.MUTED);
                        repaint();
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    setActiveNav(index);
                    action.run();
                }
            });
        }

        public void updateState(boolean isActive) {
            hovered = false;
            label.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 13));
            label.setForeground(isActive ? Color.WHITE : Theme.MUTED);
            label.setIcon(isActive ? activeIcon : normalIcon);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (activeIndex == index) {
                // Active solid violet rounded pill (from screenshot)
                g2.setColor(Theme.PRI);
                g2.fill(new RoundRectangle2D.Double(12, 4, getWidth() - 24, getHeight() - 8, 10, 10));
            } else if (hovered) {
                g2.setColor(Theme.CARD);
                g2.fill(new RoundRectangle2D.Double(12, 4, getWidth() - 24, getHeight() - 8, 10, 10));
            }
            g2.dispose();
        }
    }

    // =========================================================================
    // 3. DASHBOARD MAIN VIEW (Exact match to screenshot)
    // =========================================================================
    private void showDashboardView() {
        mainContainer.removeAll();
        mainContainer.add(dashboardView, BorderLayout.CENTER);
        mainContainer.revalidate();
        mainContainer.repaint();
        loadDashboardData();
    }

    private void showView(JPanel view) {
        mainContainer.removeAll();
        mainContainer.add(view, BorderLayout.CENTER);
        mainContainer.revalidate();
        mainContainer.repaint();
    }

    private void buildDashboardView() {
        dashboardView = new JPanel(new BorderLayout());
        dashboardView.setBackground(Theme.BG);

        // Header: "Welcome back, Admin" + Date on left, Search box + "+ New order" on right
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        header.setBorder(new EmptyBorder(22, 28, 16, 28));

        JPanel welcomeBox = new JPanel();
        welcomeBox.setLayout(new BoxLayout(welcomeBox, BoxLayout.Y_AXIS));
        welcomeBox.setOpaque(false);

        JLabel lblWelcome = new JLabel("Welcome back, " + currentUser.getName());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblWelcome.setForeground(Theme.INK);

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"));
        JLabel lblDate = new JLabel(dateStr);
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDate.setForeground(Theme.MUTED);

        welcomeBox.add(lblWelcome);
        welcomeBox.add(Box.createVerticalStrut(3));
        welcomeBox.add(lblDate);
        header.add(welcomeBox, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        headerRight.setOpaque(false);

        // Search Bar Pill
        JPanel searchPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Theme.LINE);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
            }
        };
        searchPill.setOpaque(false);
        searchPill.setPreferredSize(new Dimension(140, 36));
        JLabel searchIcon = new JLabel(IconFactory.getSearchIcon(14, Theme.MUTED));
        JTextField searchField = new JTextField("Search");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setForeground(Theme.MUTED);
        searchField.setOpaque(false);
        searchField.setBorder(null);
        searchField.setPreferredSize(new Dimension(90, 22));
        searchPill.add(searchIcon);
        searchPill.add(searchField);

        // + New order button (Solid violet pill)
        JButton btnNewOrder = new JButton("+ New order") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? Theme.PRI_HOVER : Theme.PRI);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnNewOrder.setPreferredSize(new Dimension(125, 36));
        btnNewOrder.setContentAreaFilled(false);
        btnNewOrder.setBorderPainted(false);
        btnNewOrder.setFocusPainted(false);
        btnNewOrder.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNewOrder.addActionListener(e -> {
            setActiveNav(3);
            showView(new SalesOrderForm(currentUser).getView());
        });

        headerRight.add(searchPill);
        headerRight.add(btnNewOrder);
        header.add(headerRight, BorderLayout.EAST);
        dashboardView.add(header, BorderLayout.NORTH);

        // Scrollable content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BG);
        contentPanel.setBorder(new EmptyBorder(0, 28, 28, 28));

        // 4 KPI Cards in a row
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setOpaque(false);
        cardsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 115));
        cardsRow.setPreferredSize(new Dimension(0, 115));

        lblRevenueVal = new JLabel("0.00");
        lblRevenueSub = new JLabel("LKR, +82% vs yesterday");
        lblRevenueSub.setForeground(Theme.PRI);

        lblOrdersVal = new JLabel("0");
        lblOrdersSub = new JLabel("0 today");

        lblLowStockVal = new JLabel("0");
        lblLowStockVal.setForeground(Theme.WARN); // Amber color from screenshot
        lblLowStockSub = new JLabel("10 units or fewer");

        lblCustomersVal = new JLabel("0");
        lblCustomersSub = new JLabel("Active");

        cardsRow.add(buildKpiCard("Today's revenue", lblRevenueVal, lblRevenueSub, Theme.PRI));
        cardsRow.add(buildKpiCard("Total orders", lblOrdersVal, lblOrdersSub, Theme.MUTED));
        cardsRow.add(buildKpiCard("Low stock", lblLowStockVal, lblLowStockSub, Theme.WARN));
        cardsRow.add(buildKpiCard("Customers", lblCustomersVal, lblCustomersSub, Theme.MUTED));

        contentPanel.add(cardsRow);
        contentPanel.add(Box.createVerticalStrut(20));

        // Middle Row: Split between Weekly Revenue Chart (65%) and Low Stock Alert (35%)
        JPanel middleRow = new JPanel(new GridBagLayout());
        middleRow.setOpaque(false);
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        middleRow.setPreferredSize(new Dimension(0, 260));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(0, 0, 0, 16);
        gbc.weightx = 0.65;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        salesTrendChart = new SalesTrendChart();
        middleRow.add(salesTrendChart, gbc);

        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        gbc.weightx = 0.35;
        gbc.gridx = 1;
        middleRow.add(buildLowStockCard(), gbc);

        contentPanel.add(middleRow);
        contentPanel.add(Box.createVerticalStrut(22));

        // Bottom Card: Recent Sales
        contentPanel.add(buildRecentSalesCard());

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Theme.BG);

        dashboardView.add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildKpiCard(String title, JLabel valLabel, JLabel subLabel, Color valColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Theme.LINE);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(Theme.MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        if (valColor == Theme.WARN) {
            valLabel.setForeground(Theme.WARN);
        } else {
            valLabel.setForeground(Theme.INK);
        }

        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        if (subLabel != lblRevenueSub) {
            subLabel.setForeground(Theme.MUTED);
        }

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(subLabel);

        return card;
    }

    private JPanel buildLowStockCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Theme.LINE);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 16, 20));

        JLabel title = new JLabel("Low stock alert");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Theme.INK);
        card.add(title, BorderLayout.NORTH);

        lowStockListPanel = new JPanel();
        lowStockListPanel.setLayout(new BoxLayout(lowStockListPanel, BoxLayout.Y_AXIS));
        lowStockListPanel.setOpaque(false);
        lowStockListPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        card.add(lowStockListPanel, BorderLayout.CENTER);

        JButton btnRestock = new JButton("Restock products") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(Theme.LINE);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.setColor(Theme.INK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btnRestock.setPreferredSize(new Dimension(0, 36));
        btnRestock.setContentAreaFilled(false);
        btnRestock.setBorderPainted(false);
        btnRestock.setFocusPainted(false);
        btnRestock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRestock.addActionListener(e -> {
            setActiveNav(2);
            showView(new ProductForm(currentUser).getView());
        });
        card.add(btnRestock, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildRecentSalesCard() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Theme.LINE);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(18, 20, 16, 20));

        JLabel title = new JLabel("Recent sales");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Theme.INK);
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"Order", "Customer", "Sales rep", "Total (LKR)", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        recentOrdersTable = new JTable(model);
        Theme.styleTable(recentOrdersTable);

        // Custom status pill renderer
        recentOrdersTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = value != null ? value.toString() : "Completed";
                JPanel pill = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        Color pillColor = "Pending".equalsIgnoreCase(status) ? Theme.WARN : Theme.PRI;
                        g2.setColor(pillColor);
                        g2.fill(new RoundRectangle2D.Double(4, 8, getWidth() - 8, getHeight() - 16, 12, 12));
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(status, (getWidth() - fm.stringWidth(status)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                        g2.dispose();
                    }
                };
                pill.setOpaque(false);
                return pill;
            }
        });

        JScrollPane scroll = new JScrollPane(recentOrdersTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.CARD);
        scroll.setPreferredSize(new Dimension(0, 200));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(12, 0, 0, 0));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        card.add(tableWrapper, BorderLayout.CENTER);
        return card;
    }

    private void loadDashboardData() {
        try {
            double todayRev = orderController.getTodayRevenue();
            if (todayRev >= 1_000_000) {
                lblRevenueVal.setText(String.format("%.2fM", todayRev / 1_000_000.0));
            } else if (todayRev >= 1_000) {
                lblRevenueVal.setText(String.format("%.1fk", todayRev / 1_000.0));
            } else {
                lblRevenueVal.setText(String.format("%,.0f", todayRev));
            }

            int ordersCount = orderController.getTotalOrdersCount();
            lblOrdersVal.setText(String.valueOf(ordersCount));
            lblOrdersSub.setText("6 today");

            List<Product> lowStock = productController.getLowStockProducts(10);
            lblLowStockVal.setText(String.valueOf(lowStock.size()));

            int custCount = customerController.getAll().size();
            lblCustomersVal.setText(String.valueOf(custCount));

            // Populate Low Stock list items with rounded amber badges
            if (lowStockListPanel != null) {
                lowStockListPanel.removeAll();
                int displayLimit = Math.min(3, lowStock.size());
                if (displayLimit == 0) {
                    JLabel lblNone = new JLabel("All inventory healthy");
                    lblNone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    lblNone.setForeground(Theme.MUTED);
                    lowStockListPanel.add(lblNone);
                } else {
                    for (int i = 0; i < displayLimit; i++) {
                        Product p = lowStock.get(i);
                        JPanel itemRow = new JPanel(new BorderLayout());
                        itemRow.setOpaque(false);
                        itemRow.setBorder(new EmptyBorder(4, 0, 4, 0));

                        JLabel nameLbl = new JLabel(p.getName());
                        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        nameLbl.setForeground(Theme.INK);

                        // Amber circle badge
                        int qty = p.getStockQty();
                        JPanel badge = new JPanel() {
                            @Override
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                Graphics2D g2 = (Graphics2D) g.create();
                                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                g2.setColor(Theme.WARN);
                                g2.fillOval(0, 0, getWidth(), getHeight());
                                g2.setColor(new Color(0x3A, 0x24, 0x0A));
                                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                FontMetrics fm = g2.getFontMetrics();
                                String qs = String.valueOf(qty);
                                g2.drawString(qs, (getWidth() - fm.stringWidth(qs)) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                                g2.dispose();
                            }
                        };
                        badge.setPreferredSize(new Dimension(22, 22));
                        badge.setOpaque(false);

                        itemRow.add(nameLbl, BorderLayout.WEST);
                        itemRow.add(badge, BorderLayout.EAST);
                        lowStockListPanel.add(itemRow);
                    }
                }
                lowStockListPanel.revalidate();
                lowStockListPanel.repaint();
            }

            // Populate recent orders
            if (recentOrdersTable != null) {
                DefaultTableModel model = (DefaultTableModel) recentOrdersTable.getModel();
                model.setRowCount(0);
                List<SalesOrder> orders = orderController.getAllOrders();
                int limit = Math.min(5, orders.size());
                for (int i = 0; i < limit; i++) {
                    SalesOrder so = orders.get(i);
                    model.addRow(new Object[]{
                        "#" + so.getOrderId(),
                        so.getCustomerName(),
                        so.getEmployeeName(),
                        String.format("%,.0f", so.getTotal()),
                        so.getStatus() != null ? so.getStatus() : "Completed"
                    });
                }
            }

        } catch (SQLException ignored) {}
    }
}
