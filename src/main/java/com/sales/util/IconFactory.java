package com.sales.util;

import javax.swing.Icon;
import java.awt.*;
import java.awt.geom.*;

public class IconFactory {

    public static Icon getDashboardIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            int s = (w - 6) / 2;
            g2.fill(new RoundRectangle2D.Double(1, 1, s, s, 3, 3));
            g2.fill(new RoundRectangle2D.Double(w - s - 1, 1, s, s, 3, 3));
            g2.fill(new RoundRectangle2D.Double(1, h - s - 1, s, s, 3, 3));
            g2.fill(new RoundRectangle2D.Double(w - s - 1, h - s - 1, s, s, 3, 3));
        });
    }

    public static Icon getCustomersIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.fillOval(w / 2 - 4, 2, 8, 8);
            g2.fill(new Arc2D.Double(2, 9, w - 4, 10, 0, 180, Arc2D.CHORD));
        });
    }

    public static Icon getProductsIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f));
            g2.draw(new RoundRectangle2D.Double(2, 3, w - 4, h - 6, 4, 4));
            g2.drawLine(2, 7, w - 2, 7);
            g2.drawLine(w / 2, 7, w / 2, h - 3);
        });
    }

    public static Icon getCartIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D p = new Path2D.Double();
            p.moveTo(2, 3);
            p.lineTo(5, 3);
            p.lineTo(7, 12);
            p.lineTo(w - 3, 12);
            p.lineTo(w - 1, 6);
            p.lineTo(6, 6);
            g2.draw(p);
            g2.fillOval(7, h - 4, 3, 3);
            g2.fillOval(w - 5, h - 4, 3, 3);
        });
    }

    public static Icon getOrdersIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f));
            g2.draw(new RoundRectangle2D.Double(3, 2, w - 6, h - 4, 3, 3));
            g2.drawLine(6, 6, w - 6, 6);
            g2.drawLine(6, 9, w - 6, 9);
            g2.drawLine(6, 12, w - 10, 12);
        });
    }

    public static Icon getPaymentIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f));
            g2.draw(new RoundRectangle2D.Double(1, 3, w - 2, h - 6, 4, 4));
            g2.fillRect(1, 6, w - 2, 3);
        });
    }

    public static Icon getReportIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.fillRect(2, h - 7, 3, 6);
            g2.fillRect(7, h - 11, 3, 10);
            g2.fillRect(12, h - 15, 3, 14);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(1, h - 1, w - 1, h - 1);
        });
    }

    public static Icon getJasperIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f));
            Path2D p = new Path2D.Double();
            p.moveTo(3, 2);
            p.lineTo(w - 6, 2);
            p.lineTo(w - 2, 6);
            p.lineTo(w - 2, h - 2);
            p.lineTo(3, h - 2);
            p.closePath();
            g2.draw(p);
            g2.drawLine(6, 8, w - 6, 8);
            g2.drawLine(6, 11, w - 6, 11);
        });
    }

    public static Icon getMoneyIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawOval(2, 2, w - 4, h - 4);
            g2.setFont(new Font("Georgia", Font.BOLD, (int)(size * 0.55)));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("R", (w - fm.stringWidth("R")) / 2, (h + fm.getAscent() - fm.getDescent()) / 2);
        });
    }

    public static Icon getAlertIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D p = new Path2D.Double();
            p.moveTo(w / 2, 2);
            p.lineTo(w - 2, h - 2);
            p.lineTo(2, h - 2);
            p.closePath();
            g2.draw(p);
            g2.drawLine(w / 2, 6, w / 2, 10);
            g2.fillOval(w / 2 - 1, h - 5, 2, 2);
        });
    }

    public static Icon getUserAvatarIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.fillOval(w / 2 - 4, 2, 8, 8);
            g2.fill(new Arc2D.Double(2, 10, w - 4, 10, 0, 180, Arc2D.CHORD));
        });
    }

    public static Icon getSearchIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f));
            int r = (int)(size * 0.35);
            g2.drawOval(2, 2, r * 2, r * 2);
            g2.drawLine(2 + r + (int)(r * 0.707), 2 + r + (int)(r * 0.707), w - 3, h - 3);
        });
    }

    public static Icon getLogoutIcon(int size, Color color) {
        return new VectorIcon(size, size, (g2, w, h) -> {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Door frame: top, left, bottom
            g2.drawLine(w / 2, 2, 3, 2);
            g2.drawLine(3, 2, 3, h - 3);
            g2.drawLine(3, h - 3, w / 2, h - 3);
            // Arrow pointing right out of the door
            g2.drawLine(w / 3, h / 2, w - 2, h / 2);
            g2.drawLine(w - 6, h / 2 - 4, w - 2, h / 2);
            g2.drawLine(w - 6, h / 2 + 4, w - 2, h / 2);
        });
    }

    private interface IconPainter {
        void paint(Graphics2D g2, int w, int h);
    }

    private static class VectorIcon implements Icon {
        private final int width;
        private final int height;
        private final IconPainter painter;

        VectorIcon(int width, int height, IconPainter painter) {
            this.width = width;
            this.height = height;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            painter.paint(g2, width, height);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return width; }

        @Override
        public int getIconHeight() { return height; }
    }
}
