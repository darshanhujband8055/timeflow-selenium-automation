package com.timeflow.pages;

import org.openqa.selenium.By;

public class EmployeeTasksPage extends BasePage {

    private final By mainTasksView = By.cssSelector("main, div.container, [role='main']");
    private final By taskBoardOrTable = By.cssSelector("table, [role='grid'], div.grid, div.space-y-4");

    public boolean isTasksPageLoaded() {
        return waitForUrlContains("/tasks");
    }

    public boolean isTaskContentDisplayed() {
        return isDisplayed(mainTasksView);
    }
}
