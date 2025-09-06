package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TestUtils {
    
    // Wait metodları
    public static void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // SAYFA TAM YÜKLENENE KADAR BEKLE (KONTROL İLE)
    public static void waitForPageToLoad(WebDriver driver, int timeoutSeconds) {
        try {
            // Driver'ın aktif olduğunu kontrol et
            if (driver == null) {
                logWarning("Driver is null, skipping page load check");
                return;
            }
            
            // Driver'ın hala aktif olduğunu kontrol et
            try {
                driver.getCurrentUrl();
            } catch (Exception e) {
                logWarning("Driver is no longer active, skipping page load check");
                return;
            }
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            try {
                // JavaScript readyState kontrolü
                wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
                
                // jQuery yüklenmişse onu da kontrol et
                wait.until(ExpectedConditions.jsReturnsValue("return typeof jQuery === 'undefined' || jQuery.active === 0"));
                
                logSuccess("Page loaded completely");
            } catch (Exception e) {
                // AJAX istekleri tamamlandı mı kontrol et (daha esnek)
                try {
                    wait.until(ExpectedConditions.jsReturnsValue("return window.performance.getEntriesByType('navigation')[0].loadEventEnd > 0"));
                    logSuccess("Page loaded completely (fallback check)");
                } catch (Exception e2) {
                    logWarning("Page load timeout, but continuing...");
                }
            }
        } catch (Exception e) {
            logWarning("Page load check failed, but continuing...");
        }
    }
    
    // SAYFA TAM YÜKLENENE KADAR BEKLE (DEFAULT 20 saniye)
    public static void waitForPageToLoad(WebDriver driver) {
        waitForPageToLoad(driver, 20);
    }
    
    // ELEMENT YÜKLENENE KADAR BEKLE
    public static void waitForElement(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
    
    // ELEMENT GÖRÜNÜR OLANA KADAR BEKLE
    public static void waitForElementVisible(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    // ELEMENT TIKLANABİLİR OLANA KADAR BEKLE
    public static void waitForElementClickable(WebDriver driver, By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }
    
    // Element kontrolleri
    public static boolean isElementDisplayed(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public static boolean isElementEnabled(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Utility metodlar
    public static String getCurrentUrl(WebDriver driver) {
        return driver.getCurrentUrl();
    }
    
    public static void takeScreenshot(WebDriver driver, String fileName) {
        // TODO: Implement screenshot functionality
        System.out.println("Screenshot would be taken: " + fileName);
    }
    
    public static void refreshPage(WebDriver driver) {
        driver.navigate().refresh();
    }
    
    public static void maximizeBrowser(WebDriver driver) {
        driver.manage().window().maximize();
    }
    
    // Logging metodları
    public static void logSuccess(String message) {
        System.out.println("✅ SUCCESS: " + message);
    }
    
    public static void logError(String message, Exception e) {
        System.err.println("❌ ERROR: " + message + " - " + e.getMessage());
    }
    
    public static void logInfo(String message) {
        System.out.println("ℹ️ INFO: " + message);
    }
    
    public static void logWarning(String message) {
        System.out.println("⚠️ WARNING: " + message);
    }
}
