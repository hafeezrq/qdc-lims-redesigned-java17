# LIMS Installation & First-Run Handout (Windows 10/11)

This one‑page guide is for staff or installers to set up LIMS quickly.

## 1) Install PostgreSQL (one-time on server PC)
1. Install PostgreSQL (latest stable).
2. Create database: `lims_db`
3. Note the server PC IP address (example: `192.168.1.10`)

## 2) Install LIMS (on each PC)
1. Run the LIMS installer `.exe`.
2. Finish the setup wizard.

## 3) Configure the Database Connection
Edit `application-prod.properties` on each PC to point to the shared DB:

```
spring.datasource.url=jdbc:postgresql://<SERVER_IP>:5432/lims_db
spring.datasource.username=postgres
spring.datasource.password=<password>
```

## 4) First Run (Admin)
1. Start LIMS from **Start Menu → LIMS**.
2. Create the Admin account when prompted.
3. Enter lab details (name, address, contact).
4. Create users for Reception and Lab.

## 5) Verify
1. Reception creates a test order.
2. Lab sees the same order.
3. Lab enters results and prints.

## Support Checklist
- Server PC always ON.
- All PCs on same LAN.
- PostgreSQL port 5432 open on server firewall.
- Daily/weekly backups from the server PC.

