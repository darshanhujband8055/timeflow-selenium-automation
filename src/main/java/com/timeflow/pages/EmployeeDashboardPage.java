package com.timeflow.pages;

import com.timeflow.utils.ConfigReader;
import org.openqa.selenium.By;

public class EmployeeDashboardPage extends BasePage {

    // Top Navigation Links
    private final By dashboardNavLink = By.xpath("//nav//a[contains(text(),'Dashboard')] | //a[contains(@href,'/dashboard')]");
    private final By timesheetsNavLink = By.xpath("//nav//a[contains(text(),'Timesheet')] | //a[contains(@href,'/timesheets')]");
    private final By myTasksNavLink = By.xpath("//nav//a[contains(text(),'Tasks')] | //a[contains(@href,'/tasks')]");
    private final By myAllocationNavLink = By.xpath("//nav//a[contains(text(),'Allocation')] | //a[contains(@href,'/my-allocation')]");
    private final By calendarNavLink = By.xpath("//nav//a[contains(text(),'Calendar')] | //a[contains(@href,'/calendar')]");
    private final By notificationsNavLink = By.xpath("//nav//a[contains(text(),'Notification')] | //a[contains(@href,'/notifications')]");

    // Header & Quick Actions
    private final By globalSearchTrigger = By.id("global-search-trigger");
    private final By searchModalInput = By.cssSelector("[role='dialog'] input, [cmdk-input], input[placeholder*='Search']");
    private final By searchModal = By.cssSelector("[role='dialog'], [cmdk-dialog], div[data-state='open']");
    private final By userProfileButton = By.cssSelector("button[id^='radix-'], button:has(div.avatar), button:has(span)");
    private final By logoutMenuItem = By.xpath("//div[@role='menu']//button[contains(text(),'Log out') or contains(text(),'Sign out') or contains(text(),'Logout')] | //div[@role='menuitem'][contains(text(),'Log out') or contains(text(),'Sign out')]");
    
    private final By submitWeekLink = By.xpath("//a[contains(text(),'Submit week')]");
    private final By startLoggingLink = By.xpath("//a[contains(text(),'Start logging')]");

    // Tables & Cards
    private final By tasksTable = By.tagName("table");

    // Restricted Admin navigation items to assert absence
    private final By adminSettingsLink = By.xpath("//nav//a[contains(@href,'/admin') or contains(@href,'/organization') or text()='Admin']");

    public boolean isDashboardLoaded() {
        return waitForUrlContains("/dashboard");
    }

    public EmployeeTimesheetsPage clickTimesheets() {
        if (isDisplayed(timesheetsNavLink)) {
            click(timesheetsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/timesheets");
        }
        return new EmployeeTimesheetsPage();
    }

    public EmployeeTasksPage clickMyTasks() {
        if (isDisplayed(myTasksNavLink)) {
            click(myTasksNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/tasks");
        }
        return new EmployeeTasksPage();
    }

    public EmployeeAllocationPage clickMyAllocation() {
        if (isDisplayed(myAllocationNavLink)) {
            click(myAllocationNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/my-allocation");
        }
        return new EmployeeAllocationPage();
    }

    public EmployeeCalendarPage clickCalendar() {
        if (isDisplayed(calendarNavLink)) {
            click(calendarNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/calendar");
        }
        return new EmployeeCalendarPage();
    }

    public void openGlobalSearch() {
        click(globalSearchTrigger);
    }

    public boolean isSearchModalVisible() {
        return isDisplayed(searchModal) || isDisplayed(searchModalInput);
    }

    public boolean isTasksTableDisplayed() {
        return isDisplayed(tasksTable);
    }

    public boolean isSubmitWeekLinkDisplayed() {
        return isDisplayed(submitWeekLink);
    }

    public boolean isStartLoggingLinkDisplayed() {
        return isDisplayed(startLoggingLink);
    }

    public boolean isUserProfileButtonDisplayed() {
        return isDisplayed(userProfileButton);
    }

    public boolean isAdminLinkPresent() {
        return isDisplayed(adminSettingsLink);
    }

    public void clickUserProfile() {
        click(userProfileButton);
    }

    public TimeflowLoginPage logout() {
        clickUserProfile();
        if (isDisplayed(logoutMenuItem)) {
            click(logoutMenuItem);
        }
        return new TimeflowLoginPage();
    }
}
