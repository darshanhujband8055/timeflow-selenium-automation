package com.timeflow.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class TimeflowDashboardPage extends BasePage {

    // Locators for Dashboard elements
    private final By mainContent = By.cssSelector("main, [role='main'], div.min-h-screen");
    private final By navigationLinks = By.cssSelector("nav a, aside a, [role='navigation'] a");
    private final By userProfileTrigger = By.cssSelector("[aria-haspopup='menu'], button[aria-label*='profile'], button[aria-label*='user'], div.avatar");
    private final By logoutButton = By.xpath("//button[contains(text(),'Log out') or contains(text(),'Sign out') or contains(text(),'Logout')] | //a[contains(text(),'Log out') or contains(text(),'Logout')]");

    public boolean isDashboardLoaded() {
        // Wait until URL transitions away from /login
        boolean leftLogin = waitForUrlContains("timeflow.setoo.in") && !getCurrentUrl().contains("/login");
        return leftLogin || isDisplayed(mainContent);
    }

    public List<WebElement> getNavigationLinks() {
        return driver.findElements(navigationLinks);
    }

    public void clickUserProfile() {
        if (isDisplayed(userProfileTrigger)) {
            click(userProfileTrigger);
        }
    }

    public boolean isLogoutAvailable() {
        return isDisplayed(logoutButton);
    }

    public TimeflowLoginPage logout() {
        clickUserProfile();
        if (isDisplayed(logoutButton)) {
            click(logoutButton);
        }
        return new TimeflowLoginPage();
    }
}
