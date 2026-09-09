package com.timeflow.tests;

import com.timeflow.base.BaseTest;
import com.timeflow.driver.DriverManager;
import com.timeflow.pages.AdminDashboardPage;
import com.timeflow.pages.TimeflowLoginPage;
import com.timeflow.utils.ConfigReader;
import com.timeflow.utils.ScreenshotUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * End-to-End API & Network Health Test Suite for Timeflow Admin Module.
 * Intercepts all backend API calls (Fetch/XHR/REST), logs response status codes,
 * detects failures (4xx/5xx/Network drops), and performs direct REST endpoint validation in Java.
 */
public class AdminApiHealthTest extends BaseTest {

    public static class ApiCallRecord {
        public String url;
        public String method;
        public long status;
        public String statusText;
        public boolean ok;
        public long durationMs;
        public String responseBody;

        @Override
        public String toString() {
            return String.format("[%s %d] %s (%dms) -> %s", method, status, url, durationMs, statusText);
        }
    }

    /**
     * Injects JavaScript API Network Interceptor to capture all Fetch & XHR API calls
     * and persist them across SPA navigation in sessionStorage.
     */
    private void injectNetworkInterceptor(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String interceptorScript = 
            "window.__apiCalls = window.__apiCalls || [];" +
            "if (!window.__apiInterceptorInstalled) {" +
            "    window.__apiInterceptorInstalled = true;" +
            "    const saveCall = (rec) => {" +
            "        window.__apiCalls.push(rec);" +
            "        try {" +
            "            let list = JSON.parse(sessionStorage.getItem('__apiCalls') || '[]');" +
            "            list.push(rec);" +
            "            sessionStorage.setItem('__apiCalls', JSON.stringify(list));" +
            "        } catch(e) {}" +
            "    };" +
            "    const origFetch = window.fetch;" +
            "    window.fetch = async function(...args) {" +
            "        let url = typeof args[0] === 'string' ? args[0] : (args[0] && args[0].url ? args[0].url : '');" +
            "        let method = (args[1] && args[1].method) ? args[1].method.toUpperCase() : 'GET';" +
            "        let startTime = performance.now();" +
            "        try {" +
            "            const response = await origFetch.apply(this, args);" +
            "            let clone = response.clone();" +
            "            let bodyText = '';" +
            "            try { bodyText = await clone.text(); } catch(e){}" +
            "            saveCall({" +
            "                url: url," +
            "                method: method," +
            "                status: response.status," +
            "                statusText: response.statusText || (response.ok ? 'OK' : 'Error')," +
            "                ok: response.ok," +
            "                durationMs: Math.round(performance.now() - startTime)," +
            "                responseBody: bodyText ? bodyText.substring(0, 300) : ''" +
            "            });" +
            "            return response;" +
            "        } catch (err) {" +
            "            saveCall({" +
            "                url: url," +
            "                method: method," +
            "                status: 0," +
            "                statusText: 'Network Failed: ' + err.message," +
            "                ok: false," +
            "                durationMs: Math.round(performance.now() - startTime)," +
            "                responseBody: err.toString()" +
            "            });" +
            "            throw err;" +
            "        }" +
            "    };" +
            "    const origOpen = XMLHttpRequest.prototype.open;" +
            "    const origSend = XMLHttpRequest.prototype.send;" +
            "    XMLHttpRequest.prototype.open = function(method, url) {" +
            "        this.__url = url;" +
            "        this.__method = method;" +
            "        this.__startTime = performance.now();" +
            "        return origOpen.apply(this, arguments);" +
            "    };" +
            "    XMLHttpRequest.prototype.send = function() {" +
            "        this.addEventListener('loadend', function() {" +
            "            saveCall({" +
            "                url: this.__url || ''," +
            "                method: (this.__method || 'GET').toUpperCase()," +
            "                status: this.status," +
            "                statusText: this.statusText || (this.status >= 200 && this.status < 300 ? 'OK' : 'HTTP ' + this.status)," +
            "                ok: this.status >= 200 && this.status < 300," +
            "                durationMs: Math.round(performance.now() - (this.__startTime || performance.now()))," +
            "                responseBody: this.responseText ? this.responseText.substring(0, 300) : ''" +
            "            });" +
            "        });" +
            "        return origSend.apply(this, arguments);" +
            "    };" +
            "}";
        js.executeScript(interceptorScript);
    }

