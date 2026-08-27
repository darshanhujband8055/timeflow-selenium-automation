package com.timeflow.pages;

import org.openqa.selenium.By;

public class ManagerApprovalsPage extends BasePage {

    // Locators
    private final By mainContent = By.cssSelector("main, [role='main'], div.container");
    private final By pendingTab = By.xpath("//button[contains(text(),'Pending')] | //a[contains(text(),'Pending')]");
    private final By approvedTab = By.xpath("//button[contains(text(),'Approved')] | //a[contains(text(),'Approved')]");
    private final By rejectedTab = By.xpath("//button[contains(text(),'Rejected')] | //a[contains(text(),'Rejected')]");

    public boolean isApprovalsPageLoaded() {
        return waitForUrlContains("/approvals") || isDisplayed(mainContent);
    }

    public boolean isPendingTabDisplayed() {
        return isDisplayed(pendingTab);
    }

    public boolean isApprovedTabDisplayed() {
        return isDisplayed(approvedTab);
    }

    public boolean isRejectedTabDisplayed() {
        return isDisplayed(rejectedTab);
    }
}
