# Community Living BC Project

This project contains the backend and frontend for the Community Living system.

## Getting Started

### Prerequisites

- Java 17+
- MySQL
- Node.js (for frontend)
- Python 3.12 (for data population)

### Configuration

The project **requires** environment variables for sensitive configuration like database passwords and JWT secrets. The application will not start without these.

1. Copy `.env.example` to `.env`:
   ```bash
   cp .env.example .env
   ```
2. Update the values in `.env` with your local settings.

### Database Setup

The application uses two MySQL databases: `clbc_main` and `clbc_audit`. You must manually create these and initialize the schema before running the application.

1. **Create Databases and User:**
   ```sql
   CREATE DATABASE clbc_main;
   CREATE DATABASE clbc_audit;
   CREATE USER 'cladmin'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON clbc_main.* TO 'cladmin'@'localhost';
   GRANT ALL PRIVILEGES ON clbc_audit.* TO 'cladmin'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Initialize Schema:**
   Run the following SQL scripts from the `clbc-backend/src/main/resources/sql` directory against the `clbc_main` database:
   - `01_global_tables.sql`
   - `02_tenant_tables.sql`
   - `04_indexes.sql`
   - `05_seed_data.sql`

   Against the `clbc_audit` database:
   - `03_audit_tables.sql`

### Initial Credentials

Once the seed data is loaded, you can log in with:

- **Nurse:** `nurse@rose.com` / `password123`
- **Manager:** `manager@rose.com` / `password123`

## How to Run

### 1. Start the Backend (Spring Boot)

The backend provides the API and handles data logic.

```bash
# Navigate to the backend directory
cd clbc-backend

# Run the application using the Maven wrapper
./mvnw spring-boot:run
```
*Note: Ensure your `.env` file is ready in the root directory as described in the Configuration section.*

### 2. Start the Frontend (Vue.js + Vite)

The frontend is a modern web application built with Vue.

```bash
# Navigate to the frontend directory
cd clbc-frontend

# Install dependencies (only required the first time)
npm install

# Start the development server
npm run dev
```
Once started, the terminal will give you a local URL (usually `http://localhost:5173`) to open in your browser.

### 3. (Optional) Populate Sample Data

If you need to seed your database with sample clinical records:

```bash
# In the root directory
export DB_PASSWORD=your_password  # If not already set in your shell
python populate_clinical_data.py
```

## Security Note

- **Never commit your `.env` file.** It is already added to `.gitignore`.
- If you are deploying to production, ensure you change the `JWT_SECRET` to a unique, secure value.
