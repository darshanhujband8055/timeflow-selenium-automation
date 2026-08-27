package com.timeflow.pages;

import org.openqa.selenium.By;

public class AdminSettingsPage extends BasePage {

    private final By mainContent = By.cssSelector("main, [role='main'], div.container");

    public boolean isSettingsPageLoaded() {
        return waitForUrlContains("/settings") || isDisplayed(mainContent);
    }
}
