package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.driver.DriverManager;
import com.timeflow.pages.EmployeeDashboardPage;
import com.timeflow.pages.TimeflowLoginPage;
import com.timeflow.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class EmployeeAuthenticationTest extends BaseTest {

    @Test(priority = 1, description = "EMP-AUTH-001: Employee can log in with valid credentials")
    public void testEmployeeValidLogin() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("shubham.shinde@setoo.co", "Pass@123");

        EmployeeDashboardPage dashboardPage = new EmployeeDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Employee was not redirected to /dashboard after login!");
        Assert.assertTrue(dashboardPage.isUserProfileButtonDisplayed(), "User profile is not displayed on Dashboard!");
    }

    @Test(priority = 2, description = "EMP-AUTH-002: Invalid password is rejected")
    public void testInvalidPasswordRejected() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.enterEmail("shubham.shinde@setoo.co")
                 .enterPassword("WrongPassword@123")
                 .clickSubmit();

        Assert.assertTrue(loginPage.isEmailInputDisplayed(), "User should remain on login page after invalid password attempt!");
        Assert.assertTrue(DriverManager.getDriver().getCurrentUrl().contains("/login") || loginPage.isSubmitButtonDisplayed(),
                "URL should remain on login or display login form!");
    }

    @Test(priority = 3, description = "EMP-AUTH-003: Unknown email is rejected")
    public void testUnknownEmailRejected() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.enterEmail("unregistered.employee@setoo.co")
                 .enterPassword("Pass@123")
                 .clickSubmit();

        Assert.assertTrue(loginPage.isEmailInputDisplayed(), "User should remain on login page for unknown email!");
    }

    @Test(priority = 4, description = "EMP-AUTH-004: Empty login fields are validated")
    public void testEmptyLoginFieldsValidation() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.clickSubmit();

        Assert.assertTrue(loginPage.isEmailInputDisplayed(), "Email field should remain present when submitting empty form!");
    }

    @Test(priority = 5, description = "EMP-AUTH-005: Protected route redirects to /login when unauthenticated")
    public void testProtectedRouteRedirectWhenUnauthenticated() {
        // Attempt to navigate directly to /timesheets without authenticating
        DriverManager.getDriver().get(ConfigReader.getBaseUrl() + "/timesheets");

        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        Assert.assertTrue(loginPage.isEmailInputDisplayed() || DriverManager.getDriver().getCurrentUrl().contains("/login"),
                "Unauthenticated access to /timesheets must redirect to /login!");
    }

    @Test(priority = 6, description = "EMP-AUTH-006: Session persists on page refresh")
    public void testSessionPersistsOnRefresh() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("shubham.shinde@setoo.co", "Pass@123");

        EmployeeDashboardPage dashboardPage = new EmployeeDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Dashboard did not load initially!");

        // Refresh the page
        DriverManager.getDriver().navigate().refresh();

        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "User lost authenticated session after page refresh!");
    }
}
