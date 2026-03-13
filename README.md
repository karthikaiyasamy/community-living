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

#### Spring Boot (Backend)

The backend in `clbc-backend` will automatically pick up environment variables. You can also pass them as JVM arguments if needed (e.g., `-DDB_PASSWORD=your_password`). **Note: `DB_PASSWORD` and `JWT_SECRET` are now mandatory.**

#### Data Population Script

The `populate_clinical_data.py` script also requires these environment variables:
```bash
export DB_PASSWORD=your_password
python populate_clinical_data.py
```

## Security Note

- **Never commit your `.env` file.** It is already added to `.gitignore`.
- If you are deploying to production, ensure you change the `JWT_SECRET` to a unique, secure value.
