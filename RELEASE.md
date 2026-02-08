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
- **Console window in production**: release builds are GUI-only by default (no `--win-console`).

## Troubleshooting Quick Checks
- To enable a console launcher for debugging, run the GitHub Action manually and set `debug_console=true`.
- This uses Maven profile `windows-debug-console`, which appends `--win-console` only for that build.
- For normal releases, keep `debug_console=false` (default) for a native GUI launcher.

## Decision: Keep in Repo
This file is intended to stay in the repo so future releases are consistent.
