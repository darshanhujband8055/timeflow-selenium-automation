package com.timeflow.pages;

import com.timeflow.driver.DriverManager;
import com.timeflow.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getTimeout()));
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        try {
            waitForClickability(locator).click();
        } catch (Exception e) {
            try {
                WebElement el = driver.findElement(locator);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", el);
            } catch (Exception ex) {
                waitForClickability(locator).click();
            }
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitForVisibility(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            try {
                return !driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed();
            } catch (Exception ex) {
                return false;
            }
        }
    }

    protected String getAttribute(By locator, String attributeName) {
        return waitForVisibility(locator).getAttribute(attributeName);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean waitForUrlContains(String fraction) {
        try {
            return wait.until(ExpectedConditions.urlContains(fraction));
        } catch (Exception e) {
            return driver.getCurrentUrl().contains(fraction);
        }
    }
}
