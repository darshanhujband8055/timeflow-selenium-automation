package com.timeflow.pages;

import org.openqa.selenium.By;

public class ManagerTeamPage extends BasePage {

    private final By mainContent = By.cssSelector("main, [role='main'], div.container");

    public boolean isTeamPageLoaded() {
        return waitForUrlContains("/team") || isDisplayed(mainContent);
    }
}
