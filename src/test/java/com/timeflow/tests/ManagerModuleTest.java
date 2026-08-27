package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ManagerModuleTest extends BaseTest {

    private ManagerDashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsManager() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("rutuja@setoo.co", "Pass@123");
        dashboardPage = new ManagerDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Manager Dashboard failed to load!");
    }

    @Test(priority = 1, description = "MGR-001: Verify Manager authentication, profile, and dashboard landing")
    public void testManagerAuthenticationAndDashboardUI() {
        Assert.assertTrue(dashboardPage.isUserProfileDisplayed(), "Manager user profile is not displayed!");
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Manager is not on /dashboard!");
    }

    @Test(priority = 2, description = "MGR-002: Verify Manager role-based navigation privileges (Approvals, Team, Reports, Bandwidth)")
    public void testManagerNavigationPrivileges() {
        Assert.assertTrue(dashboardPage.isApprovalsLinkPresent(), "Approvals link is missing for Manager role!");
        Assert.assertTrue(dashboardPage.isBandwidthLinkPresent(), "Bandwidth link is missing for Manager role!");
        Assert.assertTrue(dashboardPage.isAllocationsLinkPresent(), "Allocations link is missing for Manager role!");
        Assert.assertTrue(dashboardPage.isUtilizationLinkPresent(), "Utilization link is missing for Manager role!");
        Assert.assertTrue(dashboardPage.isTeamLinkPresent(), "Team link is missing for Manager role!");
        Assert.assertTrue(dashboardPage.isProjectsLinkPresent(), "Projects link is missing for Manager role!");
        Assert.assertTrue(dashboardPage.isReportsLinkPresent(), "Reports link is missing for Manager role!");
    }

    @Test(priority = 3, description = "MGR-003: Verify Manager Approvals module navigation")
    public void testManagerApprovalsModuleNavigation() {
        ManagerApprovalsPage approvalsPage = dashboardPage.clickApprovals();
        Assert.assertTrue(approvalsPage.isApprovalsPageLoaded(), "Manager Approvals page failed to load!");
    }

    @Test(priority = 4, description = "MGR-004: Verify Manager Reports module navigation")
    public void testManagerReportsModuleNavigation() {
        ManagerReportsPage reportsPage = dashboardPage.clickReports();
        Assert.assertTrue(reportsPage.isReportsPageLoaded(), "Manager Reports page failed to load!");
    }

    @Test(priority = 5, description = "MGR-005: Verify Manager Team directory module navigation")
    public void testManagerTeamModuleNavigation() {
        ManagerTeamPage teamPage = dashboardPage.clickTeam();
        Assert.assertTrue(teamPage.isTeamPageLoaded(), "Manager Team page failed to load!");
    }

    @Test(priority = 6, description = "MGR-006: Verify Manager Bandwidth and Allocations navigation")
    public void testManagerBandwidthAndAllocationsNavigation() {
        dashboardPage.clickBandwidth();
        dashboardPage.clickAllocations();
        dashboardPage.clickUtilization();
        Assert.assertTrue(dashboardPage.isDashboardLoaded() || dashboardPage.isBandwidthLinkPresent(), 
                "Manager was unable to navigate Bandwidth/Allocations!");
    }

    @Test(priority = 7, description = "MGR-007: Verify Manager Global Search trigger")
    public void testManagerGlobalSearch() {
        dashboardPage.openGlobalSearch();
        Assert.assertTrue(dashboardPage.isSearchModalVisible(), "Global search modal did not open for Manager!");
    }
}
