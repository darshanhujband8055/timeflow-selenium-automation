package com.timeflow.pages;

import org.openqa.selenium.By;

public class EmployeeTimesheetsPage extends BasePage {

    // Header & Info
    private final By headerTitle = By.xpath("//h1 | //h2 | //header//*[contains(text(),'Timesheet')]");
    private final By dateRangeLabel = By.cssSelector("h2, span.font-medium, div[class*='date-range'], span[class*='text-sm']");
    
    // Date Navigation
    private final By previousPeriodButton = By.cssSelector("button[aria-label*='previous' i], button:has(svg.lucide-chevron-left), button:has(svg.lucide-arrow-left)");
    private final By nextPeriodButton = By.cssSelector("button[aria-label*='next' i], button:has(svg.lucide-chevron-right), button:has(svg.lucide-arrow-right)");
    private final By todayButton = By.xpath("//button[contains(text(),'Today') or contains(text(),'Current')]");

    // View switcher buttons
    private final By dailyViewButton = By.xpath("//button[text()='Daily']");
    private final By weeklyViewButton = By.xpath("//button[text()='Weekly']");
    private final By monthlyViewButton = By.xpath("//button[text()='Monthly']");

    // Action buttons
    private final By syncMyTasksButton = By.xpath("//button[contains(text(),'Sync My Tasks')]");
    private final By saveDraftButton = By.xpath("//button[contains(text(),'Save draft')]");
    private final By submitWeekButton = By.xpath("//button[contains(text(),'Submit week')]");

    // Feedback toast / alert
    private final By toastNotification = By.cssSelector("[data-sonner-toast], [role='status'], [role='alert'], section[aria-label*='Notification']");

    public boolean isTimesheetsLoaded() {
        return waitForUrlContains("/timesheets");
    }

    public boolean isTimesheetPageTitleOrHeaderValid() {
        return getPageTitle().toLowerCase().contains("timesheet") || isDisplayed(headerTitle);
    }

    public void selectDailyView() {
        click(dailyViewButton);
    }

    public void selectWeeklyView() {
        click(weeklyViewButton);
    }

    public void selectMonthlyView() {
        click(monthlyViewButton);
    }

    public boolean isDailyViewButtonDisplayed() {
        return isDisplayed(dailyViewButton);
    }

    public boolean isWeeklyViewButtonDisplayed() {
        return isDisplayed(weeklyViewButton);
    }

    public boolean isMonthlyViewButtonDisplayed() {
        return isDisplayed(monthlyViewButton);
    }

    public boolean isSyncMyTasksButtonDisplayed() {
        return isDisplayed(syncMyTasksButton);
    }

    public boolean isSaveDraftButtonDisplayed() {
        return isDisplayed(saveDraftButton);
    }

    public boolean isSubmitWeekButtonDisplayed() {
        return isDisplayed(submitWeekButton);
    }

    public void clickSyncMyTasks() {
        click(syncMyTasksButton);
    }

    public void clickSaveDraft() {
        click(saveDraftButton);
    }

    public SubmitWeekModal clickSubmitWeek() {
        click(submitWeekButton);
        return new SubmitWeekModal();
    }

    public void clickPreviousPeriod() {
        if (isDisplayed(previousPeriodButton)) {
            click(previousPeriodButton);
        }
    }

    public void clickNextPeriod() {
        if (isDisplayed(nextPeriodButton)) {
            click(nextPeriodButton);
        }
    }

    public boolean isToastNotificationVisible() {
        return isDisplayed(toastNotification);
    }
}
