-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(30) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    total_amount DECIMAL(10,2),
    status VARCHAR(50),
    payment_status VARCHAR(50),
    receipt_status VARCHAR(50),
    express_company VARCHAR(100),
    express_number VARCHAR(100),
    user_address VARCHAR(500),
    logistics_info VARCHAR(1000),
    created_at DATETIME,
    updated_at DATETIME
);

-- 创建订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    price DECIMAL(10,2),
    quantity INT,
    subtotal DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- 插入测试订单数据
-- 订单1：testuser的已完成订单
INSERT INTO orders (order_number, username, total_amount, status, payment_status, receipt_status, express_company, express_number, user_address, logistics_info, created_at, updated_at)
VALUES ('20260405194500123', 'testuser', 21297.00, '已完成', '已支付', '已收货', '顺丰快递', 'SF1234567890', '北京市朝阳区', '【北京市】您的快件已送达，感谢使用顺丰快递', NOW(), NOW());

-- 订单2：testuser的待收货订单
INSERT INTO orders (order_number, username, total_amount, status, payment_status, receipt_status, express_company, express_number, user_address, logistics_info, created_at, updated_at)
VALUES ('20260405194600124', 'testuser', 7999.00, '待收货', '已支付', '未收货', '中通快递', 'ZT9876543210', '北京市海淀区', '【北京市】您的快件已发出，正在派送中', NOW(), NOW());

-- 订单3：user1的待付款订单
INSERT INTO orders (order_number, username, total_amount, status, payment_status, receipt_status, user_address, created_at, updated_at)
VALUES ('20260405194700125', 'user1', 3999.00, '待付款', '未支付', '未收货', '上海市浦东新区', NOW(), NOW());

-- 插入订单项数据
-- 订单1的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, subtotal)
VALUES (1, 1, 'iPhone 15 Pro', 7999.00, 2, 15998.00),
       (1, 2, 'MacBook Pro', 12999.00, 0.4, 5299.00);

-- 订单2的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, subtotal)
VALUES (2, 1, 'iPhone 15 Pro', 7999.00, 1, 7999.00);

-- 订单3的订单项
INSERT INTO order_items (order_id, product_id, product_name, price, quantity, subtotal)
VALUES (3, 3, 'AirPods Pro 2', 1999.00, 2, 3998.00);
