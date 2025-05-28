DROP USER IF EXISTS 'caruser'@'localhost';
CREATE DATABASE car_price_pro_simple CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
CREATE USER 'caruser'@'localhost' IDENTIFIED BY 'carpass';
GRANT ALL PRIVILEGES ON car_price_pro_simple.* to 'caruser'@'localhost';
FLUSH PRIVILEGES;