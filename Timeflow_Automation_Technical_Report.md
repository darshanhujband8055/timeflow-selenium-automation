# 📊 Timeflow Automation Framework: Technical & Architectural Report

**Target Platform:** [Timeflow](https://timeflow.setoo.in)  
**Organization:** Setoo  
**Repository:** [github.com/darshanhujband8055/timeflow-selenium-automation](https://github.com/darshanhujband8055/timeflow-selenium-automation)  
**Document Version:** 1.0 (Enterprise QA Delivery)  
**Author:** QA Automation Engineering Team  

---

## 1. Executive Summary

This report provides an engineering overview of the **Timeflow Test Automation Framework**, developed to validate end-to-end functionality, role-based access security, and business workflows across all **4 user personas** on the Setoo Timeflow application.

The automation suite was architected for **zero-friction portability**: any manager, developer, or QA engineer can clone the repository and execute the entire 52-test automated suite on their local workstation with a single click, without manually installing build tools or browser drivers.

---

## 2. Technology Stack, Tools & Services

| Dimension | Tool / Technology | Version / Details | Purpose in Automation |
|---|---|---|---|
| **Language** | **Java** | OpenJDK 17+ (LTS) | Robust, statically typed language ensuring compile-time safety and enterprise maintainability. |
| **Automation Engine** | **Selenium WebDriver** | `4.29.0` | Browser automation engine with built-in **Selenium Manager** (zero manual chromedriver downloads). |
| **Test Runner** | **TestNG** | `7.10.2` | Test execution lifecycle, assertions, parameterized suites, parallel execution, and listener hooks. |
| **Build & Package** | **Apache Maven** | `3.9+` (with `mvnw` wrapper) | Dependency management, compilation, and automated test execution via `maven-surefire-plugin`. |
| **Design Pattern** | **Page Object Model (POM)** | Clean separation of concerns | Decouples web element locators and user actions from test assertion logic. |
| **Target App Frontend** | **Next.js / React** | Tailwind CSS / Radix UI | Modern SPA frontend with asynchronous data hydration, dynamic charts, and modal components. |
| **Target App Backend** | **Node.js / REST API** | PostgreSQL / Supabase | Relational database persisting timesheets, project budgets, task allocations, and user profiles. |
| **Authentication** | **JWT / Microsoft OAuth** | Role-Based Session Tokens | Authenticates Employee, Manager, PM, and Admin roles with role-gated routes. |
| **Version Control** | **Git / GitHub** | `main` branch | [darshanhujband8055/timeflow-selenium-automation](https://github.com/darshanhujband8055/timeflow-selenium-automation) |
| **ALM / Tracking** | **Azure DevOps** | Org: `setoo-ai` / Project: `Timeflow timesheet` | Work item tracking, bug management, and sprint backlog alignment. |

---

## 3. Framework Architecture & Design Pattern

```mermaid
graph TD
    subgraph Test Orchestration Layer
        A["testng.xml (Master Suite)"] --> B["Test Classes (src/test/java)"]
        B --> C["BaseTest (Lifecycle Hooks: @BeforeMethod / @AfterMethod)"]
    end

    subgraph Driver & Infrastructure Layer
        C --> D["DriverManager (ThreadLocal<WebDriver>)"]
        D --> E["DriverFactory"]
        E --> F["Selenium Manager (Auto-resolves Chrome, Edge, Firefox)"]
        F --> G["Browser Instance (Headless or Visual)"]
    end

    subgraph Page Object Model (POM) Layer
        B --> H["Page Objects (src/main/java/com/timeflow/pages)"]
        H --> I["BasePage (Explicit Waits: WebDriverWait & ExpectedConditions)"]
        I --> G
    end

    subgraph Diagnostics & Reporting Layer
        B --> J["TestListener (ITestListener)"]
        J -->|On Failure| K["ScreenshotUtils (PNG captures in target/screenshots/)"]
        B --> L["Maven Surefire Plugin"]
        L --> M["HTML Reports (target/surefire-reports/index.html)"]
        L --> N["Emailable Summary (emailable-report.html)"]
    end
```

### Core Architectural Decisions:
1. **ThreadLocal Driver Management (`DriverManager.java`):**
   - Each test thread maintains an isolated `WebDriver` instance, preventing session crossover and ensuring seamless parallel execution across browser threads.
2. **Explicit Dynamic Synchronization (`BasePage.java`):**
   - Eliminates brittle `Thread.sleep` calls. All interactions rely on `WebDriverWait` with `ExpectedConditions` (`visibilityOfElementLocated`, `elementToBeClickable`, `invisibilityOfElementLocated`) tuned for React/Next.js client-side rendering.
3. **Externalized Configuration (`config.properties`):**
   - Parameters such as target browser (`chrome`, `edge`, `firefox`), environment URL, explicit timeouts, and `headless` toggles can be overridden via properties or runtime CLI arguments (`-Dheadless=true`).

---

## 4. Execution Workflow & Portability

The framework provides three distinct execution tiers to suit different stakeholder needs:

### Tier 1: 1-Click Zero-Setup Execution (For Managers & Stakeholders)
- **Windows:** Double-click `run-all-tests.bat`
- **macOS / Linux:** Run `./run-all-tests.sh`
- *No pre-installed Maven is required.* The bundled **Maven Wrapper (`mvnw`)** automatically checks, downloads, and configures the exact Maven runtime required.

### Tier 2: Targeted Command-Line Execution (For QA Engineers & CI/CD)
```bash
# Run full regression suite across all 4 modules:
./mvnw clean test

# Run a specific module:
./mvnw test -Dtest=EmployeeModuleTest
./mvnw test -Dtest=ManagerModuleTest
./mvnw test -Dtest=PMModuleTest
./mvnw test -Dtest=AdminModuleTest

# Run Headless mode (for background or CI pipeline execution):
./mvnw clean test -Dheadless=true
```

### Tier 3: IDE Direct Execution (For Developers)
- Open project in IntelliJ IDEA, VS Code, or Eclipse.
- Right-click `testng.xml` -> Select **Run 'testng.xml'**.

---

## 5. Failure Handling, Diagnostics & Reporting

```
                        ┌─────────────────────────────────┐
                        │       Test Execution Step       │
                        └────────────────┬────────────────┘
                                         │
                                [ Assertion Fails? ]
                                  /            \
                             YES /              \ NO
                                v                v
            ┌───────────────────────────┐    ┌───────────────────────────┐
            │  TestListener Intercepts  │    │      Mark Test PASSED     │
            └─────────────┬─────────────┘    └─────────────┬─────────────┘
                          │                                │
                          v                                v
            ┌───────────────────────────┐    ┌───────────────────────────┐
            │ ScreenshotUtils Captures  │    │ Aggregate to TestNG Report│
            │ Full DOM Screen (PNG) in  │    │  (index.html / surefire)  │
            │   target/screenshots/     │    └───────────────────────────┘
            └─────────────┬─────────────┘
                          │
                          v
            ┌───────────────────────────┐
            │  Error Stack Trace Logged │
            │  Driver Cleaned in @After │
            └───────────────────────────┘
```

1. **Automatic Failure Capture:**
   - Any assertion failure or unexpected exception triggers `TestListener.onTestFailure()`.
   - `ScreenshotUtils.java` captures a full-resolution timestamped PNG screenshot saved directly to `target/screenshots/`.
2. **Surefire HTML Execution Reports:**
   - Generated at `target/surefire-reports/index.html`.
   - Provides granular execution times, pass/fail counts, assertion logs, and system environment metadata.
3. **Module Defect Quality Reports (PDF):**
   - Four executive-grade PDF reports are maintained in the root directory documenting all discovered bugs with root cause analyses, reproduction steps, and remediation code pointers.

---

## 6. Module-by-Module Automation Script Breakdown

---

### Module 1: Standard Employee (`shubham.shinde@setoo.co`)
* **Test Classes:** `EmployeeModuleTest.java`, `EmployeeTimesheetWorkflowTest.java`, `EmployeeAuthenticationTest.java`, `EmployeePermissionsTest.java`
* **Page Objects:** `EmployeeTimesheetsPage.java`, `EmployeeDashboardPage.java`, `EmployeeTasksPage.java`, `EmployeeAllocationPage.java`, `EmployeeCalendarPage.java`, `SubmitWeekModal.java`
* **Core Automated Scenarios:**
  1. **Authentication & Session:** Valid login, invalid credentials error handling, and password eye toggle visibility.
  2. **Timesheet Entry Workflows:** Day-by-day cell input (Mon–Sun), dynamic weekly total hours calculation, billable vs. non-billable categorization.
  3. **Task & Sync Operations:** "Sync My Tasks" integration, project task row injection, and "Save draft" persistence.
  4. **Submission Integrity:** "Submit week" confirmation modal, empty/zero-hour validation guardrails, and post-submission lockouts.
  5. **Role View Permissions:** Navigational access to My Tasks, My Allocation, and Personal Calendar; verification that administrative routes remain hidden.

---

### Module 2: Reporting Manager (`rutuja@setoo.co`)
* **Test Classes:** `ManagerModuleTest.java`
* **Page Objects:** `ManagerDashboardPage.java`, `ManagerApprovalsPage.java`, `ManagerReportsPage.java`, `ManagerTeamPage.java`
* **Core Automated Scenarios:**
  1. **Approvals Queue Processing:** Review of pending weekly timesheets submitted by direct reportees across project teams.
  2. **Batch Actions:** Selection of multiple timesheets via table checkboxes, batch approval triggers, and batch rejection workflows.
  3. **Rejection Modal Guardrails (DEF-MGR-001):** Verification of mandatory feedback comment prompts upon rejection.
  4. **Team Resource Directory:** Member list rendering, assigned roles, department tags, and direct contact details.
  5. **Resource Management:** Bandwidth allocation overview, team capacity utilization gauges, and utilization thresholds.
  6. **Reports & Exports:** Filter timesheet submissions by date range and trigger CSV/Excel report downloads.

---

### Module 3: Project Manager / PM (`rohan@setoo.co`)
* **Test Classes:** `PMModuleTest.java`
* **Page Objects:** `PMDashboardPage.java`, `PMProjectsPage.java`, `PMBillingPage.java`
* **Core Automated Scenarios:**
  1. **Project Portfolio Oversight:** Active project listings, health status indicators (On Track, At Risk, Delayed), and sprint burndown graphs.
  2. **Billing & Budget Dashboard (`/billing`):** Planned budget vs. consumed budget calculations, revenue tracking, and billable hour metrics.
  3. **Financial Stat Cards:** Integrity of "Extra Burned" hours calculations (validating against double-counting defects documented in `DEF-PM-001`).
  4. **Security & Mutation Guardrails (TC 1.3):** Verification that Project Managers have read-only visibility into allocated budgets and cannot unilaterally mutate financial contract parameters without Administrative authorization.

---

### Module 4: Administrator (`darshan@setoo.co`)
* **Test Classes:** `AdminModuleTest.java`, `AdminRejectionWorkflowTest.java`, `AccessControlAndPermissionsTest.java`
* **Page Objects:** `AdminDashboardPage.java`, `AdminSettingsPage.java`
* **Core Automated Scenarios:**
  1. **Master Organizational Privileges:** Verification of Administrator badge, global oversight cards (Active Users, Active Projects, ADO Mapped status, Pending Approvals).
  2. **Multi-Tab Administration Analytics:** Asynchronous switching between `Overview`, `Approvals Analytics`, and `Pending Submissions`.
  3. **System Settings & Governance (`/settings`):** Organizational parameters, working hours baselines, and currency settings.
  4. **Strict Role-Based Access Control (RBAC):** Security negative testing confirming that lower-tier roles (Employee, PM, Manager) cannot access `/administration` or mutate organizational master data.

---

## 7. Quality Defect Summary & Artifacts

Through the automated suite and exploratory audits, 17 distinct functional defects and quality improvements were identified and compiled into module-specific PDF deliverables:

| Module Report | Defect Count | Highest Severity | Key Defect Highlight |
|---|---|---|---|
| 📄 **Employee Module** | 4 Defects | High | `DEF-EMP-001`: Lack of Absence Justification modal on 0-hour submissions. |
| 📄 **Manager Module** | 4 Defects | High | `DEF-MGR-001`: Batch rejection lacks mandatory reason/comment prompt. |
| 📄 **Project Manager Module** | 4 Defects | High | `DEF-PM-001`: Extra burned hours calculation discrepancy on `/billing`. |
| 📄 **Admin Module** | 5 Defects | High | `DEF-ADM-001`: Settings form lacks optimistic locking/concurrency control. |

---

## 8. Summary & Next Recommendations

1. **Repository:** The complete codebase is maintained and version-controlled at:  
   👉 **[github.com/darshanhujband8055/timeflow-selenium-automation](https://github.com/darshanhujband8055/timeflow-selenium-automation)**
2. **Continuous Integration (CI/CD):** Integrate `mvn clean test -Dheadless=true` into an Azure DevOps Pipeline (`azure-pipelines.yml`) to automatically trigger regression runs upon every frontend release.
3. **Defect Sync:** Synchronize the 17 identified defects to Azure DevOps Boards under the `Timeflow timesheet` project backlog.
