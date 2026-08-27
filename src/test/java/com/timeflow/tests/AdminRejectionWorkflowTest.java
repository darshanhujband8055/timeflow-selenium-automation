package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.driver.DriverManager;
import com.timeflow.pages.TimeflowLoginPage;
import com.timeflow.utils.ScreenshotUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AdminRejectionWorkflowTest extends BaseTest {

    @Test
    public void testAdminBatchRejectionWorkflow() {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Login as Admin
        System.out.println("=== STEP 1: Logging in as Admin (darshan@setoo.co) ===");
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("darshan@setoo.co", "Pass@123");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Active users')]")));
        System.out.println("[INFO] Admin logged in.");

        // Check the Role dropdown at the top right (e.g. 'Administrator ▾')
        List<WebElement> roleDropdowns = driver.findElements(By.xpath("//button[contains(.,'Administrator')] | //button[contains(@class,'cursor-pointer') and .//span[contains(text(),'Admin')]]"));
        if (!roleDropdowns.isEmpty()) {
            System.out.println("[INFO] Clicking Role Switcher dropdown: " + roleDropdowns.get(0).getText());
            js.executeScript("arguments[0].click();", roleDropdowns.get(0));
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            ScreenshotUtils.captureScreenshot("01_role_dropdown_opened");

            // Print all menu items
            List<WebElement> menuItems = driver.findElements(By.cssSelector("[role='menuitem'], div[role='menu'] button, [role='listbox'] *"));
            System.out.println("[ROLE MENU ITEMS COUNT]: " + menuItems.size());
            for (WebElement item : menuItems) {
                System.out.println("  -> Role Option: '" + item.getText() + "'");
                if (item.getText().contains("Manager")) {
                    System.out.println("--> Switching view to Manager...");
                    js.executeScript("arguments[0].click();", item);
                    try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
                    break;
                }
            }
        }

        ScreenshotUtils.captureScreenshot("02_after_role_switch");

        // Navigate to /approvals
        System.out.println("=== Navigating to /approvals ===");
        driver.get("https://timeflow.setoo.in/approvals");
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
        ScreenshotUtils.captureScreenshot("03_approvals_after_switch");

        System.out.println("[CURRENT URL]: " + driver.getCurrentUrl());
        List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr, div.border.rounded-lg, [role='row']"));
        System.out.println("[PENDING APPROVAL ROWS]: " + rows.size());
        for (int i = 0; i < rows.size(); i++) {
            System.out.println("[ROW " + (i + 1) + "]: " + rows.get(i).getText().replace("\n", " | "));
        }

        // Also inspect Manager account directly if needed
        System.out.println("=== STEP 2: Logging in as Manager (rutuja@setoo.co) ===");
        driver.get("https://timeflow.setoo.in/login");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        
        loginPage = new TimeflowLoginPage();
        loginPage.login("rutuja@setoo.co", "Pass@123");
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        driver.get("https://timeflow.setoo.in/approvals");
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
        ScreenshotUtils.captureScreenshot("04_manager_approvals_view");

        System.out.println("[MANAGER APPROVALS URL]: " + driver.getCurrentUrl());
        List<WebElement> mgrRows = driver.findElements(By.cssSelector("table tbody tr, div.border.rounded-lg, [role='row']"));
        System.out.println("[MANAGER PENDING ROWS]: " + mgrRows.size());
        for (int i = 0; i < mgrRows.size(); i++) {
            System.out.println("[MGR ROW " + (i + 1) + "]: " + mgrRows.get(i).getText().replace("\n", " | "));
        }

        // Print reject buttons on Manager view
        List<WebElement> mgrRejectBtns = driver.findElements(By.xpath("//button[contains(text(),'Reject') or contains(text(),'reject')]"));
        System.out.println("[MANAGER REJECT BUTTONS COUNT]: " + mgrRejectBtns.size());
        for (WebElement btn : mgrRejectBtns) {
            System.out.println("  -> Reject Button: '" + btn.getText() + "' | displayed: " + btn.isDisplayed());
        }

        // Test Reject button on Manager view if available
        if (!mgrRejectBtns.isEmpty()) {
            System.out.println("=== Testing Reject button on Manager view ===");
            for (WebElement btn : mgrRejectBtns) {
                if (btn.isDisplayed() && btn.isEnabled()) {
                    System.out.println("Clicking reject button: " + btn.getText());
                    js.executeScript("arguments[0].click();", btn);
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    ScreenshotUtils.captureScreenshot("05_manager_rejection_clicked");

                    List<WebElement> dialogs = driver.findElements(By.cssSelector("[role='dialog'], [role='alertdialog']"));
                    System.out.println("[REJECTION MODAL OPENED]: " + (!dialogs.isEmpty()));
                    if (!dialogs.isEmpty()) {
                        System.out.println("[MODAL TEXT]:\n" + dialogs.get(0).getText());
                        List<WebElement> textareas = dialogs.get(0).findElements(By.cssSelector("textarea, input[type='text']"));
                        System.out.println("[REASON INPUT FIELD PRESENT]: " + (!textareas.isEmpty()));
                    }
                    break;
                }
            }
        }
    }
}
