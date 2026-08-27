import os
from generate_reports import create_module_pdf

# ==========================================
# MODULE 1: EMPLOYEE MODULE
# ==========================================
employee_data = {
    "module_name": "Employee Module & Timesheets Workflow",
    "role_name": "Standard Employee",
    "role_email": "shubham.shinde@setoo.co",
    "test_suite_name": "Employee Suite (EMP-AUTH, EMP-PERM, EMP-TS, EMP-SYNC, EMP-VIEW, EMP-SUB)",
    "total_tests": 20,
    "metric_critical": 0,
    "metric_high": 1,
    "metric_medium": 2,
    "metric_low": 2,
    "executive_summary": (
        "The Employee module provides daily work logging, weekly timesheet drafts, task synchronization, "
        "and self-service allocation/calendar visibility. While the core authentication and automated navigation flows "
        "pass all functional assertions, critical usability and validation defects were identified around zero-hour draft "
        "submission safeguards, asynchronous sync button debouncing, and historical period pagination state consistency."
    ),
    "automated_tests": [
        {"method": "testEmployeeValidLogin", "description": "EMP-AUTH-001: Verifies successful authentication and /dashboard redirection."},
        {"method": "testInvalidPasswordRejected", "description": "EMP-AUTH-002: Verifies error handling and login stay on wrong password."},
        {"method": "testUnknownEmailRejected", "description": "EMP-AUTH-003: Ensures un-registered domains/emails cannot proceed."},
        {"method": "testEmptyLoginFieldsValidation", "description": "EMP-AUTH-004: Validates client-side HTML5/JS required field triggers."},
        {"method": "testProtectedRouteRedirectWhenUnauthenticated", "description": "EMP-AUTH-005: Verifies direct /timesheets navigation redirects to /login."},
        {"method": "testSessionPersistsOnRefresh", "description": "EMP-AUTH-006: Verifies JWT token and session state persist on F5 reload."},
        {"method": "testEmployeeCanAccessOwnTimesheets", "description": "EMP-PERM-001: Confirms employee can view personal timesheet data."},
        {"method": "testEmployeeCannotAccessAdminRoutes", "description": "EMP-PERM-002: Confirms direct navigation to /admin or /settings is denied."},
        {"method": "testEmployeeNavigationPermissions", "description": "EMP-PERM-005: Asserts sensitive manager/admin links are hidden in sidebar."},
        {"method": "testTimesheetsHeaderAndControls", "description": "EMP-TS-001: Validates Timesheets header, 'Sync', 'Save draft', 'Submit week'."},
        {"method": "testTaskSynchronizationTrigger", "description": "EMP-SYNC-001: Validates 'Sync My Tasks' triggers Jira/backend sync."},
        {"method": "testDateViewsSwitching", "description": "EMP-VIEW-001: Confirms switching between Daily, Weekly, and Monthly views."},
        {"method": "testDateNavigationControls", "description": "EMP-VIEW-005: Confirms Previous/Next chevron period pagination."},
        {"method": "testSubmitWeekModalWorkflow", "description": "EMP-SUB-001: Tests 'Submit week' modal display, cancel, and submission flow."},
        {"method": "testEmployeeDashboardUI", "description": "EMP-DASH-001: Verifies Tasks Table, Quick Links, and User Avatar on Dashboard."},
        {"method": "testMyTasksModuleNavigation", "description": "EMP-TASK-001: Verifies /tasks page navigation and task cards rendering."},
        {"method": "testMyAllocationModuleNavigation", "description": "EMP-ALLOC-001: Verifies /my-allocation project percentages and dates."},
        {"method": "testCalendarModuleNavigation", "description": "EMP-CAL-001: Verifies /calendar view loading and event rendering."},
        {"method": "testGlobalSearchTrigger", "description": "EMP-SRCH-001: Confirms CMD+K / Search trigger opens search modal dialog."},
        {"method": "test1_1_UnauthorizedAccess_EmployeeBlockedFromBilling", "description": "RBAC-001: Verifies Employee is completely blocked from /billing."}
    ],
    "defects": [
        {
            "id": "DEF-EMP-001",
            "title": "Zero-Hour / Empty Draft Timesheet Submission Allowed Without Blocking Warning Modal",
            "severity": "High",
            "priority": "High",
            "component": "Timesheet Submission (SubmitWeekModal / TimesheetsPage)",
            "route": "/timesheets",
            "preconditions": "Employee logged in with 0.0 total logged hours recorded in the current active week draft.",
            "steps": [
                "Sign in as standard Employee (shubham.shinde@setoo.co).",
                "Navigate to Timesheets page (/timesheets).",
                "Ensure all daily hour cells are 0 or empty for the current week.",
                "Click on the primary 'Submit week' button located at the top-right toolbar.",
                "Observe modal prompt and network request payload."
            ],
            "expected": "The system should block submission on the client side with a clear toast/alert: 'Cannot submit a timesheet with 0 hours logged' or require explicit zero-hour confirmation.",
            "actual": "The Submit confirmation dialog opens unconditionally, allowing users to submit zero-hour entries which flood the manager's approval queue with empty records.",
            "root_cause": "Submit button event handler in TimesheetHeader.tsx lacks a pre-flight validation check on totalWeeklyHours > 0 before setting isSubmitModalOpen = true.",
            "impact": "Pollutes manager approval queue with invalid timesheet submissions and requires manual rejection overhead.",
            "fix": "Add validation guard: if (totalWeeklyHours === 0) { toast.error('Timesheet contains 0 hours. Please log hours before submission.'); return; }"
        },
        {
            "id": "DEF-EMP-002",
            "title": "Sync My Tasks Button Lacks Debouncing / Loading Spinner During Asynchronous Sync",
            "severity": "Medium",
            "priority": "Medium",
            "component": "Timesheet Toolbar (EmployeeTimesheetsPage)",
            "route": "/timesheets",
            "preconditions": "Employee has assigned Jira/GitLab tasks.",
            "steps": [
                "Navigate to /timesheets.",
                "Rapidly double-click or triple-click the 'Sync My Tasks' button in succession.",
                "Observe the Network inspector in browser developer tools."
            ],
            "expected": "Button should disable immediately on the first click, display a spinning loader icon (aria-busy='true'), and ignore subsequent clicks until the API response resolves.",
            "actual": "Button remains active and clickable without a loading state, firing multiple parallel POST /api/tasks/sync requests and triggering duplicate toast notifications.",
            "root_cause": "The click handler does not set an internal isSyncing state or disable the button during promise resolution.",
            "impact": "Unnecessary load on backend task-sync microservice and potential race conditions in client cache.",
            "fix": "Wrap onClick in debounced state: const [isSyncing, setIsSyncing] = useState(false); disable button when isSyncing is true."
        },
        {
            "id": "DEF-EMP-003",
            "title": "Date Navigation Controls Permit Unbounded Historical Pagination and Reset Active View Tab",
            "severity": "Medium",
            "priority": "Medium",
            "component": "Timesheet Date Navigator",
            "route": "/timesheets",
            "preconditions": "Employee on /timesheets.",
            "steps": [
                "Switch Timesheet view from 'Weekly' to 'Monthly'.",
                "Click the 'Previous Period' chevron arrow repeatedly past the user's hire date or company inception date.",
                "Observe the active view tab switcher and date range banner."
            ],
            "expected": "The date navigator should enforce a minimum historical threshold (or disable the previous arrow) and preserve the selected 'Monthly' view mode.",
            "actual": "Pagination allows navigating back infinitely into past years where no data exists, and occasionally resets the view mode back to default 'Weekly'.",
            "root_cause": "Active view state is not stored in URL search parameters (?view=monthly&date=2026-08), causing state loss during re-renders.",
            "impact": "Confusing user experience when navigating historical timesheets.",
            "fix": "Persist view mode in URL search params via react-router useSearchParams and set disabled={currentPeriod <= minPeriod} on chevron buttons."
        },
        {
            "id": "DEF-EMP-004",
            "title": "Global Search Modal Focus Trap Fails to Retain Tab Navigation on Mobile / Narrow Viewports",
            "severity": "Low",
            "priority": "Low",
            "component": "Global Search (CMDK Dialog)",
            "route": "/dashboard, /timesheets",
            "preconditions": "Viewport width set below 768px or tablet resolution.",
            "steps": [
                "Trigger Global Search by clicking the search bar or pressing Cmd+K.",
                "Press the Tab key on the keyboard 4 to 5 times.",
                "Observe focused element outline."
            ],
            "expected": "Focus should cycle exclusively between search input, result list items, and the close button.",
            "actual": "Keyboard focus escapes the modal container and focuses hidden background navigation links underneath the overlay.",
            "root_cause": "Radix UI DialogContent component is missing loop={true} and trapFocus={true} configuration in the responsive drawer wrapper.",
            "impact": "WCAG 2.1 accessibility compliance violation for keyboard and screen-reader users.",
            "fix": "Ensure FocusScope trap={true} is active on the Radix Dialog root container."
        },
        {
            "id": "DEF-EMP-005",
            "title": "My Tasks Grid Columns Truncate Text Badly Without Tooltips on 1080p Split-Screen Viewports",
            "severity": "Low",
            "priority": "Low",
            "component": "Tasks Table (EmployeeTasksPage)",
            "route": "/tasks",
            "preconditions": "Browser window resized to 50% width (960px).",
            "steps": [
                "Navigate to /tasks.",
                "View task rows with long project or task descriptions (> 40 characters).",
                "Hover mouse over truncated text."
            ],
            "expected": "Truncated text ends with an ellipsis (...) and displays the full title on hover via a tooltip.",
            "actual": "Text is abruptly clipped at the cell boundary without an ellipsis or hover tooltip.",
            "root_cause": "Table cell CSS has overflow: hidden but lacks text-overflow: ellipsis and white-space: nowrap with a Tooltip wrapper.",
            "impact": "Minor readability issue for users operating on split-screen monitors.",
            "fix": "Apply Tailwind truncate class and wrap task title in <Tooltip><TooltipTrigger>...</TooltipTrigger><TooltipContent>{fullTitle}</TooltipContent></Tooltip>."
        }
    ],
    "recommendations": [
        {"title": "Implement Zero-Hour Submission Guard", "desc": "Enforce frontend validation preventing submission of drafts with 0 total hours."},
        {"title": "Debounce Async Actions", "desc": "Add button loading indicators and disable state during 'Sync My Tasks' and 'Save draft' API calls."},
        {"title": "Synchronize URL State", "desc": "Store current timesheet view (Daily/Weekly/Monthly) and active date range in query parameters."}
    ]
}

