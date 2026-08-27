@echo off
echo =======================================================
echo   Timeflow Test Automation Suite - 1-Click Runner
echo =======================================================
echo.
echo Running all 4 modules (Employee, Manager, PM, Admin)...
echo.

if exist mvnw.cmd (
    call mvnw.cmd clean test
) else (
    call mvn clean test
)

echo.
echo =======================================================
echo   Execution Finished!
echo   Report: target\surefire-reports\index.html
echo =======================================================
pause
