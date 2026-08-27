package com.timeflow.pages;

import org.openqa.selenium.By;

public class ManagerReportsPage extends BasePage {

    private final By mainContent = By.cssSelector("main, [role='main'], div.container");

    public boolean isReportsPageLoaded() {
        return waitForUrlContains("/reports") || isDisplayed(mainContent);
    }
}
