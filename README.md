# LIMS

**IMPORTANT**
- Do not remove or change any working functionality without discussing it with the project owner and agreeing on the change.

**NOTE**
- Demo seed data runs only in the `dev` or `test` profile. Example: `SPRING_PROFILES_ACTIVE=dev`.
- Javadocs are generated on demand using: `./mvnw -Pdocs verify`
- The default profile is `prod`. Switch to dev/test with `SPRING_PROFILES_ACTIVE=dev` or `SPRING_PROFILES_ACTIVE=test`.
- This app uses **PostgreSQL only**. SQLite is not supported.

**Database (PostgreSQL)**
- Production connection: `src/main/resources/application-prod.properties`
- Development connection: `src/main/resources/application-dev.properties`
- To reset the prod DB (fresh start):
```sh
dropdb qdc_lims
createdb qdc_lims
```
- To reset the dev DB:
```sh
dropdb qdc_lims_test
createdb qdc_lims_test
```

**Safe Data Entry Order (Empty Database)**
1. Create Admin (first run prompt).
2. Configure lab info (branding/header settings).
3. Create users.
4. Create Departments.
5. Create Categories (per Department).
6. Create Test Definitions (assign to Department + Category).
7. Add Reference Ranges (recommended).
8. Add Doctors (optional).
9. Add Inventory + Test Recipes (optional).
10. Register Patients.
11. Create Lab Orders.
12. Enter Results in Lab.

**Backup Retention (Manual Management)**
- Automatic pruning is disabled (`qdc.backup.retention-days=0`).
- Backups are stored under the app data folder:
1. macOS: `~/Library/Application Support/LIMS/Backups`
2. Windows: `%APPDATA%\\LIMS\\Backups`
3. Linux: `~/.local/share/lims/Backups`
- To manage storage, periodically move older `.zip` backups to external storage or delete them manually.

**Production Build (Recommended)**
1. Build a native installer with bundled runtime:
```sh
./mvnw -DskipTests clean package jpackage
```
2. This uses the `badass-jlink` plugin and produces an installer under `target/`.

**First Run: Master Data**
- On first run in the `prod` profile, the app will auto-seed starter master data
  (departments, categories, tests, reference ranges, inventory, and recipes) from
  `src/main/resources/seed/master-data.json`
  *only if* the database is empty.
- Admin can review and edit prices, ranges, and inventory after login.
- Disable seeding by setting `qdc.seed.master.enabled=false` in `application.properties`.
- The seed file is safe to edit to match your lab’s catalog before first run.

**Master Data Structure**
- Department -> Category -> Test Definition.
- Categories are required for reporting and classification; they can be created in
  Test Definitions or seeded in `master-data.json`.

**Reference Ranges (Sources + Notes)**
- Adult hematology ranges are based on a Pakistan reference-interval study (eJIFCC).
- Adult fasting glucose + renal function (urea/creatinine/uric acid) ranges are based on
  a Pakistan reference-interval pilot study (Islamabad).
- Adult chemistry ranges (cholesterol, triglycerides, bilirubin, total protein, ALT, ALP)
  are based on a Pakistan adult-male study (Rawalpindi) and are seeded as \"Both\" until
  female-specific ranges are added.
- Pediatric CBC ranges are based on UK NHS reference intervals due to limited local data.
- All seeded ranges use SI units and can be edited in the Reference Ranges screen.
- If a range is missing, results still work; admins can fill ranges later.

**First-Run Checklist (Admin)**
1. Create Admin account (first-run prompt).
2. Review Lab Info (name, address, contact).
3. Review master data (Departments, Tests, Ranges).
4. Verify inventory items and recipes (for stock deduction).
5. Create staff user accounts and roles.
6. Perform a test order + result entry to validate setup.

**Inventory + Recipes**
- Recipes link tests to inventory items and quantities (used for stock deduction).
- If you add a new test, update its recipe to keep inventory accurate.

**Minimum Windows Requirements**
1. Windows 10 64-bit or Windows 11.
2. If distributing a plain JAR instead of the installer, Java 21 must be installed.
