# Release Guide (Windows Installer)

This project ships a Windows desktop installer using GitHub Actions + jpackage.

## Why GitHub Actions
- Windows installers must be built on Windows.
- Using Actions makes builds repeatable and auditable.

## Prerequisites
- Ensure `pom.xml` version is updated for each release.
- Make sure the installer workflow exists: `.github/workflows/release-windows-installer.yml`.

## Release Steps (Recommended)

1) **Bump version** in `pom.xml`
   - Example: `1.0.5`

2) **Commit and push**
   - `git add -A`
   - `git commit -m "Release 1.0.5"`
   - `git push`

3) **Create a new tag** (do not reuse old tags)
   - `git tag LIMS_v1_0_5`
   - `git push origin LIMS_v1_0_5`

4) **Wait for GitHub Actions**
   - Repo → Actions → "Release Windows Installer"
   - Ensure the run completes successfully.

5) **Verify release assets**
   - Repo → Releases → `LIMS_v1_0_5`
   - Confirm `.exe` asset exists and is the latest timestamp.

## Manual Rebuild (if needed)
If you must rebuild the same tag (not recommended), you can use the workflow manual trigger with the same tag. This replaces the `.exe` asset.

## Common Pitfalls
- **Old tag**: workflow builds the exact commit the tag points to.
- **Wrong jar type**: jpackage must use the Spring Boot boot jar with `JarLauncher`.
- **Missing JDK modules**: the runtime image must include all required modules.
- **No logs**: enable console output (`--win-console`) when debugging installer failures.

## Troubleshooting Quick Checks
- Run the app from Command Prompt to capture output:
  - `cd "C:\Program Files\LIMS"`
  - `LIMS.exe --debug > "%TEMP%\lims-debug.txt" 2>&1`
- Check `%TEMP%\lims-debug.txt` for errors.

## Decision: Keep in Repo
This file is intended to stay in the repo so future releases are consistent.
