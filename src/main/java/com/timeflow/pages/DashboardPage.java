package com.timeflow.pages;

import org.openqa.selenium.By;

public class DashboardPage extends BasePage {

    // Locators
    private final By headerTitle = By.tagName("h1");
    private final By logoutButton = By.xpath("//a[contains(@class,'wp-block-button__link') and text()='Log out']");
    private final By successMessage = By.tagName("strong");

    public String getHeaderText() {
        return getText(headerTitle);
    }

    public String getSuccessMessage() {
        return getText(successMessage);
    }

    public boolean isLogoutButtonDisplayed() {
        return isDisplayed(logoutButton);
    }

    public LoginPage clickLogout() {
        click(logoutButton);
        return new LoginPage();
    }
}
