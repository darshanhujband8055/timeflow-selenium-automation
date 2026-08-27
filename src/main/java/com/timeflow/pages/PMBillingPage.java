package com.timeflow.pages;

import org.openqa.selenium.By;

public class PMBillingPage extends BasePage {

    private final By mainContent = By.cssSelector("main, [role='main'], div.container");

    public boolean isBillingPageLoaded() {
        return waitForUrlContains("/billing") || isDisplayed(mainContent);
    }
}