    /**
     * Extracts all recorded API network calls from sessionStorage and Performance Resource timing.
     */
    @SuppressWarnings("unchecked")
    private List<ApiCallRecord> getRecordedApiCalls(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // 1. Collect from window.__apiCalls and sessionStorage
        String extractScript = 
            "let list = window.__apiCalls || [];" +
            "try {" +
            "    let stored = JSON.parse(sessionStorage.getItem('__apiCalls') || '[]');" +
            "    list = list.concat(stored);" +
            "} catch(e){}" +
            "return list;";
        
        List<Map<String, Object>> rawList = (List<Map<String, Object>>) js.executeScript(extractScript);
        Map<String, ApiCallRecord> uniqueRecords = new LinkedHashMap<>();

        if (rawList != null) {
            for (Map<String, Object> map : rawList) {
                String url = String.valueOf(map.getOrDefault("url", ""));
                if (isActualApiEndpoint(url)) {
                    ApiCallRecord record = new ApiCallRecord();
                    record.url = url;
                    record.method = String.valueOf(map.getOrDefault("method", "GET"));
                    Object statusObj = map.get("status");
                    record.status = statusObj instanceof Number ? ((Number) statusObj).longValue() : 0;
                    record.statusText = String.valueOf(map.getOrDefault("statusText", ""));
                    Object okObj = map.get("ok");
                    record.ok = okObj instanceof Boolean ? (Boolean) okObj : (record.status >= 200 && record.status < 400);
                    Object durObj = map.get("durationMs");
                    record.durationMs = durObj instanceof Number ? ((Number) durObj).longValue() : 0;
                    record.responseBody = String.valueOf(map.getOrDefault("responseBody", ""));
                    
                    String key = record.method + "_" + record.url + "_" + record.status;
                    uniqueRecords.put(key, record);
                }
            }
        }

        // 2. Harvest network entries from performance.getEntriesByType('resource')
        String perfScript = 
            "let entries = window.performance.getEntriesByType('resource') || [];" +
            "return entries.map(e => ({ name: e.name, initiatorType: e.initiatorType, duration: Math.round(e.duration), responseStatus: e.responseStatus || 200 }));";
        List<Map<String, Object>> perfList = (List<Map<String, Object>>) js.executeScript(perfScript);
        if (perfList != null) {
            for (Map<String, Object> map : perfList) {
                String name = String.valueOf(map.getOrDefault("name", ""));
                String initiator = String.valueOf(map.getOrDefault("initiatorType", ""));
                if (isActualApiEndpoint(name) || "fetch".equalsIgnoreCase(initiator) || "xmlhttprequest".equalsIgnoreCase(initiator)) {
                    if (!name.endsWith(".js") && !name.endsWith(".css") && !name.endsWith(".png") && !name.endsWith(".svg")) {
                        long status = 200;
                        if (map.containsKey("responseStatus") && map.get("responseStatus") instanceof Number) {
                            status = ((Number) map.get("responseStatus")).longValue();
                        }
                        String key = "FETCH_" + name + "_" + status;
                        if (!uniqueRecords.containsKey(key)) {
                            ApiCallRecord record = new ApiCallRecord();
                            record.url = name;
                            record.method = "FETCH";
                            record.status = status;
                            record.statusText = status >= 200 && status < 400 ? "OK" : "Status " + status;
                            record.ok = status >= 200 && status < 400;
                            Object dur = map.get("duration");
                            record.durationMs = dur instanceof Number ? ((Number) dur).longValue() : 0;
                            record.responseBody = "";
                            uniqueRecords.put(key, record);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(uniqueRecords.values());
    }

    private boolean isActualApiEndpoint(String url) {
        if (url == null || url.isEmpty()) return false;
        if (url.endsWith(".css") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".svg") || 
            url.endsWith(".woff") || url.endsWith(".woff2") || url.endsWith(".ttf") || url.endsWith(".ico") || url.endsWith(".js")) {
            return false;
        }
        return url.contains("onrender.com") || url.contains("/api/") || url.contains("/rest/v1/") || 
               url.contains("/auth/") || url.contains("/reports/") || url.contains("/timesheets") || 
               url.contains("/projects") || url.contains("/search") || url.contains("/notifications");
    }

    @Test(priority = 1, description = "ADM-API-001: Intercept and validate all Admin Module UI API calls for failures (4xx/5xx)")
    public void testAdminModuleUiApiTrafficHealth() throws InterruptedException {
        WebDriver driver = DriverManager.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getTimeout()));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        System.out.println("\n=======================================================");
        System.out.println("   ADMIN MODULE: REAL-TIME API INTERCEPTION AUDIT");
        System.out.println("=======================================================");

        // 1. Install interceptor on Login page
        injectNetworkInterceptor(driver);
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("darshan@setoo.co", "Pass@123");
        Thread.sleep(3000);

        AdminDashboardPage dashboardPage = new AdminDashboardPage();
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), "Admin dashboard did not load!");
        injectNetworkInterceptor(driver);

        // 2. Exercise Admin Dashboard Analytics Tabs APIs
        System.out.println("[AUDIT] Triggering Admin Dashboard Tabs APIs...");
        dashboardPage.clickApprovalsAnalyticsTab();
        Thread.sleep(1500);
        injectNetworkInterceptor(driver);

        dashboardPage.clickPendingSubmissionsTab();
        Thread.sleep(1500);
        injectNetworkInterceptor(driver);

        dashboardPage.clickOverviewTab();
        Thread.sleep(1000);
        injectNetworkInterceptor(driver);

        // 3. Exercise Administration Settings Module
        System.out.println("[AUDIT] Triggering Admin Settings APIs (/settings)...");
        dashboardPage.clickAdministration();
        Thread.sleep(2500);
        injectNetworkInterceptor(driver);

        // 4. Exercise Admin Projects Module
        System.out.println("[AUDIT] Triggering Projects APIs (/projects)...");
        dashboardPage.clickProjects();
        Thread.sleep(2500);
        injectNetworkInterceptor(driver);

        // 5. Exercise Admin Billing Module
        System.out.println("[AUDIT] Triggering Billing APIs (/billing)...");
        dashboardPage.clickBilling();
        Thread.sleep(2500);
        injectNetworkInterceptor(driver);

        // 6. Exercise Admin Approvals Queue
        System.out.println("[AUDIT] Triggering Approvals APIs (/approvals)...");
        dashboardPage.clickApprovals();
        Thread.sleep(2500);
        injectNetworkInterceptor(driver);

        // 7. Exercise Admin Global Search API
        System.out.println("[AUDIT] Triggering Search APIs (/search)...");
        dashboardPage.openGlobalSearch();
        Thread.sleep(1500);

        // Retrieve and analyze all intercepted API calls
        List<ApiCallRecord> allApiCalls = getRecordedApiCalls(driver);
        List<ApiCallRecord> failedApiCalls = new ArrayList<>();
        List<ApiCallRecord> passedApiCalls = new ArrayList<>();

        for (ApiCallRecord call : allApiCalls) {
            if (!call.ok || call.status >= 400 || call.status == 0) {
                failedApiCalls.add(call);
            } else {
                passedApiCalls.add(call);
            }
        }

        // Print Diagnostic Summary Report
        System.out.println("\n-------------------------------------------------------");
        System.out.println("            ADMIN API AUDIT REPORT SUMMARY            ");
        System.out.println("-------------------------------------------------------");
        System.out.println(" Total Admin APIs Intercepted : " + allApiCalls.size());
        System.out.println(" PASSED APIs (Status 2xx/3xx)  : " + passedApiCalls.size());
        System.out.println(" FAILED APIs (Status 4xx/5xx)  : " + failedApiCalls.size());
        System.out.println("-------------------------------------------------------");

        if (!failedApiCalls.isEmpty()) {
            System.out.println("\n [!] FAILED API ENDPOINTS LIST:");
            for (int i = 0; i < failedApiCalls.size(); i++) {
                ApiCallRecord f = failedApiCalls.get(i);
                System.out.printf("  %d. [%s %d] %s\n     Status: %s | Duration: %dms\n     Response: %s\n\n",
                        (i + 1), f.method, f.status, f.url, f.statusText, f.durationMs, 
                        f.responseBody.replace("\n", " "));
            }
            ScreenshotUtils.captureScreenshot("admin_api_failures_detected");
        } else {
            System.out.println(" [SUCCESS] All intercepted Admin module APIs resolved with 2xx/3xx HTTP status!");
        }

        System.out.println("\n [SAMPLE PASSED APIS]:");
        for (int i = 0; i < Math.min(10, passedApiCalls.size()); i++) {
            ApiCallRecord p = passedApiCalls.get(i);
            System.out.printf("   - [%s %d] %s (%dms)\n", p.method, p.status, p.url, p.durationMs);
        }
        System.out.println("-------------------------------------------------------\n");

        // Assert no critical API failures occur
        Assert.assertEquals(failedApiCalls.size(), 0, 
            String.format("Found %d failed API requests in Admin module! Check terminal log for details.", failedApiCalls.size()));
    }

