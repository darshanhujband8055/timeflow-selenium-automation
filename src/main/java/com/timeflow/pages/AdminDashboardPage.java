package com.timeflow.pages;

import com.timeflow.utils.ConfigReader;
import org.openqa.selenium.By;

public class AdminDashboardPage extends BasePage {

    // Admin Navigation Links
    private final By dashboardNavLink = By.xpath("//a[contains(@href,'/dashboard')]");
    private final By timesheetsNavLink = By.xpath("//a[contains(@href,'/timesheets')]");
    private final By myTasksNavLink = By.xpath("//a[contains(@href,'/tasks')]");
    private final By myAllocationNavLink = By.xpath("//a[contains(@href,'/my-allocation')]");
    private final By bandwidthNavLink = By.xpath("//a[contains(@href,'/bandwidth')]");
    private final By allocationsNavLink = By.xpath("//a[contains(@href,'/allocations')]");
    private final By utilizationNavLink = By.xpath("//a[contains(@href,'/utilization')]");
    private final By billingNavLink = By.xpath("//a[contains(@href,'/billing')]");
    private final By approvalsNavLink = By.xpath("//a[contains(@href,'/approvals')]");
    private final By calendarNavLink = By.xpath("//a[contains(@href,'/calendar')]");
    private final By reportsNavLink = By.xpath("//a[contains(@href,'/reports')]");
    private final By teamNavLink = By.xpath("//a[contains(@href,'/team')]");
    private final By projectsNavLink = By.xpath("//a[contains(@href,'/projects')]");
    private final By administrationNavLink = By.xpath("//a[contains(@href,'/settings')]");
    private final By notificationsNavLink = By.xpath("//a[contains(@href,'/notifications')]");

    // Admin Header & Widgets
    private final By globalSearchTrigger = By.id("global-search-trigger");
    private final By userProfileButton = By.cssSelector("button:has(div.avatar), button[id^='radix-'], button:has(span)");
    private final By administratorBadge = By.xpath("//button[contains(text(),'Administrator')] | //span[contains(text(),'Administrator')]");
    private final By overviewTab = By.xpath("//button[contains(text(),'Overview')]");
    private final By approvalsAnalyticsTab = By.xpath("//button[contains(text(),'Approvals Analytics')]");
    private final By pendingSubmissionsTab = By.xpath("//button[contains(text(),'Pending Submissions')]");
    private final By searchModalInput = By.cssSelector("[role='dialog'] input, [cmdk-input], input[placeholder*='Search']");
    private final By searchModal = By.cssSelector("[role='dialog'], [cmdk-dialog], div[data-state='open']");

    public boolean isDashboardLoaded() {
        return waitForUrlContains("/dashboard");
    }

    public boolean isAdministratorBadgeDisplayed() {
        return isDisplayed(administratorBadge);
    }

    public boolean isOverviewTabDisplayed() {
        return isDisplayed(overviewTab);
    }

    public boolean isApprovalsAnalyticsTabDisplayed() {
        return isDisplayed(approvalsAnalyticsTab);
    }

    public boolean isPendingSubmissionsTabDisplayed() {
        return isDisplayed(pendingSubmissionsTab);
    }

    public boolean isAdministrationLinkPresent() {
        return isDisplayed(administrationNavLink);
    }

    public boolean isBillingLinkPresent() {
        return isDisplayed(billingNavLink);
    }

    public boolean isProjectsLinkPresent() {
        return isDisplayed(projectsNavLink);
    }

    public boolean isApprovalsLinkPresent() {
        return isDisplayed(approvalsNavLink);
    }

    public boolean isBandwidthLinkPresent() {
        return isDisplayed(bandwidthNavLink);
    }

    public boolean isUserProfileDisplayed() {
        return isDisplayed(userProfileButton);
    }

    public void clickApprovalsAnalyticsTab() {
        click(approvalsAnalyticsTab);
    }

    public void clickPendingSubmissionsTab() {
        click(pendingSubmissionsTab);
    }

    public void clickOverviewTab() {
        click(overviewTab);
    }

    public AdminSettingsPage clickAdministration() {
        if (isDisplayed(administrationNavLink)) {
            click(administrationNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/settings");
        }
        return new AdminSettingsPage();
    }

    public PMProjectsPage clickProjects() {
        if (isDisplayed(projectsNavLink)) {
            click(projectsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/projects");
        }
        return new PMProjectsPage();
    }

    public PMBillingPage clickBilling() {
        if (isDisplayed(billingNavLink)) {
            click(billingNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/billing");
        }
        return new PMBillingPage();
    }

    public ManagerApprovalsPage clickApprovals() {
        if (isDisplayed(approvalsNavLink)) {
            click(approvalsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/approvals");
        }
        return new ManagerApprovalsPage();
    }

    public void openGlobalSearch() {
        click(globalSearchTrigger);
    }

    public boolean isSearchModalVisible() {
        return isDisplayed(searchModal) || isDisplayed(searchModalInput);
    }
}
