package com.timeflow.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class BillingPage extends BasePage {

    // Page Header & Subtitle
    private final By pageHeading = By.xpath("//h1[contains(text(),'Billable Hours & Accounts Dashboard')]");
    private final By pageSubheading = By.xpath("//p[contains(text(),'Manage project billable hour budgets')]");

    // Stat Cards
    private final By totalPlannedBillableCard = By.xpath("//*[contains(text(),'Total Planned Billable')]");
    private final By totalConsumedCard = By.xpath("//*[contains(text(),'Total Consumed')]");
    private final By remainingBalanceCard = By.xpath("//*[contains(text(),'Remaining Balance')]");
    private final By estRevenueCard = By.xpath("//*[contains(text(),'Est. Revenue')]");
    private final By extraBurnedCard = By.xpath("//*[contains(text(),'Extra Burned')]");

    // Project Grid & Filters
    private final By projectBillableHoursSection = By.xpath("//*[contains(text(),'Project Billable Hours')]");
    private final By filterTabs = By.xpath("//button[contains(text(),'Billable') or contains(text(),'Non-Billable') or contains(text(),'Archived')]");

    // Client Summary Section
    private final By clientSummaryHeading = By.xpath("//*[contains(text(),'Client-wise Billing Summary')]");
    private final By summaryTable = By.tagName("table");

    // Navigation item
    private final By billingSidebarLink = By.xpath("//a[contains(@href,'/billing')]");

    public boolean isBillingPageLoaded() {
        return waitForUrlContains("/billing") && isDisplayed(pageHeading);
    }

    public String getHeadingText() {
        return getText(pageHeading);
    }

    public boolean isHeadingDisplayed() {
        return isDisplayed(pageHeading);
    }

    public boolean isSubheadingDisplayed() {
        return isDisplayed(pageSubheading);
    }

    public boolean isTotalPlannedBillableDisplayed() {
        return isDisplayed(totalPlannedBillableCard);
    }

    public boolean isTotalConsumedDisplayed() {
        return isDisplayed(totalConsumedCard);
    }

    public boolean isRemainingBalanceDisplayed() {
        return isDisplayed(remainingBalanceCard);
    }

    public boolean isEstRevenueDisplayed() {
        return isDisplayed(estRevenueCard);
    }

    public boolean isExtraBurnedDisplayed() {
        return isDisplayed(extraBurnedCard);
    }

    public boolean areStatCardsDisplayed() {
        return isTotalPlannedBillableDisplayed() 
                && isTotalConsumedDisplayed() 
                && isRemainingBalanceDisplayed() 
                && isEstRevenueDisplayed() 
                && isExtraBurnedDisplayed();
    }

    public boolean isProjectGridSectionDisplayed() {
        return isDisplayed(projectBillableHoursSection);
    }

    public boolean isClientSummarySectionDisplayed() {
        return isDisplayed(clientSummaryHeading) || isDisplayed(summaryTable);
    }

    public boolean isSidebarBillingLinkPresent() {
        return isDisplayed(billingSidebarLink);
    }

    public boolean hasRestrictedAccessOrNoFinancialData() {
        // Returns true if sensitive billing widgets/cards are completely absent from the DOM
        List<WebElement> headings = driver.findElements(pageHeading);
        List<WebElement> statCards = driver.findElements(totalPlannedBillableCard);
        return headings.isEmpty() && statCards.isEmpty();
    }
}