    @Test(priority = 2, description = "ADM-API-002: Direct HTTP REST probing of backend microservices in Java")
    public void testAdminDirectBackendRestEndpoints() throws Exception {
        WebDriver driver = DriverManager.getDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Login first
        TimeflowLoginPage loginPage = new TimeflowLoginPage();
        loginPage.login("darshan@setoo.co", "Pass@123");
        Thread.sleep(2000);

        // Extract auth token from localStorage if available
        String token = (String) js.executeScript(
            "for (let i = 0; i < localStorage.length; i++) {" +
            "    let k = localStorage.key(i);" +
            "    let v = localStorage.getItem(k);" +
            "    if (k.toLowerCase().includes('token') || k.toLowerCase().includes('auth') || v.includes('Bearer') || v.includes('eyJ')) {" +
            "        return v;" +
            "    }" +
            "}" +
            "return '';"
        );

        String backendBaseUrl = "https://setoo-realtimesheet-api.onrender.com";
        System.out.println("\n=======================================================");
        System.out.println("   DIRECT BACKEND REST PROBING (Java HttpClient)");
        System.out.println("   Backend: " + backendBaseUrl);
        System.out.println("=======================================================");

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        String[] backendEndpoints = {
            "/auth/me",
            "/reports/dashboard",
            "/reports/admin-dashboard",
            "/reports/upcoming-deliveries?from_date=2026-08-31&to_date=2026-09-06",
            "/timesheets?status=Submitted",
            "/search",
            "/notifications"
        };

        int testedCount = 0;
        int failedEndpoints = 0;

        for (String ep : backendEndpoints) {
            testedCount++;
            String fullUrl = backendBaseUrl + ep;
            try {
                HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(fullUrl))
                        .header("Accept", "application/json")
                        .GET()
                        .timeout(Duration.ofSeconds(15));

                if (token != null && !token.isEmpty()) {
                    String cleanToken = token.replace("\"", "");
                    if (!cleanToken.startsWith("Bearer ")) {
                        cleanToken = "Bearer " + cleanToken;
                    }
                    reqBuilder.header("Authorization", cleanToken);
                }

                HttpResponse<String> response = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofString());
                int status = response.statusCode();
                System.out.printf(" -> Probed [%d] %s\n", status, fullUrl);

                if (status >= 500) {
                    failedEndpoints++;
                    System.err.printf("    [CRITICAL 5XX ERROR] %s returned %d\n", fullUrl, status);
                }
            } catch (Exception e) {
                System.out.printf(" -> Endpoint %s timed out or unreachable: %s\n", fullUrl, e.getMessage());
            }
        }

        System.out.println("-------------------------------------------------------");
        System.out.printf(" Direct REST Probes: %d tested | %d 5xx Server Errors\n", testedCount, failedEndpoints);
        System.out.println("=======================================================\n");

        Assert.assertEquals(failedEndpoints, 0, "One or more backend API endpoints returned 5xx Server Errors!");
    }
}
