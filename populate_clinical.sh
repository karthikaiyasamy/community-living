#!/bin/bash

DB_USER="cladmin"
DB_PASS="1@CommunityLivingBC"
DB_NAME="clbc_main"

# Clear old observations to start fresh for trends
mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "DELETE FROM observations; DELETE FROM medication_administrations; DELETE FROM prescriptions;"

echo "Populating Clinical Data..."

# Residents list from previous query: res-1, res-2
RESIDENTS=("res-1" "res-2")
COMMUNITY="rose-living"

for RES_ID in "${RESIDENTS[@]}"; do
    echo "Processing $RES_ID..."
    
    # 1. Insert Prescriptions
    mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO prescriptions (id, resident_id, community_id, medication_name, dosage, frequency, route, status, date_prescribed) VALUES ('pre-${RES_ID}-1', '$RES_ID', '$COMMUNITY', 'Lisinopril', '10mg', 'Once Daily', 'Oral', 'ACTIVE', CURDATE());"
    mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO prescriptions (id, resident_id, community_id, medication_name, dosage, frequency, route, status, date_prescribed) VALUES ('pre-${RES_ID}-2', '$RES_ID', '$COMMUNITY', 'Metformin', '500mg', 'Twice Daily', 'Oral', 'ACTIVE', CURDATE());"
    
    # 2. Insert Observations (Past 7 days, 2/day)
    for i in {0..14}; do
        DAY=$((i / 2))
        HOUR=$(( (i % 2) * 8 + 8 ))
        OBS_DATE=$(date -v-${DAY}d -v${HOUR}H -v0M -v0S "+%Y-%m-%d %H:%M:%S" 2>/dev/null || date -d "-$DAY days $HOUR hours" "+%Y-%m-%d %H:%M:%S")
        
        # Heart Rate
        VAL_HR=$(( 60 + RANDOM % 30 ))
        mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO observations (id, resident_id, community_id, type, value, unit, observed_at) VALUES (LEFT(UUID(), 10), '$RES_ID', '$COMMUNITY', 'Heart Rate', '$VAL_HR', 'bpm', '$OBS_DATE');"
        
        # Blood Pressure
        SYS=$(( 110 + RANDOM % 30 ))
        DIA=$(( 70 + RANDOM % 20 ))
        mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO observations (id, resident_id, community_id, type, value, unit, observed_at) VALUES (LEFT(UUID(), 10), '$RES_ID', '$COMMUNITY', 'Blood Pressure', '$SYS/$DIA', 'mmHg', '$OBS_DATE');"
        
        # Glucose
        VAL_GL=$(( 80 + RANDOM % 60 ))
        mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO observations (id, resident_id, community_id, type, value, unit, observed_at) VALUES (LEFT(UUID(), 10), '$RES_ID', '$COMMUNITY', 'Glucose', '$VAL_GL', 'mg/dL', '$OBS_DATE');"
    done

    # 3. Insert some MAR logs for yesterday
    YESTERDAY=$(date -v-1d "+%Y-%m-%d" 2>/dev/null || date -d "yesterday" "+%Y-%m-%d")
    mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO medication_administrations (prescription_id, community_id, date_administered, time_administered, administered_by, dose_taken) VALUES ('pre-${RES_ID}-1', '$COMMUNITY', '$YESTERDAY', '09:00:00', 'nurse_joy', 1);"
    mysql -u $DB_USER -p$DB_PASS $DB_NAME -e "INSERT INTO medication_administrations (prescription_id, community_id, date_administered, time_administered, administered_by, dose_taken) VALUES ('pre-${RES_ID}-2', '$COMMUNITY', '$YESTERDAY', '09:00:00', 'nurse_joy', 1);"
done

echo "Done!"
