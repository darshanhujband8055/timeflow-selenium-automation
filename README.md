# Timeflow Automation Framework (4 Modules / User Roles)

An enterprise-grade End-to-End Test Automation Framework built with **Java 17+**, **Selenium WebDriver 4**, **TestNG**, and **Page Object Model (POM)** covering all 4 modules and roles for **Timeflow** (`https://timeflow.setoo.in`).

---

## 👥 Supported Roles & Modules

| Module / Role | Test User Email | Default Password | Primary Scope Verified |
|---|---|---|---|
| **Standard Employee** | `shubham.shinde@setoo.co` | `Pass@123` | Timesheets (Daily/Weekly/Monthly), Task Sync, Allocation, Calendar |
| **Reporting Manager** | `rutuja@setoo.co` | `Pass@123` | Approvals Queue, Reports, Team Directory, Bandwidth & Allocations |
| **Project Manager (PM)** | `rohan@setoo.co` | `Pass@123` | Projects List, Billing & Budget Dashboard, Financial Stat Cards, Approvals |
| **Administrator (Admin)** | `darshan@setoo.co` | `Pass@123` | Master Settings, User Governance, Analytics Tabs (Overview, Approvals, Submissions) |

---

## ⚡ Quick Start for Managers & Developers (1-Click Run)

### Prerequisites:
- **Java JDK 17 or higher** installed on your machine.
- Google Chrome browser installed.

### Option 1: 1-Click Run on Windows (Easiest)
Simply **double-click** the file:
```cmd
run-all-tests.bat
```
*(The bundled Maven Wrapper `mvnw.cmd` will handle everything automatically—no manual Maven installation required!)*

---

### Option 2: Run via Terminal / Command Line

```bash
# Clone the repository
git clone <repository-url>
cd "timeflow selenium"

# Run all 52 tests across all 4 modules:
./mvnw clean test

# Or if you have standard Maven installed:
mvn clean test
```

### Option 3: Run in IDE (IntelliJ IDEA / VS Code / Eclipse)
1. Open the project folder in your IDE.
2. Let Maven import dependencies from `pom.xml`.
3. Right-click on **`testng.xml`** -> Click **Run 'testng.xml'**.

---

## 📊 Viewing Test Reports & Defect Audits

1. **HTML Test Execution Report**:
   Open in any browser:
   ```
   target/surefire-reports/index.html
   ```
2. **Emailable Summary Report**:
   ```
   target/surefire-reports/emailable-report.html
   ```
3. **Module-Wise Defect PDF Reports**:
   Located in the project root:
   - `Timeflow_Defect_Report_Employee_Module.pdf`
   - `Timeflow_Defect_Report_Manager_Module.pdf`
   - `Timeflow_Defect_Report_PM_Module.pdf`
   - `Timeflow_Defect_Report_Admin_Module.pdf`

---

## ⚙️ Configuration (`src/main/resources/config.properties`)

```properties
# Target browser: chrome, firefox, edge
browser=chrome

# Application URL
url=https://timeflow.setoo.in

# Headless mode: false for visual execution, true for headless/CI
headless=false

# Timeout in seconds
timeout=15

# Maximize browser window
maximize=true
```

You can also override configurations via CLI:
```bash
# Run headless (ideal for background/CI execution)
./mvnw clean test -Dheadless=true

# Run on Edge or Firefox
./mvnw clean test -Dbrowser=edge
```

---

## 📁 Repository Structure

```
├── pom.xml                                // Maven dependencies & build plugins
├── testng.xml                             // TestNG suite configuration (52 tests)
├── mvnw / mvnw.cmd                        // Maven Wrapper (Zero-setup execution)
├── run-all-tests.bat                      // 1-Click Windows execution script
├── run-all-tests.sh                       // 1-Click macOS/Linux execution script
├── README.md                              // Quick start guide
├── Timeflow_Defect_Report_*.pdf           // Generated QA Defect Reports
└── src/
    ├── main/java/com/timeflow/
    │   ├── driver/                        // DriverFactory & DriverManager
    │   ├── pages/                         // Page Object Classes (BasePage, LoginPage, etc.)
    │   └── utils/                         // ConfigReader, ScreenshotUtils
    └── test/java/com/timeflow/
        ├── base/                          // BaseTest with driver lifecycle
        └── tests/                         // Test Classes for Employee, Manager, PM, Admin
```
