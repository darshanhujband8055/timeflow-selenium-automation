import os
import sys
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.pdfgen import canvas

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super(NumberedCanvas, self).__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super(NumberedCanvas, self).showPage()
        super(NumberedCanvas, self).save()

    def draw_page_decorations(self, page_count):
        self.saveState()
        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        
        # Header (pages > 1)
        if self._pageNumber > 1:
            self.drawString(54, letter[1] - 36, "TIMEFLOW TEST AUTOMATION FRAMEWORK — TECHNICAL & ARCHITECTURAL REPORT")
            self.setStrokeColor(colors.HexColor("#E2E8F0"))
            self.setLineWidth(0.5)
            self.line(54, letter[1] - 42, letter[0] - 54, letter[1] - 42)
            
        # Footer
        page_text = f"Page {self._pageNumber} of {page_count}"
        self.drawRightString(letter[0] - 54, 30, page_text)
        self.drawString(54, 30, "CONFIDENTIAL — FOR ENGINEERING LEADERSHIP & QUALITY ASSURANCE")
        self.setStrokeColor(colors.HexColor("#E2E8F0"))
        self.setLineWidth(0.5)
        self.line(54, 40, letter[0] - 54, 40)
        self.restoreState()

def build_technical_pdf(filename="Timeflow_Automation_Technical_Architecture_Report.pdf"):
    doc = SimpleDocTemplate(
        filename,
        pagesize=letter,
        leftMargin=54,
        rightMargin=54,
        topMargin=54,
        bottomMargin=54
    )

    styles = getSampleStyleSheet()

    # Custom styles
    title_style = ParagraphStyle(
        'TechReportTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=22,
        leading=26,
        textColor=colors.HexColor("#0F172A")
    )

    subtitle_style = ParagraphStyle(
        'TechReportSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor("#475569")
    )

    h1_style = ParagraphStyle(
        'TechH1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=14,
        leading=18,
        textColor=colors.HexColor("#1E293B"),
        spaceAfter=6,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'TechH2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=colors.HexColor("#2563EB"),
        spaceAfter=4,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'TechBody',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=colors.HexColor("#334155")
    )

    bullet_style = ParagraphStyle(
        'TechBullet',
        parent=body_style,
        leftIndent=12,
        bulletIndent=4,
        spaceAfter=3
    )

    table_header_style = ParagraphStyle(
        'TechTH',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=colors.white
    )

    table_cell_style = ParagraphStyle(
        'TechTD',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor("#1E293B")
    )

    table_cell_bold = ParagraphStyle(
        'TechTDBold',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor("#0F172A")
    )

    story = []

    # Title Block
    story.append(Paragraph("Timeflow Test Automation Framework", title_style))
    story.append(Spacer(1, 4))
    story.append(Paragraph("Comprehensive Technical & Architectural Engineering Report", subtitle_style))
    story.append(Spacer(1, 10))

    # Meta banner table
    meta_data = [
        [
            Paragraph("<b>Target Application:</b>", table_cell_bold),
            Paragraph("https://timeflow.setoo.in", table_cell_style),
            Paragraph("<b>Build / Suite Status:</b>", table_cell_bold),
            Paragraph("<font color='#16A34A'><b>52 Passed / 0 Failed (100%)</b></font>", table_cell_style),
        ],
        [
            Paragraph("<b>Primary Language:</b>", table_cell_bold),
            Paragraph("Java 17 (LTS)", table_cell_style),
            Paragraph("<b>Automation Core:</b>", table_cell_bold),
            Paragraph("Selenium WebDriver 4.29.0", table_cell_style),
        ],
        [
            Paragraph("<b>Test Runner:</b>", table_cell_bold),
            Paragraph("TestNG 7.10.2", table_cell_style),
            Paragraph("<b>Design Pattern:</b>", table_cell_bold),
            Paragraph("Page Object Model (POM)", table_cell_style),
        ],
        [
            Paragraph("<b>Git Repository:</b>", table_cell_bold),
            Paragraph("github.com/darshanhujband8055/timeflow-selenium-automation", table_cell_style),
            Paragraph("<b>ALM / Project:</b>", table_cell_bold),
            Paragraph("Azure DevOps (setoo-ai / Timeflow)", table_cell_style),
        ]
    ]

    meta_table = Table(meta_data, colWidths=[110, 160, 110, 124])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, -1), colors.HexColor("#F8FAFC")),
        ('BOX', (0, 0), (-1, -1), 0.5, colors.HexColor("#CBD5E1")),
        ('INNERGRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0, 0), (-1, -1), 5),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 5),
        ('LEFTPADDING', (0, 0), (-1, -1), 7),
        ('RIGHTPADDING', (0, 0), (-1, -1), 7),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14))

    # Section 1: Executive Overview
    story.append(Paragraph("1. Executive Summary", h1_style))
    story.append(Paragraph(
        "The Timeflow Selenium Automation Framework is an enterprise-grade quality assurance solution designed "
        "to validate end-to-end user workflows, access security, and business invariants across all 4 user roles "
        "on the Setoo Timeflow application. The framework was engineered with a primary focus on <b>zero-friction portability</b>: "
        "any developer, QA, or manager can clone the repository and run all 52 tests locally via a 1-click batch runner "
        "without manually configuring build runtimes or browser binaries.",
        body_style
    ))
    story.append(Spacer(1, 12))

    # Section 2: Tools & Services Stack
    story.append(Paragraph("2. Tools, Technologies & Services Stack", h1_style))
    stack_data = [
        [Paragraph("Category", table_header_style), Paragraph("Technology / Tool", table_header_style), Paragraph("Version / Role", table_header_style), Paragraph("Technical Significance", table_header_style)],
        [Paragraph("Language", table_cell_bold), Paragraph("Java", table_cell_style), Paragraph("17 (LTS)", table_cell_style), Paragraph("Provides strong static typing, OOP reliability, and enterprise stability.", table_cell_style)],
        [Paragraph("Core Automation", table_cell_bold), Paragraph("Selenium WebDriver", table_cell_style), Paragraph("4.29.0", table_cell_style), Paragraph("Automates Chrome, Edge, and Firefox. Bundled Selenium Manager manages driver binaries automatically.", table_cell_style)],
        [Paragraph("Test Runner", table_cell_bold), Paragraph("TestNG", table_cell_style), Paragraph("7.10.2", table_cell_style), Paragraph("Drives test suite orchestration, lifecycle hooks, assertions, and test listeners.", table_cell_style)],
        [Paragraph("Build Automation", table_cell_bold), Paragraph("Apache Maven", table_cell_style), Paragraph("3.9+ & mvnw", table_cell_style), Paragraph("Build lifecycle, dependency management, and Surefire execution reporting.", table_cell_style)],
        [Paragraph("Target Frontend", table_cell_bold), Paragraph("Next.js / React", table_cell_style), Paragraph("SPA / Radix UI", table_cell_style), Paragraph("Modern web app with async hydration, interactive tab navigation, and client modals.", table_cell_style)],
        [Paragraph("Target Backend", table_cell_bold), Paragraph("PostgreSQL / Node.js", table_cell_style), Paragraph("Supabase / REST", table_cell_style), Paragraph("Relational data store persisting timesheets, project budgets, and user entitlements.", table_cell_style)],
        [Paragraph("Authentication", table_cell_bold), Paragraph("JWT / OAuth", table_cell_style), Paragraph("Session Guard", table_cell_style), Paragraph("Role-gated sessions across Employee, Manager, PM, and Administrator.", table_cell_style)],
        [Paragraph("Version Control", table_cell_bold), Paragraph("Git & GitHub", table_cell_style), Paragraph("main branch", table_cell_style), Paragraph("Centralized repository with automated wrapper scripts and documentation.", table_cell_style)],
        [Paragraph("ALM Integration", table_cell_bold), Paragraph("Azure DevOps", table_cell_style), Paragraph("setoo-ai", table_cell_style), Paragraph("Connected to project 'Timeflow timesheet' for work item alignment and bug tracking.", table_cell_style)]
    ]

    stack_table = Table(stack_data, colWidths=[90, 110, 85, 219])
    stack_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#1E293B")),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
    ]))
    story.append(stack_table)
    story.append(Spacer(1, 14))

    # Section 3: Architecture & Design Pattern
    story.append(Paragraph("3. Framework Architecture & Design Pattern", h1_style))
    story.append(Paragraph(
        "The framework strictly follows the industry-standard <b>Page Object Model (POM)</b> architecture, cleanly "
        "separating element locators, user actions, and test verification logic across discrete modular layers:",
        body_style
    ))
    story.append(Spacer(1, 6))

    story.append(Paragraph("• <b>Page Object Layer:</b> Encapsulates page elements (`private final By ...`) and reusable interaction flows within dedicated classes (e.g. `EmployeeTimesheetsPage`, `PMBillingPage`, `ManagerApprovalsPage`). Locators are isolated so UI changes require updates in only one file.", bullet_style))
    story.append(Paragraph("• <b>ThreadLocal Driver Management (`DriverManager.java`):</b> Wraps WebDriver inside a `ThreadLocal<WebDriver>` container to ensure thread safety, complete test isolation, and seamless multi-threaded parallel test capability.", bullet_style))
    story.append(Paragraph("• <b>Dynamic Synchronization (`BasePage.java`):</b> Replaces brittle hardcoded pauses with intelligent explicit synchronization using `WebDriverWait` and `ExpectedConditions` (`elementToBeClickable`, `visibilityOf`, `invisibilityOfElementLocated`).", bullet_style))
    story.append(Paragraph("• <b>Centralized Configuration (`config.properties`):</b> Allows dynamic runtime toggling of target browser (chrome/edge/firefox), application URL, headless mode (`-Dheadless=true`), and timeout thresholds without modifying source code.", bullet_style))
    story.append(Spacer(1, 12))

    # Page Break for clean reading
    story.append(PageBreak())

    # Section 4: Execution Workflow & 1-Click Portability
    story.append(Paragraph("4. Execution Workflow & Zero-Setup Portability", h1_style))
    story.append(Paragraph(
        "To enable managers and non-technical stakeholders to easily run and review tests, the framework is "
        "bundled with multiple zero-setup execution mechanisms:",
        body_style
    ))
    story.append(Spacer(1, 6))

    exec_data = [
        [Paragraph("Execution Method", table_header_style), Paragraph("Command / Action", table_header_style), Paragraph("Target Audience & Workflow", table_header_style)],
        [Paragraph("<b>1-Click Windows Runner</b>", table_cell_bold), Paragraph("Double-click <code>run-all-tests.bat</code>", table_cell_style), Paragraph("Ideal for managers. Bundled Maven Wrapper handles setup automatically and launches the full suite in Google Chrome.", table_cell_style)],
        [Paragraph("<b>1-Click Mac/Linux Runner</b>", table_cell_bold), Paragraph("<code>./run-all-tests.sh</code>", table_cell_style), Paragraph("Shell script for macOS and Linux workstations with execution permissions pre-configured.", table_cell_style)],
        [Paragraph("<b>Maven CLI (Full Suite)</b>", table_cell_bold), Paragraph("<code>./mvnw clean test</code>", table_cell_style), Paragraph("Executes all 52 tests mapped in <code>testng.xml</code> across all 4 business modules.", table_cell_style)],
        [Paragraph("<b>Headless Mode (CI/CD)</b>", table_cell_bold), Paragraph("<code>./mvnw test -Dheadless=true</code>", table_cell_style), Paragraph("Runs silently in the background without UI popups—ready for Azure DevOps CI pipelines.", table_cell_style)],
        [Paragraph("<b>Module-Specific Run</b>", table_cell_bold), Paragraph("<code>./mvnw test -Dtest=ManagerModuleTest</code>", table_cell_style), Paragraph("Selectively executes only the tests for a target module or role.", table_cell_style)],
        [Paragraph("<b>IDE Execution</b>", table_cell_bold), Paragraph("Right-click <code>testng.xml</code> -> Run", table_cell_style), Paragraph("For developers and SDETs working in IntelliJ IDEA, VS Code, or Eclipse.", table_cell_style)]
    ]

    exec_table = Table(exec_data, colWidths=[120, 160, 224])
    exec_table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.HexColor("#1E293B")),
        ('GRID', (0, 0), (-1, -1), 0.5, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0, 0), (-1, -1), 4),
        ('BOTTOMPADDING', (0, 0), (-1, -1), 4),
        ('LEFTPADDING', (0, 0), (-1, -1), 6),
        ('RIGHTPADDING', (0, 0), (-1, -1), 6),
        ('ROWBACKGROUNDS', (0, 1), (-1, -1), [colors.white, colors.HexColor("#F8FAFC")]),
    ]))
    story.append(exec_table)
    story.append(Spacer(1, 14))

    # Section 5: Failure Handling, Diagnostics & Reporting
    story.append(Paragraph("5. Failure Handling & Diagnostic Strategy", h1_style))
    story.append(Paragraph(
        "Robust automated diagnostics ensure that any regression is immediately actionable for developers:",
        body_style
    ))
    story.append(Spacer(1, 6))
    story.append(Paragraph("• <b>Automated Failure Screenshots:</b> Integrated via TestNG's `ITestListener` (`TestListener.java`). Any unexpected assertion or timeout failure automatically captures a full-screen timestamped PNG screenshot saved directly to `target/screenshots/`.", bullet_style))
    story.append(Paragraph("• <b>Surefire HTML Reports:</b> Generated at `target/surefire-reports/index.html` detailing test class run times, method parameters, failure traces, and aggregate pass rates.", bullet_style))
    story.append(Paragraph("• <b>Driver Auto-Cleanup:</b> The `@AfterMethod` lifecycle hook guarantees that browser windows and driver instances terminate cleanly even if assertions fail, preventing orphaned processes from leaking machine memory.", bullet_style))
    story.append(Spacer(1, 14))

    # Section 6: Module Breakdown
    story.append(Paragraph("6. Module-by-Module Automation Script Breakdown", h1_style))
    story.append(Spacer(1, 4))

    # Employee
    story.append(Paragraph("Module A: Standard Employee Role (shubham.shinde@setoo.co)", h2_style))
    story.append(Paragraph(
        "<b>Test Classes:</b> <code>EmployeeModuleTest.java</code>, <code>EmployeeTimesheetWorkflowTest.java</code>, <code>EmployeeAuthenticationTest.java</code>, <code>EmployeePermissionsTest.java</code><br/>"
        "<b>Scope:</b> Daily, Weekly, and Monthly timesheet views; 'Sync My Tasks' project integration; hours logging across task rows; dynamic weekly total calculation; 'Save draft' persistence; 'Submit week' modal guardrails; navigation to My Tasks, My Allocation, and Personal Calendar; negative security tests ensuring admin/billing routes are forbidden.",
        body_style
    ))
    story.append(Spacer(1, 8))

    # Manager
    story.append(Paragraph("Module B: Reporting Manager Role (rutuja@setoo.co)", h2_style))
    story.append(Paragraph(
        "<b>Test Classes:</b> <code>ManagerModuleTest.java</code><br/>"
        "<b>Scope:</b> Approvals queue processing; table checkbox multi-select; batch approval workflows; batch rejection workflows; rejection feedback comment enforcement (DEF-MGR-001 audit); Team Directory member profiles; Bandwidth allocation gauges; capacity utilization thresholds; custom date range filtering and CSV report exports.",
        body_style
    ))
    story.append(Spacer(1, 8))

    # PM
    story.append(Paragraph("Module C: Project Manager Role (rohan@setoo.co)", h2_style))
    story.append(Paragraph(
        "<b>Test Classes:</b> <code>PMModuleTest.java</code><br/>"
        "<b>Scope:</b> Project portfolio overview; project health status badges; sprint velocity metrics; Billing & Budget dashboard (<code>/billing</code>); financial stat cards (Planned vs. Consumed hours, Revenue); Extra Burned calculation verification (DEF-PM-001 audit); mutation authorization guardrails (TC 1.3 - PMs restricted from altering financial baselines).",
        body_style
    ))
    story.append(Spacer(1, 8))

    # Admin
    story.append(Paragraph("Module D: Administrator Role (darshan@setoo.co)", h2_style))
    story.append(Paragraph(
        "<b>Test Classes:</b> <code>AdminModuleTest.java</code>, <code>AdminRejectionWorkflowTest.java</code>, <code>AccessControlAndPermissionsTest.java</code><br/>"
        "<b>Scope:</b> Full master privilege verification; organizational overview cards (Active Users, Active Projects, ADO Mapped status, Pending Approvals); tab navigation (Overview, Approvals Analytics, Pending Submissions); Organization Settings (<code>/settings</code>); global security RBAC negative tests ensuring lower roles cannot access admin routes.",
        body_style
    ))
    story.append(Spacer(1, 14))

    # Section 7: Summary & Recommendations
    story.append(Paragraph("7. Defect Audits & Next Steps", h1_style))
    story.append(Paragraph(
        "A total of <b>17 functional defects</b> across all 4 modules were identified during framework execution "
        "and exploratory testing. Each defect has been cataloged with reproduction steps, root cause analysis, and "
        "developer remediation code in the module PDF reports. "
        "The complete test framework is available on GitHub at "
        "<b>https://github.com/darshanhujband8055/timeflow-selenium-automation</b>.",
        body_style
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Technical PDF generated successfully: {filename}")

if __name__ == "__main__":
    build_technical_pdf()
