package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.pages.TimeflowDashboardPage;
import com.timeflow.pages.TimeflowLoginPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TimeflowLoginTest extends BaseTest {

    @Test(priority = 1, description = "Verify TimeFlow Login Page UI branding and elements")
    public void testLoginPageUIBranding() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();

        Assert.assertTrue(loginPage.getPageTitle().contains("TimeFlow"), 
                "Page title did not contain 'TimeFlow'!");
        Assert.assertEquals(loginPage.getPageHeading(), "Sign in to TimeFlow", 
                "Page heading did not match expected text!");
        Assert.assertTrue(loginPage.isEmailInputDisplayed(), 
                "Work Email input field is not displayed!");
        Assert.assertTrue(loginPage.isPasswordInputDisplayed(), 
                "Password input field is not displayed!");
        Assert.assertTrue(loginPage.isSubmitButtonDisplayed(), 
                "Continue/Submit button is not displayed!");
        Assert.assertTrue(loginPage.isForgotPasswordButtonDisplayed(), 
                "Forgot password action is not displayed!");
        Assert.assertTrue(loginPage.isFooterNoticeDisplayed(), 
                "Footer restriction notice is not displayed!");
    }

    @Test(priority = 2, description = "Verify password show/hide eye toggle functionality")
    public void testPasswordVisibilityToggle() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();

        Assert.assertEquals(loginPage.getPasswordInputType(), "password", 
                "Initial password input type should be 'password'!");

        loginPage.enterPassword("Pass@123");
        loginPage.togglePasswordVisibility();

        Assert.assertEquals(loginPage.getPasswordInputType(), "text", 
                "Password input type should change to 'text' after clicking show password!");

        loginPage.togglePasswordVisibility();
        Assert.assertEquals(loginPage.getPasswordInputType(), "password", 
                "Password input type should revert to 'password' after second click!");
    }

    @Test(priority = 3, description = "Verify login fails with invalid password")
    public void testInvalidPasswordLogin() {
        TimeflowLoginPage loginPage = new TimeflowLoginPage();

        loginPage.enterEmail("shubham.shinde@setoo.co")
                 .enterPassword("WrongPassword@999")
                 .clickSubmit();

        // Ensure user remains on login page or sees error
        Assert.assertTrue(loginPage.isEmailInputDisplayed(), 
                "User should remain on the login page after an invalid password attempt!");
    }

    @DataProvider(name = "userRolesData")
    public Object[][] getUserRolesData() {
        return new Object[][]{
                {"Admin", "darshan@setoo.co", "Pass@123"},
                {"Project Manager", "rohan@setoo.co", "Pass@123"},
                {"Manager", "rutuja@setoo.co", "Pass@123"},
                {"Employee", "shubham.shinde@setoo.co", "Pass@123"}
        };
    }

    @Test(priority = 4, dataProvider = "userRolesData", description = "Verify successful authentication across all 4 user roles")
    public void testRoleBasedLogin(String roleName, String email, String password) {
        System.out.println("Testing authentication for role: " + roleName + " (" + email + ")");
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        
        TimeflowDashboardPage dashboardPage = loginPage.login(email, password);

        Assert.assertTrue(dashboardPage.isDashboardLoaded(), 
                "Dashboard was not loaded successfully for role: " + roleName + " (" + email + ")!");
    }
}
