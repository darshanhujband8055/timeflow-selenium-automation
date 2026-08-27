package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.driver.DriverManager;
import com.timeflow.pages.EmployeeDashboardPage;
import com.timeflow.pages.EmployeeTimesheetsPage;
import com.timeflow.pages.TimeflowLoginPage;
import com.timeflow.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class EmployeePermissionsTest extends BaseTest {

    private EmployeeDashboardPage dashboardPage;

    @BeforeMethod(alwaysRun = true)
    public void loginAsEmployee() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("shubham.shinde@setoo.co", "Pass@123");
        dashboardPage = new EmployeeDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Employee dashboard failed to load!");
    }

    @Test(priority = 1, description = "EMP-PERM-001: Employee can access own timesheets")
    public void testEmployeeCanAccessOwnTimesheets() {
        EmployeeTimesheetsPage timesheetsPage = dashboardPage.clickTimesheets();
        Assert.assertTrue(timesheetsPage.isTimesheetsLoaded(), "Employee was unable to access /timesheets!");
    }

    @Test(priority = 2, description = "EMP-PERM-002: Employee cannot access Administrator configuration")
    public void testEmployeeCannotAccessAdminRoutes() {
        // Attempt direct navigation to admin route
        DriverManager.getDriver().get(ConfigReader.getBaseUrl() + "/admin");
        
        // Assert that user is either redirected away from admin or sees no admin settings
        Assert.assertFalse(dashboardPage.isAdminLinkPresent(), "Admin navigation link is inappropriately present for employee!");
    }

    @Test(priority = 3, description = "EMP-PERM-005: Employee sees only Employee-appropriate navigation")
    public void testEmployeeNavigationPermissions() {
        Assert.assertFalse(dashboardPage.isAdminLinkPresent(), 
                "Inaccessible Admin/Configuration link should not be present in employee navigation!");
    }
}
