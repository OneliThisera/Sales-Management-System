package com.sales.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSeeder {

    public static boolean isOrdersEmpty() {
        try {
            Connection conn = DBConnection.getInstance().getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM sales_order")) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException ignored) {}
        return true;
    }

    public static void seedData() throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("DELETE FROM payment");
            stmt.execute("DELETE FROM order_item");
            stmt.execute("DELETE FROM sales_order");
            stmt.execute("DELETE FROM product");
            stmt.execute("DELETE FROM category");
            stmt.execute("DELETE FROM customer");
            stmt.execute("DELETE FROM employee");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            stmt.execute("INSERT INTO employee (employee_id, name, role, username, password) VALUES " +
                "(1, 'Admin User', 'Admin', 'admin', 'admin123')," +
                "(2, 'Nimali Perera', 'Cashier', 'cashier', 'cashier123')");

            stmt.execute("INSERT INTO category (category_id, name, description) VALUES " +
                "(1, 'Gaming PCs & Laptops', 'Custom liquid-cooled gaming rigs, high-end workstations and ultraportable gaming laptops')," +
                "(2, 'Processors & Motherboards', 'Multi-core desktop CPUs, Intel Core i9, AMD Ryzen, and enthusiast motherboards')," +
                "(3, 'Graphics Cards (GPUs)', 'NVIDIA GeForce RTX 40-series and AMD Radeon RX discrete graphics cards')," +
                "(4, 'Memory & Fast Storage', 'DDR5 high-frequency RAM kits, Gen4/Gen5 NVMe M.2 solid state drives and modular PSUs')," +
                "(5, 'Peripherals & Displays', 'Esports mechanical keyboards, ultralight wireless mice, headsets and 240Hz gaming monitors')");

            stmt.execute("INSERT INTO product (product_id, name, description, price, stock_qty, category_id) VALUES " +
                "(1, 'Apex Zenith RTX 4080 Gaming Rig', 'Liquid-cooled Intel i9-14900KF, RTX 4080 16GB, 64GB DDR5, 2TB Gen4 SSD', 685000.00, 5, 1)," +
                "(2, 'ASUS ROG Zephyrus G16 Gaming Laptop', 'Intel Core Ultra 9, RTX 4070 8GB, 32GB RAM, 1TB SSD, 240Hz OLED', 545000.00, 8, 1)," +
                "(3, 'Lenovo Legion Pro 5 16 Gaming Laptop', 'AMD Ryzen 7 7745HX, RTX 4060 8GB, 16GB DDR5, 1TB SSD, 165Hz IPS', 445000.00, 10, 1)," +
                "(4, 'MSI Stealth 14 Studio Laptop', 'Intel Core i7-13700H, RTX 4060, 16GB RAM, 1TB NVMe, QHD 240Hz', 395000.00, 6, 1)," +
                "(5, 'Intel Core i9-14900K 24-Core CPU', 'Up to 6.0 GHz unlocked desktop processor with Intel UHD Graphics 770', 195000.00, 14, 2)," +
                "(6, 'AMD Ryzen 7 7800X3D 8-Core CPU', 'Ultimate gaming processor with 3D V-Cache technology up to 5.0 GHz', 165000.00, 18, 2)," +
                "(7, 'ASUS ROG Maximus Z790 Dark Hero', 'Intel Z790 LGA 1700 ATX motherboard with PCIe 5.0 and Wi-Fi 7', 178000.00, 7, 2)," +
                "(8, 'MSI MAG B650 Tomahawk WiFi', 'AMD AM5 ATX gaming motherboard with DDR5 support and Lightning Gen4', 78000.00, 15, 2)," +
                "(9, 'MSI GeForce RTX 4080 Super 16GB', 'Tri Frozr 3 thermal design, DLSS 3.5, Ada Lovelace architecture', 395000.00, 4, 3)," +
                "(10, 'ASUS TUF Gaming GeForce RTX 4070 Ti', '12GB GDDR6X, military-grade capacitors, dual ball fan bearings', 295000.00, 8, 3)," +
                "(11, 'Gigabyte GeForce RTX 4060 Windforce', '8GB GDDR6, alternate spinning fans, ultra durable protection backplate', 128000.00, 16, 3)," +
                "(12, 'Corsair Vengeance RGB 32GB DDR5 6000MHz', '2x16GB high performance dual channel kit with ten-zone RGB lighting', 46500.00, 22, 4)," +
                "(13, 'Kingston FURY Beast 16GB DDR5 5600MHz', 'Single module high speed plug and play low profile heatsink memory', 24500.00, 30, 4)," +
                "(14, 'Samsung 990 PRO 2TB NVMe M.2 SSD', 'PCIe Gen 4.0 read speeds up to 7450 MB/s with thermal control', 68000.00, 18, 4)," +
                "(15, 'Crucial P3 Plus 1TB NVMe M.2 SSD', 'PCIe Gen 4.0 x4 read speeds up to 5000 MB/s for fast game load times', 28500.00, 5, 4)," +
                "(16, 'Corsair RM850x 850W Modular PSU', '80 PLUS Gold certified fully modular low-noise ATX power supply', 54000.00, 11, 4)," +
                "(17, 'Razer DeathAdder V3 Pro Wireless Mouse', 'Focus Pro 30K optical sensor, 63g ultra-lightweight ergonomic mouse', 42000.00, 15, 5)," +
                "(18, 'Logitech G PRO X Superlight 2 Mouse', 'HERO 2 sensor, LIGHTFORCE hybrid switches, 60g tournament mouse', 48500.00, 4, 5)," +
                "(19, 'SteelSeries Apex Pro TKL Keyboard', 'OmniPoint 2.0 adjustable hypermagnetic switches and OLED smart display', 65000.00, 9, 5)," +
                "(20, 'ASUS ROG Swift 27 inch 240Hz Gaming Monitor', 'Fast IPS QHD 2560x1440, 1ms GTG response time, G-SYNC compatible', 145000.00, 7, 5)");

            stmt.execute("INSERT INTO customer (customer_id, name, email, phone, address) VALUES " +
                "(1, 'Kavindu Wickramasinghe', 'kavindu.w@gmail.com', '0771234567', '45 Duplication Road, Colombo 03')," +
                "(2, 'Shenal Perera', 'shenal.p@yahoo.com', '0719876543', '18 Havelock Road, Colombo 05')," +
                "(3, 'Nimesh Fernando', 'nimesh.f@hotmail.com', '0762345678', '82 Peradeniya Road, Kandy')," +
                "(4, 'Thisara Gunasekara', 'thisara.g@gmail.com', '0753456789', '29 Matara Road, Galle')," +
                "(5, 'Sachintha De Silva', 'sachintha.d@outlook.com', '0704567890', '104 Ward Place, Colombo 07')," +
                "(6, 'Dilshan Samarasinghe', 'dilshan.s@gmail.com', '0725678901', '15 Negombo Road, Ja-Ela')," +
                "(7, 'Bhanuka Jayasuriya', 'bhanuka.j@gmail.com', '0776789012', '67 Kotte Road, Rajagiriya')," +
                "(8, 'Akila Senanayake', 'akila.s@gmail.com', '0787890123', '31 Nawala Road, Nugegoda')," +
                "(9, 'Tharindu Wijesinghe', 'tharindu.w@gmail.com', '0711122334', '12 Station Road, Bambalapitiya')," +
                "(10, 'Sandun Rathnayake', 'sandun.r@yahoo.com', '0772233445', '88 High Level Road, Maharagama')," +
                "(11, 'Imran Mansoor', 'imran.m@techcorp.lk', '0763344556', '54 Galle Road, Dehiwala')," +
                "(12, 'Chathura Alwis', 'chathura.a@gmail.com', '0754455667', '21 Temple Road, Nugegoda')," +
                "(13, 'Hashini Jayawardena', 'hashini.j@outlook.com', '0705566778', '93 Kandy Road, Kiribathgoda')," +
                "(14, 'Dhanushka Bandara', 'dhanushka.b@gmail.com', '0786677889', '140 Gampaha Road, Yakkala')," +
                "(15, 'Minoli Cooray', 'minoli.c@gmail.com', '0727788990', '77 Dharmapala Mawatha, Colombo 03')");

            stmt.execute("INSERT INTO sales_order (order_id, customer_id, employee_id, order_date, status, total) VALUES " +
                "(1, 1, 1, NOW() - INTERVAL 4 DAY, 'Completed', 779500.00)," +
                "(2, 2, 2, NOW() - INTERVAL 3 DAY, 'Completed', 591500.00)," +
                "(3, 3, 2, NOW() - INTERVAL 3 DAY, 'Completed', 263000.00)," +
                "(4, 4, 2, NOW() - INTERVAL 2 DAY, 'Completed', 510000.00)," +
                "(5, 5, 1, NOW() - INTERVAL 2 DAY, 'Completed', 237000.00)," +
                "(6, 6, 2, NOW() - INTERVAL 1 DAY, 'Completed', 419500.00)," +
                "(7, 7, 2, NOW() - INTERVAL 1 DAY, 'Completed', 363000.00)," +
                "(8, 8, 1, NOW() - INTERVAL 12 HOUR, 'Completed', 193500.00)," +
                "(9, 1, 2, NOW() - INTERVAL 5 HOUR, 'Completed', 443500.00)," +
                "(10, 2, 2, NOW() - INTERVAL 3 HOUR, 'Completed', 337000.00)," +
                "(11, 3, 1, NOW() - INTERVAL 90 MINUTE, 'Completed', 211000.00)," +
                "(12, 4, 2, NOW() - INTERVAL 40 MINUTE, 'Completed', 145000.00)," +
                "(13, 5, 2, NOW() - INTERVAL 15 MINUTE, 'Pending', 96500.00)");

            stmt.execute("INSERT INTO order_item (item_id, order_id, product_id, qty, unit_price, subtotal) VALUES " +
                "(1, 1, 1, 1, 685000.00, 685000.00)," +
                "(2, 1, 14, 1, 68000.00, 68000.00)," +
                "(3, 1, 19, 1, 65000.00, 65000.00)," +
                "(4, 2, 2, 1, 545000.00, 545000.00)," +
                "(5, 2, 12, 1, 46500.00, 46500.00)," +
                "(6, 3, 6, 1, 165000.00, 165000.00)," +
                "(7, 3, 8, 1, 78000.00, 78000.00)," +
                "(8, 4, 3, 1, 445000.00, 445000.00)," +
                "(9, 4, 19, 1, 65000.00, 65000.00)," +
                "(10, 5, 7, 1, 178000.00, 178000.00)," +
                "(11, 5, 16, 1, 54000.00, 54000.00)," +
                "(12, 6, 9, 1, 395000.00, 395000.00)," +
                "(13, 6, 13, 1, 24500.00, 24500.00)," +
                "(14, 7, 10, 1, 295000.00, 295000.00)," +
                "(15, 7, 14, 1, 68000.00, 68000.00)," +
                "(16, 8, 11, 1, 128000.00, 128000.00)," +
                "(17, 8, 19, 1, 65000.00, 65000.00)," +
                "(18, 9, 9, 1, 395000.00, 395000.00)," +
                "(19, 9, 18, 1, 48500.00, 48500.00)," +
                "(20, 10, 10, 1, 295000.00, 295000.00)," +
                "(21, 10, 17, 1, 42000.00, 42000.00)," +
                "(22, 11, 5, 1, 195000.00, 195000.00)," +
                "(23, 11, 15, 1, 28500.00, 28500.00)," +
                "(24, 12, 20, 1, 145000.00, 145000.00)," +
                "(25, 13, 18, 1, 48500.00, 48500.00)," +
                "(26, 13, 12, 1, 46500.00, 46500.00)");

            stmt.execute("INSERT INTO payment (payment_id, order_id, amount, method, payment_date) VALUES " +
                "(1, 1, 779500.00, 'Card', NOW() - INTERVAL 4 DAY)," +
                "(2, 2, 591500.00, 'Bank Transfer', NOW() - INTERVAL 3 DAY)," +
                "(3, 3, 263000.00, 'Card', NOW() - INTERVAL 3 DAY)," +
                "(4, 4, 510000.00, 'Card', NOW() - INTERVAL 2 DAY)," +
                "(5, 5, 237000.00, 'Cash', NOW() - INTERVAL 2 DAY)," +
                "(6, 6, 419500.00, 'Card', NOW() - INTERVAL 1 DAY)," +
                "(7, 7, 363000.00, 'Bank Transfer', NOW() - INTERVAL 1 DAY)," +
                "(8, 8, 193500.00, 'Cash', NOW() - INTERVAL 12 HOUR)," +
                "(9, 9, 443500.00, 'Card', NOW() - INTERVAL 5 HOUR)," +
                "(10, 10, 337000.00, 'Bank Transfer', NOW() - INTERVAL 3 HOUR)," +
                "(11, 11, 211000.00, 'Card', NOW() - INTERVAL 90 MINUTE)," +
                "(12, 12, 145000.00, 'Cash', NOW() - INTERVAL 40 MINUTE)");
        }
    }
}
