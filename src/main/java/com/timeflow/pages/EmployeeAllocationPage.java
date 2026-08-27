package com.timeflow.pages;

import org.openqa.selenium.By;

public class EmployeeAllocationPage extends BasePage {

    private final By mainContent = By.cssSelector("main, div.container, [role='main']");

    public boolean isAllocationPageLoaded() {
        return waitForUrlContains("/my-allocation");
    }

    public boolean isAllocationContentDisplayed() {
        return isDisplayed(mainContent);
    }
}
