package com.timeflow.pages;

import com.timeflow.utils.ConfigReader;
import org.openqa.selenium.By;

public class ManagerDashboardPage extends BasePage {

    // Manager Specific Navigation Links
    private final By dashboardNavLink = By.xpath("//a[contains(@href,'/dashboard')]");
    private final By timesheetsNavLink = By.xpath("//a[contains(@href,'/timesheets')]");
    private final By myTasksNavLink = By.xpath("//a[contains(@href,'/tasks')]");
    private final By myAllocationNavLink = By.xpath("//a[contains(@href,'/my-allocation')]");
    private final By bandwidthNavLink = By.xpath("//a[contains(@href,'/bandwidth')]");
    private final By allocationsNavLink = By.xpath("//a[contains(@href,'/allocations')]");
    private final By utilizationNavLink = By.xpath("//a[contains(@href,'/utilization')]");
    private final By approvalsNavLink = By.xpath("//a[contains(@href,'/approvals')]");
    private final By calendarNavLink = By.xpath("//a[contains(@href,'/calendar')]");
    private final By reportsNavLink = By.xpath("//a[contains(@href,'/reports')]");
    private final By teamNavLink = By.xpath("//a[contains(@href,'/team')]");
    private final By projectsNavLink = By.xpath("//a[contains(@href,'/projects')]");
    private final By notificationsNavLink = By.xpath("//a[contains(@href,'/notifications')]");

    // Header & Actions
    private final By globalSearchTrigger = By.id("global-search-trigger");
    private final By userProfileButton = By.cssSelector("button:has(div.avatar), button[id^='radix-'], button:has(span)");
    private final By searchModalInput = By.cssSelector("[role='dialog'] input, [cmdk-input], input[placeholder*='Search']");
    private final By searchModal = By.cssSelector("[role='dialog'], [cmdk-dialog], div[data-state='open']");

    public boolean isDashboardLoaded() {
        return waitForUrlContains("/dashboard");
    }

    public boolean isApprovalsLinkPresent() {
        return isDisplayed(approvalsNavLink);
    }

    public boolean isBandwidthLinkPresent() {
        return isDisplayed(bandwidthNavLink);
    }

    public boolean isAllocationsLinkPresent() {
        return isDisplayed(allocationsNavLink);
    }

    public boolean isUtilizationLinkPresent() {
        return isDisplayed(utilizationNavLink);
    }

    public boolean isTeamLinkPresent() {
        return isDisplayed(teamNavLink);
    }

    public boolean isProjectsLinkPresent() {
        return isDisplayed(projectsNavLink);
    }

    public boolean isReportsLinkPresent() {
        return isDisplayed(reportsNavLink);
    }

    public boolean isUserProfileDisplayed() {
        return isDisplayed(userProfileButton);
    }

    public ManagerApprovalsPage clickApprovals() {
        if (isDisplayed(approvalsNavLink)) {
            click(approvalsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/approvals");
        }
        return new ManagerApprovalsPage();
    }

    public ManagerReportsPage clickReports() {
        if (isDisplayed(reportsNavLink)) {
            click(reportsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/reports");
        }
        return new ManagerReportsPage();
    }

    public ManagerTeamPage clickTeam() {
        if (isDisplayed(teamNavLink)) {
            click(teamNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/team");
        }
        return new ManagerTeamPage();
    }

    public void clickBandwidth() {
        if (isDisplayed(bandwidthNavLink)) {
            click(bandwidthNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/bandwidth");
        }
    }

    public void clickAllocations() {
        if (isDisplayed(allocationsNavLink)) {
            click(allocationsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/allocations");
        }
    }

    public void clickUtilization() {
        if (isDisplayed(utilizationNavLink)) {
            click(utilizationNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/utilization");
        }
    }

    public void clickProjects() {
        if (isDisplayed(projectsNavLink)) {
            click(projectsNavLink);
        } else {
            driver.get(ConfigReader.getBaseUrl() + "/projects");
        }
    }

    public void openGlobalSearch() {
        click(globalSearchTrigger);
    }

    public boolean isSearchModalVisible() {
        return isDisplayed(searchModal) || isDisplayed(searchModalInput);
    }
}
