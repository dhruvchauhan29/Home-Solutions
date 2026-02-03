-- Insert Categories
INSERT INTO categories (name, description, active, created_at, updated_at) VALUES
('Cleaning', 'Professional home cleaning services', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Plumbing', 'Expert plumbing and water solutions', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Electrical', 'Safe and reliable electrical services', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Carpentry', 'Custom carpentry and furniture repair', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Painting', 'Interior and exterior painting services', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Appliance Repair', 'Repair services for home appliances', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pest Control', 'Effective pest control solutions', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AC Services', 'AC installation, repair and maintenance', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Cleaning Category (ID: 1)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Basic House Cleaning', 'Standard cleaning of rooms, kitchen, and bathrooms', 1, 500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Deep Cleaning', 'Comprehensive deep cleaning including hard-to-reach areas', 1, 1200.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Kitchen Cleaning', 'Complete kitchen cleaning including appliances', 1, 400.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bathroom Cleaning', 'Thorough bathroom sanitization', 1, 300.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Plumbing Category (ID: 2)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Tap Repair', 'Fix leaking or damaged taps', 2, 250.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Pipe Repair', 'Repair broken or leaking pipes', 2, 500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Drain Cleaning', 'Clear blocked drains and sewage', 2, 350.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Water Tank Cleaning', 'Complete water tank cleaning and sanitization', 2, 800.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Electrical Category (ID: 3)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Switch/Socket Installation', 'Install or replace switches and sockets', 3, 200.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fan Installation', 'Install ceiling or wall fans', 3, 350.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Wiring Repair', 'Fix electrical wiring issues', 3, 600.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Light Fixture Installation', 'Install lights and fixtures', 3, 300.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Carpentry Category (ID: 4)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Furniture Repair', 'Repair damaged furniture', 4, 400.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Door Installation', 'Install new doors or repair existing ones', 4, 800.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Cabinet Installation', 'Install kitchen or bathroom cabinets', 4, 1000.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Custom Furniture', 'Build custom furniture pieces', 4, 2000.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Painting Category (ID: 5)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Room Painting', 'Paint single room walls and ceiling', 5, 1500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Full House Painting', 'Complete house interior painting', 5, 8000.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Exterior Painting', 'Paint exterior walls and surfaces', 5, 5000.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Texture Painting', 'Decorative texture painting services', 5, 2500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Appliance Repair Category (ID: 6)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Washing Machine Repair', 'Repair washing machine issues', 6, 500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Refrigerator Repair', 'Fix refrigerator cooling and other issues', 6, 600.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Microwave Repair', 'Repair microwave ovens', 6, 400.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Chimney Repair', 'Kitchen chimney repair and cleaning', 6, 450.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for Pest Control Category (ID: 7)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('Cockroach Control', 'Eliminate cockroach infestation', 7, 800.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Termite Control', 'Professional termite treatment', 7, 2000.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bed Bug Treatment', 'Complete bed bug elimination', 7, 1500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('General Pest Control', 'Comprehensive pest control service', 7, 1200.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Services for AC Services Category (ID: 8)
INSERT INTO services (name, description, category_id, base_price, extra_hourly_rate, active, created_at, updated_at) VALUES
('AC Installation', 'Install new air conditioner', 8, 2000.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AC Repair', 'Fix AC cooling and other issues', 8, 600.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AC Service', 'Regular AC maintenance and cleaning', 8, 500.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AC Gas Refilling', 'Refill AC gas for optimal cooling', 8, 800.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Admin User (password: admin123)
INSERT INTO users (phone, email, full_name, password, enabled, created_at, updated_at) VALUES
('9999999999', 'admin@homesolutions.com', 'Admin User', '$2a$10$xQ5Z8qYqQYqYqYqYqYqYqOqYqYqYqYqYqYqYqYqYqYqYqYqYqYqYq', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role) VALUES
((SELECT id FROM users WHERE phone = '9999999999'), 'ROLE_ADMIN');
