# LIMS Production Deployment (Shared Database, Multi-PC)

This guide describes a practical, low-risk setup for running LIMS on multiple
Windows 10/11 computers (Reception, Lab, Admin) while sharing the same data.

## Overview
- Install LIMS on each PC.
- Use one PostgreSQL server for all PCs.
- Configure each app instance to connect to the shared database.

## 1) Choose the Database Server PC
Pick one always-on computer to host PostgreSQL (Reception PC or a dedicated PC).
Requirements:
- Windows 10/11
- Stable LAN connection
- Always powered on during working hours

## 2) Install PostgreSQL on the Server PC
1. Install PostgreSQL (latest stable).
2. Set a strong password for the postgres user.
3. Create a database:
   - Database name: `lims_db`
   - Owner: `postgres` (or a dedicated user you create)

## 3) Allow Network Connections to PostgreSQL
On the server PC:
1. Find `postgresql.conf` and set:
   - `listen_addresses = '*'`
2. Find `pg_hba.conf` and add a line for your LAN subnet, for example:
   - `host  all  all  192.168.1.0/24  md5`
3. Restart PostgreSQL service.

## 4) Windows Firewall Rule (Server PC)
Allow inbound TCP port 5432.
- Windows Defender Firewall → Advanced Settings → Inbound Rules → New Rule
- Port: 5432, TCP, allow

## 5) Configure LIMS on Each Client PC
Each PC needs its own installation of LIMS, but all must point to the same DB.

In `application-prod.properties` on each client:

```
spring.datasource.url=jdbc:postgresql://<SERVER_IP>:5432/lims_db
spring.datasource.username=postgres
spring.datasource.password=<your-password>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

Replace `<SERVER_IP>` with the server PC LAN IP.

## 6) First Run / Master Data Seeding
- The first run on an empty DB will seed master data.
- Only do this once (on the server DB).
- Other PCs will see the same master data automatically.

## 7) Backups (Server PC)
- Backups should be managed on the **server PC**.
- Keep a daily or weekly copy on external storage.
- If the server PC fails, restore the DB from backups.

## 8) Operational Checklist
1. Verify all PCs can ping the DB server.
2. Open LIMS on each PC and log in.
3. Create a test order on Reception.
4. Confirm the Lab PC sees the same order.
5. Enter a result and confirm it prints on Reception.

## 9) Javadocs (Release Hygiene)
Before each production build:
```
./mvnw -Pdocs verify
```
This regenerates Javadocs and ensures they match the current code.

## Troubleshooting
- If clients cannot connect:
  - Check server IP, firewall, and PostgreSQL service.
  - Confirm `pg_hba.conf` allows your LAN subnet.
- If data looks inconsistent:
  - Ensure all clients are connecting to the same DB host.

