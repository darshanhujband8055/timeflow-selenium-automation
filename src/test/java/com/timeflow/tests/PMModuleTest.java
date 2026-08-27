package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PMModuleTest extends BaseTest {

    private PMDashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsPM() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("rohan@setoo.co", "Pass@123");
        dashboardPage = new PMDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "PM Dashboard failed to load!");
    }

    @Test(priority = 1, description = "PM-001: Verify Project Manager (PM) authentication, profile, and dashboard landing")
    public void testPMAuthenticationAndDashboardUI() {
        Assert.assertTrue(dashboardPage.isUserProfileDisplayed(), "PM user profile is not displayed!");
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "PM is not on /dashboard!");
    }

    @Test(priority = 2, description = "PM-002: Verify PM role-based navigation privileges (Projects, Billing, Approvals, Reports)")
    public void testPMNavigationPrivileges() {
        Assert.assertTrue(dashboardPage.isProjectsLinkPresent(), "Projects link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isBillingLinkPresent(), "Billing & Budget link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isApprovalsLinkPresent(), "Approvals link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isBandwidthLinkPresent(), "Bandwidth link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isAllocationsLinkPresent(), "Allocations link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isUtilizationLinkPresent(), "Utilization link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isReportsLinkPresent(), "Reports link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isTeamLinkPresent(), "Team link is missing for PM role!");
        Assert.assertTrue(dashboardPage.isAdministrationLinkPresent(), "Administration link is missing for PM role!");
    }

    @Test(priority = 3, description = "PM-003: Verify PM Projects management module navigation")
    public void testPMProjectsModuleNavigation() {
        PMProjectsPage projectsPage = dashboardPage.clickProjects();
        Assert.assertTrue(projectsPage.isProjectsPageLoaded(), "PM Projects page failed to load!");
        Assert.assertTrue(projectsPage.isProjectsContentDisplayed(), "PM Projects list/grid is not displayed!");
    }

    @Test(priority = 4, description = "PM-004: Verify PM Billing & Budget module navigation")
    public void testPMBillingAndBudgetModuleNavigation() {
        PMBillingPage billingPage = dashboardPage.clickBilling();
        Assert.assertTrue(billingPage.isBillingPageLoaded(), "PM Billing & Budget page failed to load!");
    }

    @Test(priority = 5, description = "PM-005: Verify PM Approvals queue module navigation")
    public void testPMApprovalsModuleNavigation() {
        ManagerApprovalsPage approvalsPage = dashboardPage.clickApprovals();
        Assert.assertTrue(approvalsPage.isApprovalsPageLoaded(), "PM Approvals page failed to load!");
    }

    @Test(priority = 6, description = "PM-006: Verify PM Reports and Team directory navigation")
    public void testPMReportsAndTeamNavigation() {
        ManagerReportsPage reportsPage = dashboardPage.clickReports();
        Assert.assertTrue(reportsPage.isReportsPageLoaded(), "PM Reports page failed to load!");
        ManagerTeamPage teamPage = dashboardPage.clickTeam();
        Assert.assertTrue(teamPage.isTeamPageLoaded(), "PM Team page failed to load!");
    }

    @Test(priority = 7, description = "PM-007: Verify PM Global Search trigger")
    public void testPMGlobalSearchTrigger() {
        dashboardPage.openGlobalSearch();
        Assert.assertTrue(dashboardPage.isSearchModalVisible(), "Global search modal did not open for PM!");
    }
}
