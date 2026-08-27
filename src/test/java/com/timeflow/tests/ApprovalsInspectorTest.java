package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.driver.DriverManager;
import com.timeflow.pages.TimeflowLoginPage;
import com.timeflow.utils.ScreenshotUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

public class ApprovalsInspectorTest extends BaseTest {

    @Test
    public void inspectApprovalsAcrossRoles() {
        WebDriver driver = DriverManager.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String[][] users = {
            {"PM", "rohan@setoo.co", "Pass@123"},
            {"Manager", "rutuja@setoo.co", "Pass@123"},
            {"Admin", "darshan@setoo.co", "Pass@123"}
        };

        for (String[] user : users) {
            String role = user[0];
            String email = user[1];
            String password = user[2];

            System.out.println("\n=============================================");
            System.out.println("LOGGING IN AS: " + role + " (" + email + ")");
            System.out.println("=============================================");

            driver.get("https://timeflow.setoo.in/login");
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

            TimeflowLoginPage loginPage = new TimeflowLoginPage();
            loginPage.login(email, password);
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

            System.out.println("[INFO] Landing URL: " + driver.getCurrentUrl());
            
            // Navigate to Approvals
            List<WebElement> approvalsLinks = driver.findElements(By.xpath("//a[contains(@href,'/approvals')] | //nav//a[.//span[contains(text(),'Approvals')]]"));
            if (!approvalsLinks.isEmpty()) {
                System.out.println("[INFO] Found sidebar link: " + approvalsLinks.get(0).getText());
                js.executeScript("arguments[0].click();", approvalsLinks.get(0));
            } else {
                driver.get("https://timeflow.setoo.in/approvals");
            }

            try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
            ScreenshotUtils.captureScreenshot("approvals_view_" + role);

            System.out.println("[INFO] Approvals Page URL for " + role + ": " + driver.getCurrentUrl());

            List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr, div.border.rounded-lg, [role='row']"));
            System.out.println("[INFO] Rows/Items found for " + role + ": " + rows.size());
            for (int i = 0; i < Math.min(5, rows.size()); i++) {
                String text = rows.get(i).getText().trim();
                if (!text.isEmpty()) {
                    System.out.println("  -> Item " + (i + 1) + ": " + text.replace("\n", " | "));
                }
            }

            List<WebElement> allButtons = driver.findElements(By.tagName("button"));
            System.out.println("[INFO] Visible Buttons for " + role + ":");
            for (WebElement b : allButtons) {
                if (b.isDisplayed() && !b.getText().trim().isEmpty()) {
                    System.out.println("     * Button: '" + b.getText().trim() + "'");
                }
            }

            // Clear cookies for next user
            driver.manage().deleteAllCookies();
        }
    }
}
