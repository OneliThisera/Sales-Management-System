package com.sales.view;

import com.sales.controller.OrderController;
import com.sales.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesTrendChart extends JPanel {

    private final OrderController orderController = new OrderController();
    private List<Object[]> trendData = new ArrayList<>();
    private double totalWeeklyRevenue = 0.0;

    public SalesTrendChart() {
        setOpaque(false);
        setPreferredSize(new Dimension(0, 240));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        loadData();
    }

    public void loadData() {
        try {
            trendData = orderController.getDailyRevenueTrend(7);
            totalWeeklyRevenue = 0.0;
            for (Object[] row : trendData) {
                totalWeeklyRevenue += (double) row[1];
            }
            repaint();
        } catch (SQLException ignored) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Card background & subtle border
        g2.setColor(Theme.CARD);
        g2.fill(new RoundRectangle2D.Double(0, 0, w, h, 14, 14));
        g2.setColor(Theme.LINE);
        g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, 14, 14));

        // Header: "Weekly revenue" (bold, white)
        g2.setColor(Theme.INK);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString("Weekly revenue", 22, 32);

        // Subtitle right: "7-day total LKR X.XXM"
        String sumText = formatTotalMillions(totalWeeklyRevenue);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(Theme.MUTED);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(sumText, w - fm.stringWidth(sumText) - 22, 32);

        if (trendData.isEmpty()) {
            g2.setColor(Theme.MUTED);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            String emptyMsg = "No sales records recorded in the past 7 days";
            g2.drawString(emptyMsg, (w - g2.getFontMetrics().stringWidth(emptyMsg)) / 2, h / 2 + 10);
            g2.dispose();
            return;
        }

        double maxRev = 1000.0;
        for (Object[] row : trendData) {
            double rev = (double) row[1];
            if (rev > maxRev) maxRev = rev;
        }
        maxRev = Math.max(maxRev, 10000.0);

        int chartLeft = 30;
        int chartRight = w - 30;
        int chartTop = 60;
        int chartBottom = h - 40;
        int chartHeight = chartBottom - chartTop;

        int count = trendData.size();
        int availableWidth = chartRight - chartLeft;
        int slotWidth = availableWidth / count;
        int barWidth = Math.min(54, Math.max(30, slotWidth - 22));

        for (int i = 0; i < count; i++) {
            Object[] row = trendData.get(i);
            String day = (String) row[0];
            double rev = (double) row[1];
            boolean isLast = (i == count - 1);

            int barHeight = (int) ((rev / maxRev) * chartHeight);
            if (barHeight < 16) barHeight = 16; // Minimum visual bar height

            int x = chartLeft + (i * slotWidth) + (slotWidth - barWidth) / 2;
            int y = chartBottom - barHeight;

            // Bar color: past days soft purple/violet, current/last day vibrant electric violet
            if (isLast || rev >= maxRev * 0.85) {
                g2.setColor(Theme.PRI);
            } else {
                g2.setColor(new Color(Theme.PRI.getRed(), Theme.PRI.getGreen(), Theme.PRI.getBlue(), 120));
            }
            g2.fill(new RoundRectangle2D.Double(x, y, barWidth, barHeight, 8, 8));

            // Day label below bar (e.g. "01", "02", "05 Oct")
            g2.setFont(new Font("Segoe UI", isLast ? Font.BOLD : Font.PLAIN, 12));
            g2.setColor(isLast ? Theme.INK : Theme.MUTED);
            int dw = g2.getFontMetrics().stringWidth(day);
            g2.drawString(day, x + (barWidth - dw) / 2, chartBottom + 22);
        }

        g2.dispose();
    }

    private String formatTotalMillions(double total) {
        if (total >= 1_000_000) {
            return String.format("7-day total LKR %.2fM", total / 1_000_000.0);
        } else if (total >= 1_000) {
            return String.format("7-day total LKR %.1fk", total / 1_000.0);
        } else {
            return String.format("7-day total LKR %,.2f", total);
        }
    }
}
