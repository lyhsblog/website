-- Create sequence for donation_id
CREATE SEQUENCE donation_seq START WITH 1 INCREMENT BY 50;

-- Create donation table
CREATE TABLE donation (
    donation_id BIGINT PRIMARY KEY DEFAULT nextval('donation_seq'),
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    donor_name VARCHAR(100),
    donor_email VARCHAR(255),
    message VARCHAR(500),
    donation_date TIMESTAMP NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_donation_status ON donation(status);
CREATE INDEX idx_donation_date ON donation(donation_date);
CREATE INDEX idx_donation_amount ON donation(amount);
CREATE INDEX idx_donation_email ON donation(donor_email);

-- Create check constraint for status values
ALTER TABLE donation ADD CONSTRAINT chk_donation_status 
    CHECK (status IN ('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED'));

-- Insert some sample data
INSERT INTO donation (amount, donor_name, donor_email, message, donation_date, is_anonymous, status) VALUES
(100.00, '张三', 'zhangsan@example.com', '支持项目发展！', CURRENT_TIMESTAMP - INTERVAL '5 days', false, 'COMPLETED'),
(200.00, '李四', 'lisi@example.com', '期待更好的功能', CURRENT_TIMESTAMP - INTERVAL '4 days', false, 'COMPLETED'),
(500.00, NULL, 'anonymous@example.com', '匿名支持', CURRENT_TIMESTAMP - INTERVAL '3 days', true, 'COMPLETED'),
(150.00, '王五', 'wangwu@example.com', '加油！', CURRENT_TIMESTAMP - INTERVAL '2 days', false, 'CONFIRMED'),
(300.00, '赵六', 'zhaoliu@example.com', '希望项目越来越好', CURRENT_TIMESTAMP - INTERVAL '1 day', false, 'CONFIRMED'),
(1000.00, '钱七', 'qianqi@example.com', '大力支持！', CURRENT_TIMESTAMP, false, 'PENDING');