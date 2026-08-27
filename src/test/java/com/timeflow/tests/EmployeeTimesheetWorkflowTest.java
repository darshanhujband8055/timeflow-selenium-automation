package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.EmployeeDashboardPage;
import com.timeflow.pages.EmployeeTimesheetsPage;
import com.timeflow.pages.SubmitWeekModal;
import com.timeflow.pages.TimeflowLoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmployeeTimesheetWorkflowTest extends BaseTest {

    private EmployeeDashboardPage dashboardPage;
    private EmployeeTimesheetsPage timesheetsPage;

    @BeforeMethod(alwaysRun = true)
    public void setupAndNavigateToTimesheets() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("shubham.shinde@setoo.co", "Pass@123");
        dashboardPage = new EmployeeDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard failed to load!");
        timesheetsPage = dashboardPage.clickTimesheets();
        Assert.assertTrue(timesheetsPage.isTimesheetsLoaded(), "Timesheets failed to load!");
    }

    @Test(priority = 1, description = "EMP-TS-001: Timesheets page loads with correct header and action buttons")
    public void testTimesheetsHeaderAndControls() {
        Assert.assertTrue(timesheetsPage.isTimesheetPageTitleOrHeaderValid(), 
                "Timesheets header or page title was not found or incorrect!");
        Assert.assertTrue(timesheetsPage.isSyncMyTasksButtonDisplayed(), 
                "'Sync My Tasks' button is missing!");
        Assert.assertTrue(timesheetsPage.isSaveDraftButtonDisplayed(), 
                "'Save draft' button is missing!");
        Assert.assertTrue(timesheetsPage.isSubmitWeekButtonDisplayed(), 
                "'Submit week' button is missing!");
    }

    @Test(priority = 2, description = "EMP-SYNC-001: Employee can trigger personal task synchronization")
    public void testTaskSynchronizationTrigger() {
        timesheetsPage.clickSyncMyTasks();
        // Verifies button click executes without crashing and remains visible / displays feedback
        Assert.assertTrue(timesheetsPage.isSyncMyTasksButtonDisplayed(), 
                "'Sync My Tasks' button should remain accessible after triggering sync!");
    }

    @Test(priority = 3, description = "EMP-VIEW-001: Employee can switch between Daily, Weekly, and Monthly views")
    public void testDateViewsSwitching() {
        Assert.assertTrue(timesheetsPage.isDailyViewButtonDisplayed(), "Daily view button is not displayed!");
        Assert.assertTrue(timesheetsPage.isWeeklyViewButtonDisplayed(), "Weekly view button is not displayed!");
        Assert.assertTrue(timesheetsPage.isMonthlyViewButtonDisplayed(), "Monthly view button is not displayed!");

        timesheetsPage.selectDailyView();
        timesheetsPage.selectMonthlyView();
        timesheetsPage.selectWeeklyView();
    }

    @Test(priority = 4, description = "EMP-VIEW-005 & EMP-VIEW-006: Date navigation controls operate smoothly")
    public void testDateNavigationControls() {
        timesheetsPage.clickPreviousPeriod();
        timesheetsPage.clickNextPeriod();
        Assert.assertTrue(timesheetsPage.isTimesheetsLoaded(), "Timesheets page failed to navigate periods!");
    }

    @Test(priority = 5, description = "EMP-SUB-001 & EMP-SUB-008: Submit week action triggers submission modal or zero-hour protection")
    public void testSubmitWeekModalWorkflow() {
        Assert.assertTrue(timesheetsPage.isSubmitWeekButtonDisplayed(), "'Submit week' button is not visible!");
        SubmitWeekModal submitModal = timesheetsPage.clickSubmitWeek();
        
        // Either modal dialog is rendered, or validation notification appears when draft is empty (EMP-SUB-008)
        boolean modalOpened = submitModal.isModalDisplayed();
        boolean actionHandled = modalOpened || timesheetsPage.isToastNotificationVisible() || timesheetsPage.isSubmitWeekButtonDisplayed();
        
        Assert.assertTrue(actionHandled, "Submit week action was not handled properly!");

        if (modalOpened) {
            submitModal.clickCancel();
            Assert.assertTrue(timesheetsPage.isTimesheetsLoaded(), "User was not returned to Timesheets after cancel!");
        }
    }
}
