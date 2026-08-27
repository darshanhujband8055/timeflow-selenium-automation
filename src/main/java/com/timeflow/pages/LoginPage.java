package com.timeflow.pages;

import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    // Locators
    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By submitButton = By.id("submit");
    private final By errorMessage = By.id("error");

    public LoginPage enterUsername(String username) {
        type(usernameInput, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public void clickSubmit() {
        click(submitButton);
    }

    public DashboardPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickSubmit();
        return new DashboardPage();
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    public boolean isUsernameFieldDisplayed() {
        return isDisplayed(usernameInput);
    }
}
