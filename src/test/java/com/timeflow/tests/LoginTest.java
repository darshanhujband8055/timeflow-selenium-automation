package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.DashboardPage;
import com.timeflow.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Verify successful login with valid credentials")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login("student", "Password123");

        Assert.assertTrue(dashboardPage.getCurrentUrl().contains("practicetestautomation.com/logged-in-successfully/"),
                "URL does not contain the expected landing page URL!");
        Assert.assertEquals(dashboardPage.getHeaderText(), "Logged In Successfully",
                "Header text does not match expected text!");
        Assert.assertTrue(dashboardPage.isLogoutButtonDisplayed(),
                "Logout button is not displayed on Dashboard!");
    }

    @Test(description = "Verify error message when logging in with invalid username")
    public void testInvalidUsername() {
        LoginPage loginPage = new LoginPage();
        loginPage.enterUsername("incorrectUser")
                 .enterPassword("Password123")
                 .clickSubmit();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed!");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Your username is invalid!"),
                "Error message text did not match expected message!");
    }

    @Test(description = "Verify error message when logging in with invalid password")
    public void testInvalidPassword() {
        LoginPage loginPage = new LoginPage();
        loginPage.enterUsername("student")
                 .enterPassword("incorrectPassword")
                 .clickSubmit();

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed!");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Your password is invalid!"),
                "Error message text did not match expected message!");
    }

    @Test(description = "Verify user can log out successfully from dashboard")
    public void testLogout() {
        LoginPage loginPage = new LoginPage();
        DashboardPage dashboardPage = loginPage.login("student", "Password123");

        LoginPage postLogoutPage = dashboardPage.clickLogout();

        Assert.assertTrue(postLogoutPage.isUsernameFieldDisplayed(),
                "Username field is not displayed after logout!");
        Assert.assertTrue(postLogoutPage.getCurrentUrl().contains("practice-test-login"),
                "URL does not match login page URL after logout!");
    }
}
