CREATE TABLE IF NOT EXISTS communities (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(50) PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('DIRECTOR', 'MANAGER', 'NURSE', 'PARENT', 'RESIDENT') NOT NULL,
    community_id VARCHAR(50),
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS fhir_error_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    community_id VARCHAR(50),
    error_type VARCHAR(50),
    error_message TEXT,
    fhir_resource_type VARCHAR(50),
    patient_identifier VARCHAR(100),
    source_provider VARCHAR(100),
    validation_details JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id)
);
CREATE TABLE IF NOT EXISTS system_clients (
    id VARCHAR(50) PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    client_secret_hash VARCHAR(255) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id)
);
