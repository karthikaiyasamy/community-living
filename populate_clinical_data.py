import mysql.connector
from datetime import datetime, timedelta
import random
import uuid
import os

# Configuration
db_config = {
    'user': os.environ.get('DB_USER', 'cladmin'),
    'password': os.environ.get('DB_PASSWORD'),
    'host': os.environ.get('DB_HOST', '127.0.0.1'),
    'database': os.environ.get('DB_DATABASE', 'clbc_main')
}

def populate_data():
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    residents = [
        {'id': 'res-1', 'comm': 'rose-living'},
        {'id': 'res-2', 'comm': 'rose-living'}
    ]

    # 1. Observations (7 days of history)
    vital_configs = [
        {'type': 'Heart Rate', 'unit': 'bpm', 'range': (65, 85)},
        {'type': 'Blood Pressure', 'unit': 'mmHg', 'range': (110, 140)}, # Values like "120/80"
        {'type': 'Glucose', 'unit': 'mg/dL', 'range': (90, 130)},
        {'type': 'Weight', 'unit': 'kg', 'range': (70, 75)}
    ]

    for res in residents:
        print(f"Populating vitals for {res['id']}...")
        for vital in vital_configs:
            for i in range(14): # 2 readings per day for 7 days
                time_offset = timedelta(days=i//2, hours=(i%2)*8 + 8)
                observed_at = datetime.now() - timedelta(days=7) + time_offset
                
                if vital['type'] == 'Blood Pressure':
                    systolic = random.randint(110, 140)
                    diastolic = random.randint(70, 90)
                    val = f"{systolic}/{diastolic}"
                else:
                    val = str(random.randint(*vital['range']))
                
                cursor.execute("""
                    INSERT INTO observations (id, resident_id, community_id, type, value, unit, observed_at)
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                """, (str(uuid.uuid4())[:10], res['id'], res['comm'], vital['type'], val, vital['unit'], observed_at))

    # 2. Prescriptions
    meds = [
        {'name': 'Lisinopril', 'dose': '10mg', 'freq': 'Daily', 'route': 'Oral'},
        {'name': 'Metformin', 'dose': '500mg', 'freq': 'BID', 'route': 'Oral'},
        {'name': 'Atorvastatin', 'dose': '20mg', 'freq': 'Nightly', 'route': 'Oral'}
    ]

    for res in residents:
        print(f"Adding prescriptions for {res['id']}...")
        for med in meds:
            p_id = f"presc-{res['id']}-{med['name'].lower()}"
            cursor.execute("""
                INSERT IGNORE INTO prescriptions (id, resident_id, community_id, medication_name, dosage, frequency, route, status, date_prescribed)
                VALUES (%s, %s, %s, %s, %s, %s, %s, 'ACTIVE', %s)
            """, (p_id, res['id'], res['comm'], med['name'], med['dose'], med['freq'], med['route'], datetime.now().date()))

            # 3. Administrations (Yesterday and Today)
            for d in [0, 1]:
                date_admin = datetime.now().date() - timedelta(days=d)
                cursor.execute("""
                    INSERT INTO medication_administrations (prescription_id, community_id, date_administered, time_administered, administered_by, dose_taken)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """, (p_id, res['comm'], date_admin, '09:00:00', 'system-populate', True))

    conn.commit()
    cursor.close()
    conn.close()
    print("Population complete!")

if __name__ == "__main__":
    populate_data()
