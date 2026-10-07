package com.sales.util;

import java.awt.*;

public class Theme {

    public enum ThemeMode {
        MIDNIGHT("1 Midnight"),
        CLEAN_BLUE("2 Clean blue"),
        EMERALD_SLATE("3 Emerald slate");

        private final String displayName;
        ThemeMode(String name) { this.displayName = name; }
        public String getDisplayName() { return displayName; }
    }

    public static ThemeMode currentMode = ThemeMode.CLEAN_BLUE;

    // Active theme colors
    public static Color BG;
    public static Color CARD;
    public static Color SIDEBAR;
    public static Color INK;
    public static Color MUTED;
    public static Color PRI;
    public static Color PRI_HOVER;
    public static Color PRI2;
    public static Color LINE;
    public static Color OK;
    public static Color BAD;
    public static Color WARN;
    public static Color PILL_BG;

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD= new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);

    static {
        applyTheme(ThemeMode.CLEAN_BLUE);
    }

    public static void applyTheme(ThemeMode mode) {
        currentMode = mode;
        switch (mode) {
            case CLEAN_BLUE:
            default:
                BG        = new Color(0x0A, 0x11, 0x24); // #0A1124 deep navy
                CARD      = new Color(0x11, 0x1C, 0x35); // #111C35 elevated blue card
                SIDEBAR   = new Color(0x0E, 0x17, 0x2E); // #0E172E blue sidebar
                INK       = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC crisp white
                MUTED     = new Color(0x94, 0xA3, 0xB8); // #94A3B8 slate muted
                PRI       = new Color(0x25, 0x63, 0xEB); // #2563EB Royal Blue
                PRI_HOVER = new Color(0x1D, 0x4E, 0xD8); // #1D4ED8 Blue hover
                PRI2      = new Color(0x1E, 0x29, 0x4B); // #1E294B soft blue tint
                LINE      = new Color(0x1E, 0x2B, 0x48); // #1E2B48 border
                OK        = new Color(0x10, 0xB9, 0x81);
                BAD       = new Color(0xEF, 0x44, 0x44);
                WARN      = new Color(0xF5, 0x9E, 0x0B);
                PILL_BG   = new Color(0x1E, 0x2C, 0x4D);
                break;

            case EMERALD_SLATE:
                BG        = new Color(0x08, 0x14, 0x11); // #081411 deep forest
                CARD      = new Color(0x0F, 0x23, 0x1F); // #0F231F elevated emerald card
                SIDEBAR   = new Color(0x0B, 0x1B, 0x18); // #0B1B18 dark slate
                INK       = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC
                MUTED     = new Color(0x6E, 0xE7, 0xB7); // #6EE7B7
                PRI       = new Color(0x05, 0x96, 0x69); // #059669 Emerald Green
                PRI_HOVER = new Color(0x04, 0x78, 0x57);
                PRI2      = new Color(0x13, 0x33, 0x2C);
                LINE      = new Color(0x16, 0x3A, 0x32);
                OK        = new Color(0x10, 0xB9, 0x81);
                BAD       = new Color(0xEF, 0x44, 0x44);
                WARN      = new Color(0xF5, 0x9E, 0x0B);
                PILL_BG   = new Color(0x12, 0x2D, 0x27);
                break;

            case MIDNIGHT:
                BG        = new Color(0x0B, 0x0F, 0x19); // #0B0F19 deep slate canvas
                CARD      = new Color(0x13, 0x1B, 0x2E); // #131B2E dark card surface
                SIDEBAR   = new Color(0x0F, 0x17, 0x2A); // #0F172A slate sidebar
                INK       = new Color(0xF8, 0xFA, 0xFC); // #F8FAFC bright text
                MUTED     = new Color(0x94, 0xA3, 0xB8); // #94A3B8 slate muted
                PRI       = new Color(0x7C, 0x3A, 0xED); // #7C3AED electric violet
                PRI_HOVER = new Color(0x6D, 0x28, 0xD9);
                PRI2      = new Color(0x24, 0x1B, 0x45); // #241B45 soft violet tint
                LINE      = new Color(0x1E, 0x29, 0x3B); // #1E293B border
                OK        = new Color(0x10, 0xB9, 0x81); // #10B981
                BAD       = new Color(0xEF, 0x44, 0x44); // #EF4444
                WARN      = new Color(0xF5, 0x9E, 0x0B); // #F59E0B amber badge
                PILL_BG   = new Color(0x1A, 0x23, 0x3A);
                break;
        }
    }

    public static void styleTable(javax.swing.JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(INK);
        table.setBackground(CARD);
        table.setSelectionBackground(PRI);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(LINE);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(new Color(0x18, 0x22, 0x38));
                lbl.setForeground(new Color(0xF1, 0xF5, 0xF9));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                    javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                lbl.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                lbl.setOpaque(true);
                return lbl;
            }
        });

        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    lbl.setBackground(PRI);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(row % 2 == 0 ? CARD : new Color(0x0E, 0x14, 0x24));
                    lbl.setForeground(new Color(0xF8, 0xFA, 0xFC));
                }
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
                lbl.setOpaque(true);
                return lbl;
            }
        });
    }

    public static <T> void styleComboBox(javax.swing.JComboBox<T> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(CARD);
        combo.setForeground(INK);
        combo.setOpaque(true);

        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected javax.swing.JButton createArrowButton() {
                javax.swing.JButton btn = new javax.swing.JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(CARD);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(MUTED);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xp = {cx - 4, cx + 4, cx};
                        int[] yp = {cy - 2, cy - 2, cy + 3};
                        g2.fillPolygon(xp, yp, 3);
                        g2.dispose();
                    }
                };
                btn.setBorder(javax.swing.BorderFactory.createEmptyBorder());
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setOpaque(true);
                btn.setBackground(CARD);
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(CARD);
                g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                g2.dispose();
            }
        });

        combo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(LINE, 1, true),
            javax.swing.BorderFactory.createEmptyBorder(4, 10, 4, 4)
        ));

        combo.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                javax.swing.JLabel lbl = (javax.swing.JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setOpaque(true);
                if (isSelected) {
                    lbl.setBackground(PRI);
                    lbl.setForeground(Color.WHITE);
                } else {
                    lbl.setBackground(CARD);
                    lbl.setForeground(INK);
                }
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                lbl.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return lbl;
            }
        });
    }

    public static void styleTextField(javax.swing.JTextField txt) {
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setBackground(new Color(0x0E, 0x16, 0x2C));
        txt.setForeground(INK);
        txt.setCaretColor(INK);
        txt.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(LINE, 1, true),
            javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    public static void styleScrollPane(javax.swing.JScrollPane sp) {
        sp.getViewport().setBackground(CARD);
        sp.setBackground(CARD);
        sp.setBorder(javax.swing.BorderFactory.createLineBorder(LINE, 1));
    }
}
