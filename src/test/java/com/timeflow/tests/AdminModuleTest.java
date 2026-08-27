package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class AdminModuleTest extends BaseTest {

    private AdminDashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsAdmin() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("darshan@setoo.co", "Pass@123");
        dashboardPage = new AdminDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Administrator Dashboard failed to load!");
    }

    @Test(priority = 1, description = "ADM-001: Verify Administrator authentication, profile, badge, and dashboard landing")
    public void testAdminAuthenticationAndDashboardUI() {
        Assert.assertTrue(dashboardPage.isUserProfileDisplayed(), "Admin user profile is not displayed!");
        Assert.assertTrue(dashboardPage.isAdministratorBadgeDisplayed(), "Administrator badge is missing!");
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Admin is not on /dashboard!");
    }

    @Test(priority = 2, description = "ADM-002: Verify Administrator full navigation privileges")
    public void testAdminNavigationPrivileges() {
        Assert.assertTrue(dashboardPage.isAdministrationLinkPresent(), "Administration link is missing for Admin role!");
        Assert.assertTrue(dashboardPage.isBillingLinkPresent(), "Billing link is missing for Admin role!");
        Assert.assertTrue(dashboardPage.isProjectsLinkPresent(), "Projects link is missing for Admin role!");
        Assert.assertTrue(dashboardPage.isApprovalsLinkPresent(), "Approvals link is missing for Admin role!");
        Assert.assertTrue(dashboardPage.isBandwidthLinkPresent(), "Bandwidth link is missing for Admin role!");
    }

    @Test(priority = 3, description = "ADM-003: Verify Admin dashboard analytics tab switcher (Overview, Analytics, Submissions)")
    public void testAdminDashboardAnalyticsTabs() {
        Assert.assertTrue(dashboardPage.isOverviewTabDisplayed(), "Overview tab is missing!");
        dashboardPage.clickApprovalsAnalyticsTab();
        Assert.assertTrue(dashboardPage.isApprovalsAnalyticsTabDisplayed(), "Approvals Analytics tab failed to switch!");
        dashboardPage.clickPendingSubmissionsTab();
        Assert.assertTrue(dashboardPage.isPendingSubmissionsTabDisplayed(), "Pending Submissions tab failed to switch!");
        dashboardPage.clickOverviewTab();
    }

    @Test(priority = 4, description = "ADM-004: Verify Administration configuration module navigation")
    public void testAdminSettingsModuleNavigation() {
        AdminSettingsPage settingsPage = dashboardPage.clickAdministration();
        Assert.assertTrue(settingsPage.isSettingsPageLoaded(), "Administration settings page failed to load!");
    }

    @Test(priority = 5, description = "ADM-005: Verify Admin Projects and Billing management navigation")
    public void testAdminProjectsAndBillingNavigation() {
        PMProjectsPage projectsPage = dashboardPage.clickProjects();
        Assert.assertTrue(projectsPage.isProjectsPageLoaded(), "Projects page failed to load for Admin!");
        PMBillingPage billingPage = dashboardPage.clickBilling();
        Assert.assertTrue(billingPage.isBillingPageLoaded(), "Billing page failed to load for Admin!");
    }

    @Test(priority = 6, description = "ADM-006: Verify Admin Approvals queue module navigation")
    public void testAdminApprovalsQueueNavigation() {
        ManagerApprovalsPage approvalsPage = dashboardPage.clickApprovals();
        Assert.assertTrue(approvalsPage.isApprovalsPageLoaded(), "Approvals page failed to load for Admin!");
    }

    @Test(priority = 7, description = "ADM-007: Verify Administrator Global Search trigger")
    public void testAdminGlobalSearchTrigger() {
        dashboardPage.openGlobalSearch();
        Assert.assertTrue(dashboardPage.isSearchModalVisible(), "Global search modal did not open for Administrator!");
    }
}