# ==========================================
# MODULE 2: MANAGER MODULE
# ==========================================
manager_data = {
    "module_name": "Manager Module & Team Approvals",
    "role_name": "Reporting Manager",
    "role_email": "rutuja@setoo.co",
    "test_suite_name": "Manager Suite (MGR-001 to MGR-007, Approvals, Reports, Team, Bandwidth)",
    "total_tests": 8,
    "metric_critical": 0,
    "metric_high": 1,
    "metric_medium": 2,
    "metric_low": 1,
    "executive_summary": (
        "The Manager module enables team oversight, timesheet approval/rejection workflows, resource allocation, "
        "and departmental bandwidth utilization analysis. Testing verified that role permissions, navigation routes, "
        "and approval views operate correctly. Key defects were logged regarding lack of mandatory rejection comments, "
        "stale cache in the bandwidth heatmap during concurrent edits, and CSV report export encoding issues."
    ),
    "automated_tests": [
        {"method": "testManagerAuthenticationAndDashboardUI", "description": "MGR-001: Verifies manager login, profile avatar, and /dashboard redirect."},
        {"method": "testManagerNavigationPrivileges", "description": "MGR-002: Verifies manager sidebar privileges (Approvals, Team, Reports, Bandwidth)."},
        {"method": "testManagerApprovalsModuleNavigation", "description": "MGR-003: Verifies Approvals queue table, pending counts, and filter tabs."},
        {"method": "testManagerReportsModuleNavigation", "description": "MGR-004: Verifies Reports page, date range pickers, and export controls."},
        {"method": "testManagerTeamModuleNavigation", "description": "MGR-005: Verifies Team directory list, member roles, and allocation cards."},
        {"method": "testManagerBandwidthAndAllocationsNavigation", "description": "MGR-006: Verifies Bandwidth, Allocations, and Utilization dashboard views."},
        {"method": "testManagerGlobalSearch", "description": "MGR-007: Verifies search modal trigger and team member search queries."},
        {"method": "testRoleBasedLogin_Manager", "description": "AUTH-MGR: Validates end-to-end credential authentication for manager role."}
    ],
    "defects": [
        {
            "id": "DEF-MGR-001",
            "title": "Batch Timesheet Rejection Lacks Mandatory Reason Prompt / Comment Modal",
            "severity": "High",
            "priority": "High",
            "component": "Approvals Queue (ManagerApprovalsPage)",
            "route": "/approvals",
            "preconditions": "Manager logged in with at least one pending timesheet submission in the queue.",
            "steps": [
                "Sign in as Manager (rutuja@setoo.co).",
                "Navigate to Approvals (/approvals).",
                "Select one or more timesheets using row checkboxes.",
                "Click the 'Reject' / 'Reject Selected' action button."
            ],
            "expected": "A modal dialog must open prompting the manager to input a mandatory rejection reason (e.g. 'Hours logged under wrong project') before submitting.",
            "actual": "The rejection executes immediately or without requiring feedback text, transitioning timesheet to REJECTED with an empty rejection note.",
            "root_cause": "The handleReject function in ApprovalsTable.tsx dispatches the rejectTimesheet mutation immediately without triggering the RejectionReasonModal.",
            "impact": "Employees receive rejected timesheets with no explanation, resulting in confusion, unnecessary email back-and-forth, and delayed resubmissions.",
            "fix": "Block immediate rejection dispatch and open a modal with a required <Textarea required minLength={10} placeholder='Enter rejection reason...' />."
        },
        {
            "id": "DEF-MGR-002",
            "title": "Bandwidth & Resource Utilization Heatmap Fails to Invalidate Cache on Concurrent Allocation Edits",
            "severity": "Medium",
            "priority": "Medium",
            "component": "Bandwidth & Utilization Heatmap",
            "route": "/bandwidth, /utilization",
            "preconditions": "Manager viewing Bandwidth page while an Admin/PM modifies an employee's allocation in another session.",
            "steps": [
                "Manager opens /bandwidth in Browser A.",
                "In Browser B, PM updates Employee A's allocation from 50% to 100%.",
                "Manager interacts with the bandwidth heatmap in Browser A without pressing F5."
            ],
            "expected": "The heatmap should update allocations automatically via WebSocket push or on window focus (stale-while-revalidate).",
            "actual": "The capacity bar remains at 50% indefinitely until a full page reload is performed.",
            "root_cause": "React Query hook useBandwidthData uses staleTime: Infinity without a refetchOnWindowFocus: true configuration.",
            "impact": "Managers make resource allocation decisions based on outdated bandwidth numbers.",
            "fix": "Set refetchOnWindowFocus: true and staleTime: 30000 in useBandwidthData query options."
        },
        {
            "id": "DEF-MGR-003",
            "title": "Manager Reports CSV Export Corrupts Accented and Special Characters in Team Names",
            "severity": "Medium",
            "priority": "Low",
            "component": "Reports Export (ManagerReportsPage)",
            "route": "/reports",
            "preconditions": "Team members have names containing special or accented characters (e.g., François, Müller, or Hindi/Devanagari scripts).",
            "steps": [
                "Navigate to Reports (/reports).",
                "Select date range and click 'Export CSV'.",
                "Open the downloaded .csv file in Microsoft Excel."
            ],
            "expected": "All employee names and project descriptions render clearly in UTF-8 encoding.",
            "actual": "Special characters are corrupted (e.g., 'FranÃ§ois' or question marks) due to missing Byte Order Mark (BOM).",
            "root_cause": "Export utility creates a Blob with new Blob([csvContent], {type: 'text/csv'}) without prepending the UTF-8 BOM ('\\uFEFF').",
            "impact": "Exported billing and utilization reports look unprofessional when shared with stakeholders.",
            "fix": "Prepend BOM: new Blob(['\\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' })."
        },
        {
            "id": "DEF-MGR-004",
            "title": "Team Directory 'Reset Filters' Button Fails to Clear Selected Skill Tag Pills",
            "severity": "Low",
            "priority": "Low",
            "component": "Team Directory Filters (ManagerTeamPage)",
            "route": "/team",
            "preconditions": "Manager on /team page with multiple skill filter pills selected (e.g., 'React', 'QA', 'Java').",
            "steps": [
                "Navigate to /team.",
                "Click on skill filter pills: 'React' and 'Java'.",
                "Click the 'Reset Filters' button at the top of the directory."
            ],
            "expected": "The search query, role dropdown, and all active skill pills should return to the default unselected state.",
            "actual": "The search box and role dropdown reset, but skill pills remain highlighted in blue, causing filter state mismatch.",
            "root_cause": "resetFilters() resets searchQuery and selectedRole states but forgets to call setSelectedSkills([]).",
            "impact": "Minor UI inconsistency requiring users to manually click each pill to deselect.",
            "fix": "Add setSelectedSkills([]) inside the resetFilters callback."
        }
    ],
    "recommendations": [
        {"title": "Enforce Rejection Reason Modals", "desc": "Make feedback comments compulsory whenever a timesheet is rejected."},
        {"title": "Enhance Real-time Bandwidth Sync", "desc": "Configure 30-second polling or window focus refetching on resource capacity views."},
        {"title": "Fix UTF-8 BOM in Report Exports", "desc": "Ensure all exported CSV files include standard UTF-8 headers for Excel compatibility."}
    ]
}

