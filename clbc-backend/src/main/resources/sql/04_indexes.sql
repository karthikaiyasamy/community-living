-- Global tables indexes
CREATE INDEX idx_users_community ON users(community_id);
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);

-- Tenant tables indexes
CREATE INDEX idx_residents_community ON residents(community_id);
CREATE INDEX idx_residents_status ON residents(status);
CREATE INDEX idx_prescriptions_resident ON prescriptions(resident_id);
CREATE INDEX idx_prescriptions_community ON prescriptions(community_id);
CREATE INDEX idx_med_admin_prescription ON medication_administrations(prescription_id);
CREATE INDEX idx_observations_resident ON observations(resident_id);
CREATE INDEX idx_conditions_resident ON conditions(resident_id);
CREATE INDEX idx_allergies_resident ON allergies(resident_id);
CREATE INDEX idx_fhir_res_community ON fhir_resources(community_id);
CREATE INDEX idx_fhir_res_resident ON fhir_resources(resident_id);
