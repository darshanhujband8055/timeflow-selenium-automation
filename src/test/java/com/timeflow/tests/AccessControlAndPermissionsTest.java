package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.driver.DriverManager;
import com.timeflow.pages.AdminDashboardPage;
import com.timeflow.pages.BillingPage;
import com.timeflow.pages.EmployeeDashboardPage;
import com.timeflow.pages.PMDashboardPage;
import com.timeflow.pages.TimeflowLoginPage;
import com.timeflow.utils.ConfigReader;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AccessControlAndPermissionsTest extends BaseTest {

    /**
     * Test Case 1.1: Unauthorized Access
     * Action: Log in as a standard Employee and attempt to navigate to /billing or call GET /billing.
     * Expected Result: Access denied (403 Forbidden or redirected, and no billing/financial content exposed).
     */
    @Test(priority = 1, description = "TC 1.1: Unauthorized Access - Standard Employee is blocked from viewing /billing dashboard")
    public void test1_1_UnauthorizedAccess_EmployeeBlockedFromBilling() {
        // Step 1: Log in as standard Employee
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("shubham.shinde@setoo.co", "Pass@123");

        EmployeeDashboardPage employeeDashboard = new EmployeeDashboardPage();
        Assert.assertTrue(employeeDashboard.isDashboardLoaded(), "Employee dashboard failed to load after login!");

        // Step 2: Verify that Billing is not present in employee's navigation sidebar
        BillingPage billingPage = new BillingPage();
        Assert.assertFalse(billingPage.isSidebarBillingLinkPresent(), 
                "Security Failure: Billing navigation link is visible to standard Employee in sidebar!");

        // Step 3: Attempt direct URL navigation to /billing
        String billingUrl = ConfigReader.getBaseUrl() + "/billing";
        DriverManager.getDriver().get(billingUrl);

        // Step 4: Verify that financial/billing dashboard is guarded and NOT exposed to Employee
        Assert.assertTrue(billingPage.hasRestrictedAccessOrNoFinancialData(), 
                "Security Failure: Financial stat cards and billing dashboard are exposed to standard Employee!");
        Assert.assertFalse(billingPage.isHeadingDisplayed(), 
                "Security Failure: 'Billable Hours & Accounts Dashboard' heading should not be accessible to standard Employee!");
        Assert.assertFalse(billingPage.areStatCardsDisplayed(), 
                "Security Failure: Financial stat cards should not be rendered for standard Employee!");
    }

    /**
     * Test Case 1.2: Authorized View - Admin
     * Action: Log in as Admin and navigate to /billing.
     * Expected Result: Page loads successfully displaying stat cards and project grid/table.
     */
    @Test(priority = 2, description = "TC 1.2: Authorized View - Admin can access /billing with stat cards and project grid")
    public void test1_2_AuthorizedView_AdminAccessesBillingDashboard() {
        // Step 1: Log in as Admin
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("darshan@setoo.co", "Pass@123");

        AdminDashboardPage adminDashboard = new AdminDashboardPage();
        Assert.assertTrue(adminDashboard.isDashboardLoaded(), "Admin dashboard failed to load after login!");

        // Step 2: Navigate to Billing page via sidebar link
        Assert.assertTrue(adminDashboard.isBillingLinkPresent(), "Billing link missing from Admin sidebar!");
        adminDashboard.clickBilling();

        // Step 3: Verify Page heading and description
        BillingPage billingPage = new BillingPage();
        Assert.assertTrue(billingPage.isBillingPageLoaded(), "Billing page failed to load for Admin!");
        Assert.assertEquals(billingPage.getHeadingText(), "Billable Hours & Accounts Dashboard", 
                "Billing page heading mismatch for Admin!");
        Assert.assertTrue(billingPage.isSubheadingDisplayed(), "Billing page subheading not displayed for Admin!");

        // Step 4: Verify Stat Cards
        Assert.assertTrue(billingPage.isTotalPlannedBillableDisplayed(), "Total Planned Billable card missing for Admin!");
        Assert.assertTrue(billingPage.isTotalConsumedDisplayed(), "Total Consumed card missing for Admin!");
        Assert.assertTrue(billingPage.isRemainingBalanceDisplayed(), "Remaining Balance card missing for Admin!");
        Assert.assertTrue(billingPage.isEstRevenueDisplayed(), "Est. Revenue card missing for Admin!");
        Assert.assertTrue(billingPage.isExtraBurnedDisplayed(), "Extra Burned card missing for Admin!");
        Assert.assertTrue(billingPage.areStatCardsDisplayed(), "Stat cards suite incomplete for Admin!");

        // Step 5: Verify Project Grid and Client Summary Table
        Assert.assertTrue(billingPage.isProjectGridSectionDisplayed(), "Project Billable Hours section missing for Admin!");
        Assert.assertTrue(billingPage.isClientSummarySectionDisplayed(), "Client-wise Billing Summary table missing for Admin!");
    }

    /**
     * Test Case 1.2: Authorized View - Project Manager (PM)
     * Action: Log in as PM and navigate to /billing.
     * Expected Result: Page loads successfully displaying stat cards and project grid/table.
     */
    @Test(priority = 3, description = "TC 1.2: Authorized View - PM can access /billing with stat cards and project grid")
    public void test1_2_AuthorizedView_PMAccessesBillingDashboard() {
        // Step 1: Log in as Project Manager
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("rohan@setoo.co", "Pass@123");

        PMDashboardPage pmDashboard = new PMDashboardPage();
        Assert.assertTrue(pmDashboard.isDashboardLoaded(), "PM dashboard failed to load after login!");

        // Step 2: Navigate to Billing page via sidebar link
        Assert.assertTrue(pmDashboard.isBillingLinkPresent(), "Billing link missing from PM sidebar!");
        pmDashboard.clickBilling();

        // Step 3: Verify Page heading and description
        BillingPage billingPage = new BillingPage();
        Assert.assertTrue(billingPage.isBillingPageLoaded(), "Billing page failed to load for PM!");
        Assert.assertEquals(billingPage.getHeadingText(), "Billable Hours & Accounts Dashboard", 
                "Billing page heading mismatch for PM!");
        Assert.assertTrue(billingPage.isSubheadingDisplayed(), "Billing page subheading not displayed for PM!");

        // Step 4: Verify Stat Cards
        Assert.assertTrue(billingPage.isTotalPlannedBillableDisplayed(), "Total Planned Billable card missing for PM!");
        Assert.assertTrue(billingPage.isTotalConsumedDisplayed(), "Total Consumed card missing for PM!");
        Assert.assertTrue(billingPage.isRemainingBalanceDisplayed(), "Remaining Balance card missing for PM!");
        Assert.assertTrue(billingPage.isEstRevenueDisplayed(), "Est. Revenue card missing for PM!");
        Assert.assertTrue(billingPage.isExtraBurnedDisplayed(), "Extra Burned card missing for PM!");
        Assert.assertTrue(billingPage.areStatCardsDisplayed(), "Stat cards suite incomplete for PM!");

        // Step 5: Verify Project Grid and Client Summary Table
        Assert.assertTrue(billingPage.isProjectGridSectionDisplayed(), "Project Billable Hours section missing for PM!");
        Assert.assertTrue(billingPage.isClientSummarySectionDisplayed(), "Client-wise Billing Summary table missing for PM!");
    }

    /**
     * Test Case 1.3: Budget Configuration Guard
     * Action: Attempt to create/update a budget via POST /billing/budgets as a PM or Accounts user.
     * Expected Result: Action restricted (403 Forbidden / mutation blocked). Only Admin can configure budgets.
     */
    @Test(priority = 4, description = "TC 1.3: Budget Configuration Guard - Mutation restricted for non-admin roles")
    public void test1_3_BudgetConfigurationGuard_RestrictedForPM() {
        // Step 1: Log in as PM
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("rohan@setoo.co", "Pass@123");

        PMDashboardPage pmDashboard = new PMDashboardPage();
        Assert.assertTrue(pmDashboard.isDashboardLoaded(), "PM dashboard failed to load!");

        // Step 2: Attempt to call budget creation/update endpoint as PM
        JavascriptExecutor js = (JavascriptExecutor) DriverManager.getDriver();
        String script = 
            "var callback = arguments[arguments.length - 1];" +
            "fetch('/billing/budgets', {" +
            "  method: 'POST'," +
            "  headers: {'Content-Type': 'application/json'}," +
            "  body: JSON.stringify({projectId: 'test', plannedHours: 100, rate: 50})" +
            "}).then(r => callback({status: r.status, ok: r.ok}))" +
            "  .catch(e => callback({error: e.toString()}));";

        DriverManager.getDriver().manage().timeouts().setScriptTimeout(java.time.Duration.ofSeconds(10));
        Object result = js.executeAsyncScript(script);

        // Step 3: Assert that unauthorized mutation request is NOT successful (either 403 Forbidden, 404, or rejected)
        Assert.assertNotNull(result, "Budget configuration response should not be null!");
        String responseStr = result.toString();
        Assert.assertFalse(responseStr.contains("ok=true") && responseStr.contains("status=200"), 
                "Security Failure: PM user was able to execute budget mutation via POST /billing/budgets!");
    }
}
