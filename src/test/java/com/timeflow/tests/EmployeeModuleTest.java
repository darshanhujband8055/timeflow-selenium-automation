package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmployeeModuleTest extends BaseTest {

    private EmployeeDashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsEmployee() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("shubham.shinde@setoo.co", "Pass@123");
        dashboardPage = new EmployeeDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Employee Dashboard failed to load after login!");
    }

    @Test(priority = 1, description = "Verify Employee Dashboard UI, user profile, quick links, and tasks table")
    public void testEmployeeDashboardUI() {
        Assert.assertTrue(dashboardPage.isUserProfileButtonDisplayed(), 
                "User profile avatar button is not displayed on Dashboard!");
        Assert.assertTrue(dashboardPage.isTasksTableDisplayed(), 
                "Tasks table is not displayed on Dashboard!");
        Assert.assertTrue(dashboardPage.isSubmitWeekLinkDisplayed(), 
                "'Submit week' quick action is not displayed!");
        Assert.assertTrue(dashboardPage.isStartLoggingLinkDisplayed(), 
                "'Start logging' quick action is not displayed!");
    }

    @Test(priority = 2, description = "Verify Timesheets module navigation, view toggles (Daily/Weekly/Monthly) and actions")
    public void testTimesheetsModuleAndViews() {
        EmployeeTimesheetsPage timesheetsPage = dashboardPage.clickTimesheets();
        Assert.assertTrue(timesheetsPage.isTimesheetsLoaded(), "Timesheets page failed to load!");

        // Assert view switchers
        Assert.assertTrue(timesheetsPage.isDailyViewButtonDisplayed(), "Daily view button not displayed!");
        Assert.assertTrue(timesheetsPage.isWeeklyViewButtonDisplayed(), "Weekly view button not displayed!");
        Assert.assertTrue(timesheetsPage.isMonthlyViewButtonDisplayed(), "Monthly view button not displayed!");

        // Switch views
        timesheetsPage.selectDailyView();
        timesheetsPage.selectMonthlyView();
        timesheetsPage.selectWeeklyView();

        // Assert primary action buttons
        Assert.assertTrue(timesheetsPage.isSyncMyTasksButtonDisplayed(), "'Sync My Tasks' button is not displayed!");
        Assert.assertTrue(timesheetsPage.isSaveDraftButtonDisplayed(), "'Save draft' button is not displayed!");
        Assert.assertTrue(timesheetsPage.isSubmitWeekButtonDisplayed(), "'Submit week' button is not displayed!");
    }

    @Test(priority = 3, description = "Verify My Tasks module navigation and layout")
    public void testMyTasksModuleNavigation() {
        EmployeeTasksPage tasksPage = dashboardPage.clickMyTasks();
        Assert.assertTrue(tasksPage.isTasksPageLoaded(), "My Tasks page failed to load!");
        Assert.assertTrue(tasksPage.isTaskContentDisplayed(), "Task view content is not displayed!");
    }

    @Test(priority = 4, description = "Verify My Allocation module navigation")
    public void testMyAllocationModuleNavigation() {
        EmployeeAllocationPage allocationPage = dashboardPage.clickMyAllocation();
        Assert.assertTrue(allocationPage.isAllocationPageLoaded(), "My Allocation page failed to load!");
        Assert.assertTrue(allocationPage.isAllocationContentDisplayed(), "Allocation content is not displayed!");
    }

    @Test(priority = 5, description = "Verify Calendar module navigation")
    public void testCalendarModuleNavigation() {
        EmployeeCalendarPage calendarPage = dashboardPage.clickCalendar();
        Assert.assertTrue(calendarPage.isCalendarPageLoaded(), "Calendar page failed to load!");
        Assert.assertTrue(calendarPage.isCalendarDisplayed(), "Calendar view is not displayed!");
    }

    @Test(priority = 6, description = "Verify Global Search modal trigger")
    public void testGlobalSearchTrigger() {
        dashboardPage.openGlobalSearch();
        Assert.assertTrue(dashboardPage.isSearchModalVisible(), "Global search modal did not open!");
    }
}
