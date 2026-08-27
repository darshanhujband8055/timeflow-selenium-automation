package com.timeflow.pages;

import org.openqa.selenium.By;

public class PMProjectsPage extends BasePage {

    private final By mainContent = By.cssSelector("main, [role='main'], div.container");
    private final By projectsTableOrCards = By.cssSelector("table, div.grid, div[class*='project']");

    public boolean isProjectsPageLoaded() {
        return waitForUrlContains("/projects") || isDisplayed(mainContent);
    }

    public boolean isProjectsContentDisplayed() {
        return isDisplayed(projectsTableOrCards) || isDisplayed(mainContent);
    }
}
