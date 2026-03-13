CREATE TABLE IF NOT EXISTS residents (
    id VARCHAR(50) PRIMARY KEY,
    community_id VARCHAR(50) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(50),
    primary_physician VARCHAR(100),
    physician_contact VARCHAR(100),
    mrn VARCHAR(50),
    status ENUM('ACTIVE', 'INACTIVE', 'DISCHARGED', 'RECEIVED') DEFAULT 'RECEIVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS resident_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resident_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    dietary_preferences TEXT,
    activity_preferences TEXT,
    communication_preferences TEXT,
    care_notes TEXT,
    last_updated_by VARCHAR(50),
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES residents(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS prescriptions (
    id VARCHAR(50) PRIMARY KEY,
    resident_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    route VARCHAR(100),
    prescriber_name VARCHAR(100),
    prescriber_license VARCHAR(100),
    date_prescribed DATE,
    date_discontinued DATE,
    indication TEXT,
    side_effects TEXT,
    status ENUM('ACTIVE', 'DISCONTINUED', 'PAUSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES residents(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS medication_administrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    date_administered DATE NOT NULL,
    time_administered TIME NOT NULL,
    administered_by VARCHAR(50),
    dose_taken BOOLEAN DEFAULT TRUE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS observations (
    id VARCHAR(50) PRIMARY KEY,
    resident_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    type VARCHAR(100),
    value VARCHAR(255),
    unit VARCHAR(50),
    observed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES residents(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS conditions (
    id VARCHAR(50) PRIMARY KEY,
    resident_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    condition_name VARCHAR(255) NOT NULL,
    on_set_date DATE,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES residents(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS allergies (
    id VARCHAR(50) PRIMARY KEY,
    resident_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    allergen VARCHAR(255) NOT NULL,
    severity VARCHAR(50),
    reaction TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES residents(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS medical_documents (
    id VARCHAR(50) PRIMARY KEY,
    resident_id VARCHAR(50) NOT NULL,
    community_id VARCHAR(50) NOT NULL,
    document_name VARCHAR(255),
    document_type VARCHAR(100),
    document_content LONGBLOB, -- or JSON
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES residents(id),
    FOREIGN KEY (community_id) REFERENCES communities(id)
);

CREATE TABLE IF NOT EXISTS fhir_resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resident_id VARCHAR(50),
    community_id VARCHAR(50) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    fhir_id VARCHAR(100),
    fhir_json JSON NOT NULL,
    source_provider VARCHAR(100),
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    FOREIGN KEY (community_id) REFERENCES communities(id)
);
