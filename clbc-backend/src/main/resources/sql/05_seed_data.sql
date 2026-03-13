-- Seed initial community
INSERT INTO communities (id, name, description) 
VALUES ('rose-living', 'Rose Living Society', 'Primary community for testing');

-- Seed initial users
-- Password is 'password123' hashed with BCrypt
INSERT INTO users (id, email, password_hash, role, community_id, status) 
VALUES ('user-1', 'nurse@rose.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'NURSE', 'rose-living', 'ACTIVE');

INSERT INTO users (id, email, password_hash, role, community_id, status) 
VALUES ('user-2', 'manager@rose.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOnu', 'MANAGER', 'rose-living', 'ACTIVE');

-- Seed some residents
INSERT INTO residents (id, community_id, first_name, last_name, date_of_birth, gender, status)
VALUES ('res-1', 'rose-living', 'John', 'Doe', '1995-05-15', 'MALE', 'ACTIVE');

INSERT INTO residents (id, community_id, first_name, last_name, date_of_birth, gender, status)
VALUES ('res-2', 'rose-living', 'Jane', 'Smith', '1998-10-22', 'FEMALE', 'ACTIVE');