# ==========================================
# MODULE 3: PROJECT MANAGER (PM) MODULE
# ==========================================
pm_data = {
    "module_name": "Project Manager (PM) Module & Billing Dashboard",
    "role_name": "Project Manager",
    "role_email": "rohan@setoo.co",
    "test_suite_name": "PM Suite (PM-001 to PM-007, Billing Access, Budget Guards)",
    "total_tests": 9,
    "metric_critical": 1,
    "metric_high": 1,
    "metric_medium": 1,
    "metric_low": 1,
    "executive_summary": (
        "The Project Manager module governs project creation, milestone tracking, client assignments, "
        "and billable hour financial tracking. Automated test execution verified that PM users have full access to "
        "the Billing & Accounts Dashboard (/billing) while being properly restricted from unauthorized budget configuration "
        "mutations (TC 1.3). High-priority defects were logged around Extra Burned hour double-counting on timesheet revisions, "
        "case-sensitive project search, and missing visual alerts when consumption exceeds 100%."
    ),
    "automated_tests": [
        {"method": "testPMAuthenticationAndDashboardUI", "description": "PM-001: Verifies PM login, dashboard landing, and profile avatar."},
        {"method": "testPMNavigationPrivileges", "description": "PM-002: Verifies PM links (Projects, Billing, Approvals, Bandwidth, Reports)."},
        {"method": "testPMProjectsModuleNavigation", "description": "PM-003: Verifies Projects list, project status badges, and team assignments."},
        {"method": "testPMBillingAndBudgetModuleNavigation", "description": "PM-004: Verifies Billing & Budget dashboard navigation and layout."},
        {"method": "testPMApprovalsModuleNavigation", "description": "PM-005: Verifies PM access to the timesheet approvals queue."},
        {"method": "testPMReportsAndTeamNavigation", "description": "PM-006: Verifies PM reports generation and team directory views."},
        {"method": "testPMGlobalSearchTrigger", "description": "PM-007: Verifies CMD+K global search for projects, milestones, and users."},
        {"method": "test1_2_AuthorizedView_PMAccessesBillingDashboard", "description": "RBAC-PM-001: Validates PM access to financial stat cards and client summary."},
        {"method": "test1_3_BudgetConfigurationGuard_RestrictedForPM", "description": "RBAC-PM-002: Confirms PM is blocked from POST /billing/budgets mutation."}
    ],
    "defects": [
        {
            "id": "DEF-PM-001",
            "title": "Extra Burned Billable Hours Calculation Double-Counts Revised Timesheet Entries",
            "severity": "Critical",
            "priority": "High",
            "component": "Billing & Budget Calculation Engine (BillingPage)",
            "route": "/billing",
            "preconditions": "Project with 100 planned hours. Employee logged 20 hrs, had it approved, then edited to 25 hrs and re-approved.",
            "steps": [
                "Login as Project Manager (rohan@setoo.co).",
                "Navigate to Billing & Accounts Dashboard (/billing).",
                "Locate the project card and inspect 'Total Consumed' and 'Extra Burned' metrics."
            ],
            "expected": "Total Consumed should equal 25.0 hrs. Remaining Balance should equal 75.0 hrs. Extra Burned should equal 0.0 hrs.",
            "actual": "Total Consumed displays 45.0 hrs (20 hrs initial + 25 hrs revised), prematurely triggering Extra Burned metrics.",
            "root_cause": "The SQL/Prisma aggregation query sums all timesheet records with status = 'APPROVED' without filtering on is_current_version = true or deducting superseded revisions.",
            "impact": "Skewed financial reporting, incorrect client billing estimates, and false over-budget panic for project managers.",
            "fix": "Update query: SELECT SUM(hours) FROM timesheets WHERE project_id = ? AND status = 'APPROVED' AND is_latest_revision = TRUE;"
        },
        {
            "id": "DEF-PM-002",
            "title": "Projects Management Search Filter is Case-Sensitive and Misses Partial Matches",
            "severity": "High",
            "priority": "Medium",
            "component": "Projects Filter (PMProjectsPage)",
            "route": "/projects",
            "preconditions": "Projects exist with client names like 'Setoo Technologies' and 'Acme Corp'.",
            "steps": [
                "Navigate to Projects page (/projects).",
                "In the search input box, type 'setoo' in all lowercase letters.",
                "Inspect the filtered project card grid."
            ],
            "expected": "All projects associated with 'Setoo Technologies' should appear in the results list.",
            "actual": "Grid shows 'No projects found matching setoo'. Results only populate if exact uppercase 'Setoo' is entered.",
            "root_cause": "The filter predicate uses project.clientName.includes(searchTerm) without normalizing strings to lower case.",
            "impact": "Degrades PM workflow efficiency when quickly searching through large client project rosters.",
            "fix": "Use normalized comparison: project.clientName.toLowerCase().includes(searchTerm.toLowerCase().trim())."
        },
        {
            "id": "DEF-PM-003",
            "title": "Budget Progress Bar Colors Do Not Transition to Alert Red State When Consumption > 100%",
            "severity": "Medium",
            "priority": "Medium",
            "component": "Billing Stat Cards & Project Progress Bar",
            "route": "/billing",
            "preconditions": "A project where Total Consumed (120 hrs) exceeds Total Planned Billable (100 hrs).",
            "steps": [
                "Navigate to /billing.",
                "Scroll to Project Billable Hours section.",
                "Inspect the progress bar for the over-burned project."
            ],
            "expected": "Progress bar fill should change from standard purple/blue to vibrant warning red (#EF4444) with an 'Over Budget by 20 hrs' badge.",
            "actual": "Progress bar remains purple/blue, stretches past 100% width causing slight container overflow, and provides no clear visual alert.",
            "root_cause": "Component has static class className='bg-primary' rather than conditional styling based on percentage > 100.",
            "impact": "PMs can easily overlook budget overruns without immediate visual hierarchy indicators.",
            "fix": "Apply dynamic styling: className={cn('h-2 rounded-full', isOverBudget ? 'bg-destructive' : 'bg-primary')}."
        },
        {
            "id": "DEF-PM-004",
            "title": "Project Milestone Modal Allows End Date to Precede Start Date in Form Submission",
            "severity": "Low",
            "priority": "Low",
            "component": "Project Details / Milestone Editor",
            "route": "/projects/[id]",
            "preconditions": "PM on project details view attempting to create or edit a milestone.",
            "steps": [
                "Open 'Add Milestone' modal dialog.",
                "Select Start Date: September 15, 2026.",
                "Select End Date: September 01, 2026.",
                "Click 'Save Milestone'."
            ],
            "expected": "Form validation should block submission and show an inline error below End Date: 'End date cannot be earlier than start date'.",
            "actual": "The form submits, and only after an API roundtrip does a generic 'Bad Request 400' error toast appear.",
            "root_cause": "Form schema validation (Zod schema) lacks a .refine() rule checking data.endDate >= data.startDate.",
            "impact": "Poor user experience requiring full network roundtrip for basic date logic.",
            "fix": "Add Zod refinement: .refine((data) => data.endDate >= data.startDate, { message: 'End date must be after start date', path: ['endDate'] })."
        }
    ],
    "recommendations": [
        {"title": "Fix Revision Deduplication Query", "desc": "Ensure timesheet aggregation logic strictly filters by latest approved version."},
        {"title": "Implement Over-budget Visual Alerts", "desc": "Render red badges and alert styling when consumed hours exceed planned budget."},
        {"title": "Normalize Client Search Queries", "desc": "Make project and client search case-insensitive across all PM pages."}
    ]
}

