package com.timeflow.pages;

import org.openqa.selenium.By;

public class TimeflowLoginPage extends BasePage {

    // Locators
    private final By emailInput = By.id("email");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.cssSelector("button[type='submit']");
    private final By showPasswordButton = By.cssSelector("button[aria-label*='password'], button[aria-label*='Password']");
    private final By forgotPasswordButton = By.xpath("//button[contains(text(),'Forgot password?')]");
    private final By pageHeading = By.tagName("h1");
    private final By pageSubtitle = By.xpath("//p[contains(text(),'Enter your registered work email')]");
    private final By footerNotice = By.xpath("//*[contains(text(),'This system is restricted to authorized Setoo members')]");
    private final By toastAlert = By.cssSelector("[data-sonner-toast], [role='status'], [role='alert'], section[aria-label*='Notification']");

    public TimeflowLoginPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public TimeflowLoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public void clickSubmit() {
        click(submitButton);
    }

    public TimeflowDashboardPage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSubmit();
        waitForUrlContains("/dashboard");
        return new TimeflowDashboardPage();
    }

    public TimeflowLoginPage togglePasswordVisibility() {
        click(showPasswordButton);
        return this;
    }

    public String getPasswordInputType() {
        return getAttribute(passwordInput, "type");
    }

    public String getPageHeading() {
        return getText(pageHeading);
    }

    public String getPageSubtitle() {
        return getText(pageSubtitle);
    }

    public boolean isEmailInputDisplayed() {
        return isDisplayed(emailInput);
    }

    public boolean isPasswordInputDisplayed() {
        return isDisplayed(passwordInput);
    }

    public boolean isSubmitButtonDisplayed() {
        return isDisplayed(submitButton);
    }

    public boolean isFooterNoticeDisplayed() {
        return isDisplayed(footerNotice);
    }

    public boolean isForgotPasswordButtonDisplayed() {
        return isDisplayed(forgotPasswordButton);
    }

    public void clickForgotPassword() {
        click(forgotPasswordButton);
    }

    public String getToastAlertText() {
        try {
            return getText(toastAlert);
        } catch (Exception e) {
            return "";
        }
    }
}
