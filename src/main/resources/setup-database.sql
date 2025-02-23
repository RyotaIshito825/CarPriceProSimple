CREATE DATABASE car_price_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'caruser'@'localhost' IDENTIFIED BY 'carpass';
GRANT ALL PRIVILEGES ON daily_report_system.* to 'caruser'@'localhost';