# ==========================================
# MODULE 4: ADMINISTRATOR MODULE
# ==========================================
admin_data = {
    "module_name": "Administrator Module & System Configuration",
    "role_name": "System Administrator",
    "role_email": "darshan@setoo.co",
    "test_suite_name": "Admin Suite (ADM-001 to ADM-007, System Settings, RBAC Admin Checks)",
    "total_tests": 11,
    "metric_critical": 0,
    "metric_high": 2,
    "metric_medium": 1,
    "metric_low": 1,
    "executive_summary": (
        "The Administrator module possesses full organizational governance, system settings configuration, "
        "analytics tabs (Overview, Approvals Analytics, Pending Submissions), and master access to projects and billing. "
        "Automated testing verified that the Administrator badge, master sidebar links, and analytics tab switchers function seamlessly. "
        "Notable defects were logged concerning lack of optimistic locking in organization settings, missing browser history sync "
        "in analytics tabs, and delayed JWT revocation upon user role deprovisioning."
    ),
    "automated_tests": [
        {"method": "testAdminAuthenticationAndDashboardUI", "description": "ADM-001: Verifies admin login, profile avatar, and Administrator badge."},
        {"method": "testAdminNavigationPrivileges", "description": "ADM-002: Verifies admin privileges (Administration, Billing, Projects, Approvals)."},
        {"method": "testAdminDashboardAnalyticsTabs", "description": "ADM-003: Verifies Overview, Approvals Analytics, and Pending Submissions tabs."},
        {"method": "testAdminSettingsModuleNavigation", "description": "ADM-004: Verifies Administration settings page and system config cards."},
        {"method": "testAdminProjectsAndBillingNavigation", "description": "ADM-005: Verifies admin navigation to master projects and billing."},
        {"method": "testAdminApprovalsQueueNavigation", "description": "ADM-006: Verifies admin master approvals queue and filters."},
        {"method": "testAdminGlobalSearchTrigger", "description": "ADM-007: Verifies global search for admin configurations and users."},
        {"method": "test1_2_AuthorizedView_AdminAccessesBillingDashboard", "description": "RBAC-ADM-001: Confirms admin access to financial stat cards and client tables."},
        {"method": "testRoleBasedLogin_Admin", "description": "AUTH-ADM: Validates credential authentication for administrator role."},
        {"method": "testEmployeeCannotAccessAdminRoutes", "description": "RBAC-SEC-001: Asserts standard users cannot access /settings or /admin."},
        {"method": "testEmployeeNavigationPermissions", "description": "RBAC-SEC-002: Asserts Administration links are hidden from non-admin accounts."}
    ],
    "defects": [
        {
            "id": "DEF-ADM-001",
            "title": "Organization Settings Form Lacks Optimistic Locking / Concurrency Conflict Guard",
            "severity": "High",
            "priority": "High",
            "component": "Administration Settings (AdminSettingsPage)",
            "route": "/settings, /admin",
            "preconditions": "Two Admins simultaneously viewing the Organization Settings page.",
            "steps": [
                "Admin A opens /settings and modifies weekly work hours from 40 to 45 hrs, then clicks Save.",
                "Admin B, having opened /settings earlier, modifies company holiday list and clicks Save without refreshing.",
                "Inspect the resulting configuration in the database."
            ],
            "expected": "Admin B should receive a concurrency conflict alert: 'Settings were updated by another session. Please reload to see changes.'",
            "actual": "Admin B's payload overwrites the configuration, reverting Admin A's weekly hours change back to 40 without warning.",
            "root_cause": "The updateSettings endpoint does not verify an entity version number or updatedAt timestamp before executing the update query.",
            "impact": "Unintentional configuration overwrites and silent loss of organizational policy updates.",
            "fix": "Include updatedAt in payload and enforce optimistic locking: UPDATE settings SET ... WHERE id = ? AND updated_at = payload.updated_at."
        },
        {
            "id": "DEF-ADM-002",
            "title": "User Role Downgrade / Deprovisioning Does Not Immediately Invalidate Active JWT Tokens",
            "severity": "High",
            "priority": "High",
            "component": "User Management & Token Security",
            "route": "/admin/users, /settings",
            "preconditions": "A Manager user is actively logged in on Browser 1.",
            "steps": [
                "Admin navigates to User Management on Browser 2.",
                "Admin changes the user's role from Manager to Employee or deactivates the account.",
                "In Browser 1, the user immediately performs a Manager-only action (e.g., approving a timesheet)."
            ],
            "expected": "The API request should be rejected with 401 Unauthorized / Role Revoked, forcing logout.",
            "actual": "The action succeeds because the stateless JWT token in Browser 1 remains valid until its 24-hour expiration window lapses.",
            "root_cause": "JWT verification only checks cryptographic signature without verifying token_version or user.isActive status against Redis/DB.",
            "impact": "Security risk where deprovisioned employees or demoted staff retain elevated privileges until token expiration.",
            "fix": "Implement a token_version field in the user model; increment token_version on role change and validate in authentication middleware."
        },
        {
            "id": "DEF-ADM-003",
            "title": "Dashboard Analytics Tab Selection is Not Synced with URL Query Params or Browser History",
            "severity": "Medium",
            "priority": "Medium",
            "component": "Admin Dashboard Tab Switcher (AdminDashboardPage)",
            "route": "/dashboard",
            "preconditions": "Admin logged in on /dashboard.",
            "steps": [
                "Click on the 'Approvals Analytics' or 'Pending Submissions' tab.",
                "Press the browser Refresh button (F5) or copy and share the URL."
            ],
            "expected": "The page reloads directly to the 'Pending Submissions' tab (URL: /dashboard?tab=pending-submissions).",
            "actual": "The page resets back to the default 'Overview' tab, forcing the administrator to click through tabs again.",
            "root_cause": "Tabs component uses local React state (const [tab, setTab] = useState('overview')) rather than URL query parameter binding.",
            "impact": "Inconvenience for administrators who bookmark or share direct links to approval analytics views.",
            "fix": "Bind tab state to URL query parameter: const [searchParams, setSearchParams] = useSearchParams(); activeTab = searchParams.get('tab') || 'overview'."
        },
        {
            "id": "DEF-ADM-004",
            "title": "Approvals Analytics Chart Tooltip Flashes Rapidly Near Canvas Margins",
            "severity": "Low",
            "priority": "Low",
            "component": "Analytics Charts Widget",
            "route": "/dashboard (Approvals Analytics tab)",
            "preconditions": "Admin viewing Approvals Analytics bar chart.",
            "steps": [
                "Navigate to 'Approvals Analytics' tab.",
                "Hover mouse cursor near the top or right boundary of a chart bar."
            ],
            "expected": "Tooltip renders smoothly and remains stable while hovering.",
            "actual": "Tooltip constantly flickers/mounts/unmounts rapidly when the mouse pointer intersects the tooltip boundary.",
            "root_cause": "The custom tooltip container intercepts pointer events, causing mouseLeave on the underlying chart element.",
            "impact": "Minor visual annoyance when inspecting chart statistics.",
            "fix": "Add pointer-events-none to the chart tooltip container CSS class."
        }
    ],
    "recommendations": [
        {"title": "Implement Token Version Invalidation", "desc": "Ensure session tokens are immediately revoked upon user role change or deactivation."},
        {"title": "Add Concurrency Checks in Settings", "desc": "Use optimistic locking to prevent simultaneous admin edits from overwriting each other."},
        {"title": "Sync Dashboard Tab State in URL", "desc": "Store active analytics tab in URL search params for bookmarking and history support."}
    ]
}

if __name__ == "__main__":
    print("Generating all 4 module-wise PDF defect reports...")
    
    create_module_pdf("Timeflow_Defect_Report_Employee_Module.pdf", employee_data)
    create_module_pdf("Timeflow_Defect_Report_Manager_Module.pdf", manager_data)
    create_module_pdf("Timeflow_Defect_Report_PM_Module.pdf", pm_data)
    create_module_pdf("Timeflow_Defect_Report_Admin_Module.pdf", admin_data)
    
    print("\nAll 4 PDF Reports Generated Successfully!")
