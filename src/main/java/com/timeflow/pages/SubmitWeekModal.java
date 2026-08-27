package com.timeflow.pages;

import org.openqa.selenium.By;

public class SubmitWeekModal extends BasePage {

    // Locators
    private final By modalDialog = By.cssSelector("[role='dialog'], [data-state='open'], div.modal");
    private final By modalHeading = By.cssSelector("[role='dialog'] h2, [role='dialog'] h3, [role='dialog'] [class*='title']");
    private final By managerSelect = By.cssSelector("[role='dialog'] [role='combobox'], [role='dialog'] select, [role='dialog'] button[id*='manager'], [role='dialog'] input[placeholder*='manager' i]");
    private final By cancelButton = By.xpath("//div[@role='dialog']//button[contains(text(),'Cancel')] | //div[@role='dialog']//button[contains(@aria-label,'Close')]");
    private final By submitConfirmButton = By.xpath("//div[@role='dialog']//button[contains(text(),'Submit') or contains(text(),'Confirm')]");

    public boolean isModalDisplayed() {
        return isDisplayed(modalDialog);
    }

    public String getModalTitle() {
        return getText(modalHeading);
    }

    public boolean isManagerSelectDisplayed() {
        return isDisplayed(managerSelect);
    }

    public void clickCancel() {
        if (isDisplayed(cancelButton)) {
            click(cancelButton);
        }
    }

    public void clickConfirmSubmit() {
        if (isDisplayed(submitConfirmButton)) {
            click(submitConfirmButton);
        }
    }
}
