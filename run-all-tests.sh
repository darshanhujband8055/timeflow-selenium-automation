#!/usr/bin/env bash
echo "======================================================="
echo "  Timeflow Test Automation Suite - 1-Click Runner"
echo "======================================================="
echo ""
echo "Running all 4 modules (Employee, Manager, PM, Admin)..."
echo ""

if [ -f "./mvnw" ]; then
    chmod +x ./mvnw
    ./mvnw clean test
else
    mvn clean test
fi

echo ""
echo "======================================================="
echo "  Execution Finished!"
echo "  Report: target/surefire-reports/index.html"
echo "======================================================="
