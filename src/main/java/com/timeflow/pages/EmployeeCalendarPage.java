package com.timeflow.pages;

import org.openqa.selenium.By;

public class EmployeeCalendarPage extends BasePage {

    private final By calendarView = By.cssSelector("main, div.calendar, [role='grid'], div.fc");

    public boolean isCalendarPageLoaded() {
        return waitForUrlContains("/calendar");
    }

    public boolean isCalendarDisplayed() {
        return isDisplayed(calendarView);
    }
}
