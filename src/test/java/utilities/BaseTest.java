package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public abstract class BaseTest {
    
    // DRIVER YÖNETİMİ 
    protected WebDriver driver;
    
    // DRIVER AKTİF Mİ KONTROL ET
    protected boolean isDriverActive() {
        try {
            if (driver != null) {
                // Driver'ın aktif olup olmadığını kontrol et
                driver.getCurrentUrl();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // DRIVER GETIR - Aktif değilse yeni oluştur
    protected WebDriver getActiveDriver() {
        if (!isDriverActive()) {
            driver = MyDriver.getDriver();
            TestUtils.logInfo("New driver created");
        } else {
            TestUtils.logInfo("Using existing active driver");
        }
        return driver;
    }

    // ========== NAVIGATION & BROWSER CONTROL ==========
    
    // Sayfa yenileme
    protected void refreshPage() {
        driver.navigate().refresh();
        TestUtils.logInfo("Page refreshed");
    }

    // Geri gitme
    protected void goBack() {
        driver.navigate().back();
        TestUtils.logInfo("Navigated back");
    }

    // İleri gitme
    protected void goForward() {
        driver.navigate().forward();
        TestUtils.logInfo("Navigated forward");
    }

    // Browser maximize
    protected void maximizeBrowser() {
        driver.manage().window().maximize();
        TestUtils.logInfo("Browser maximized");
    }

    // Browser minimize
    protected void minimizeBrowser() {
        driver.manage().window().minimize();
        TestUtils.logInfo("Browser minimized");
    }

    // Pencere boyutunu ayarla
    protected void setWindowSize(int width, int height) {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(width, height));
        TestUtils.logInfo("Window size set to: " + width + "x" + height);
    }

    // ========== ELEMENT INTERACTION HELPERS ==========
    
    // Element'e tıklama (wait ile)
    protected void clickElement(By locator, int timeoutSeconds) {
        TestUtils.waitForElementClickable(driver, locator, timeoutSeconds);
        driver.findElement(locator).click();
        TestUtils.logSuccess("Element clicked: " + locator);
    }

    // Text yazma (clear + sendKeys)
    protected void typeText(By locator, String text, int timeoutSeconds) {
        TestUtils.waitForElementVisible(driver, locator, timeoutSeconds);
        WebElement element = driver.findElement(locator);
        element.clear();
        element.sendKeys(text);
        TestUtils.logSuccess("Text entered: " + text);
    }

    // Dropdown seçimi
    protected void selectDropdownOption(By dropdownLocator, By optionLocator, int timeoutSeconds) {
        TestUtils.waitForElementClickable(driver, dropdownLocator, timeoutSeconds);
        driver.findElement(dropdownLocator).click();
        TestUtils.waitForElementClickable(driver, optionLocator, timeoutSeconds);
        driver.findElement(optionLocator).click();
        TestUtils.logSuccess("Dropdown option selected");
    }

    // Hover over element
    protected void hoverOverElement(By locator, int timeoutSeconds) {
        TestUtils.waitForElementVisible(driver, locator, timeoutSeconds);
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.moveToElement(driver.findElement(locator)).perform();
        TestUtils.logSuccess("Hovered over element: " + locator);
    }

    // Çift tıklama
    protected void doubleClickElement(By locator, int timeoutSeconds) {
        TestUtils.waitForElementClickable(driver, locator, timeoutSeconds);
        org.openqa.selenium.interactions.Actions actions = new org.openqa.selenium.interactions.Actions(driver);
        actions.doubleClick(driver.findElement(locator)).perform();
        TestUtils.logSuccess("Double clicked element: " + locator);
    }

    // ========== VERIFICATION HELPERS ==========
    
    // URL kontrolü
    protected boolean isCurrentUrlContains(String expectedText) {
        return driver.getCurrentUrl().contains(expectedText);
    }

    // Sayfa başlığı kontrolü
    protected boolean isPageTitleContains(String expectedText) {
        return driver.getTitle().contains(expectedText);
    }

    // Element görünürlük kontrolü
    protected boolean isElementDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Element aktif mi kontrolü
    protected boolean isElementEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // Element text'ini al
    protected String getElementText(By locator) {
        try {
            return driver.findElement(locator).getText();
        } catch (Exception e) {
            TestUtils.logError("Failed to get element text", e);
            return "";
        }
    }

    // Element attribute'unu al
    protected String getElementAttribute(By locator, String attribute) {
        try {
            return driver.findElement(locator).getAttribute(attribute);
        } catch (Exception e) {
            TestUtils.logError("Failed to get element attribute", e);
            return "";
        }
    }

    // ========== SCROLL & ELEMENT FINDING HELPERS ==========
    
    // Element bulana kadar scroll yap (YUKARI-AŞAĞI)
    protected WebElement scrollToFindElement(By locator, int maxScrolls) {
        TestUtils.logInfo("Searching for element with scroll: " + locator);
        
        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    TestUtils.logSuccess("Element found after " + (i + 1) + " scrolls");
                    return element;
                }
            } catch (Exception e) {
                // Element bulunamadı, scroll yap
            }
            
            scrollDown();
            TestUtils.waitForSeconds(1);
        }
        
        // Yukarı doğru da scroll yap
        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    TestUtils.logSuccess("Element found after scrolling up " + (i + 1) + " times");
                    return element;
                }
            } catch (Exception e) {
                // Element bulunamadı, scroll yap
            }
            
            scrollUp();
            TestUtils.waitForSeconds(1);
        }
        
        throw new RuntimeException("Element not found after scrolling: " + locator);
    }

    // Element bulana kadar scroll yap (SADECE AŞAĞI)
    protected WebElement scrollDownToFindElement(By locator, int maxScrolls) {
        TestUtils.logInfo("Searching for element by scrolling down: " + locator);
        
        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    TestUtils.logSuccess("Element found after " + (i + 1) + " scrolls down");
                    return element;
                }
            } catch (Exception e) {
                // Element bulunamadı, scroll yap
            }
            
            scrollDown();
            TestUtils.waitForSeconds(1);
        }
        
        throw new RuntimeException("Element not found after scrolling down: " + locator);
    }

    // Element bulana kadar scroll yap (SADECE YUKARI)
    protected WebElement scrollUpToFindElement(By locator, int maxScrolls) {
        TestUtils.logInfo("Searching for element by scrolling up: " + locator);
        
        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    TestUtils.logSuccess("Element found after " + (i + 1) + " scrolls up");
                    return element;
                }
            } catch (Exception e) {
                // Element bulunamadı, scroll yap
            }
            
            scrollUp();
            TestUtils.waitForSeconds(1);
        }
        
        throw new RuntimeException("Element not found after scrolling up: " + locator);
    }

    // Aşağı scroll
    protected void scrollDown() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 300);");
        TestUtils.logInfo("Scrolled down");
    }

    // Yukarı scroll
    protected void scrollUp() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, -300);");
        TestUtils.logInfo("Scrolled up");
    }

    // Element'e scroll yap
    protected void scrollToElement(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);
            TestUtils.logSuccess("Scrolled to element: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Failed to scroll to element", e);
        }
    }

    // Sayfanın en üstüne git
    protected void scrollToTop() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0);");
        TestUtils.logInfo("Scrolled to top");
    }

    // Sayfanın en altına git
    protected void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        TestUtils.logInfo("Scrolled to bottom");
    }

    // ========== WAIT & TIMING HELPERS ==========
    
    // Element kaybolana kadar bekle
    protected void waitForElementToDisappear(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        TestUtils.logSuccess("Element disappeared: " + locator);
    }

    // Belirli text görünene kadar bekle
    protected void waitForTextToBePresent(By locator, String text, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        TestUtils.logSuccess("Text appeared: " + text);
    }

    // URL değişene kadar bekle
    protected void waitForUrlToChange(String oldUrl, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(oldUrl)));
        TestUtils.logSuccess("URL changed from: " + oldUrl);
    }

    // Başlık değişene kadar bekle
    protected void waitForPageTitleToChange(String oldTitle, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.not(ExpectedConditions.titleIs(oldTitle)));
        TestUtils.logSuccess("Page title changed from: " + oldTitle);
    }

    // ========== DATA & UTILITY HELPERS ==========
    
    // Ekran görüntüsü al
    protected void takeScreenshot(String filename) {
        try {
            TestUtils.takeScreenshot(driver, filename);
            TestUtils.logSuccess("Screenshot taken: " + filename);
        } catch (Exception e) {
            TestUtils.logError("Failed to take screenshot", e);
        }
    }

    // Şu anki zaman damgasını al
    protected String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return now.format(formatter);
    }

    // Rastgele string oluştur
    protected String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // Rastgele email oluştur
    protected String generateRandomEmail() {
        return "test_" + generateRandomString(8) + "@example.com";
    }

    // Tarihi formatla
    protected String formatDate(String pattern) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return now.format(formatter);
    }

    // ========== CLEANUP HELPERS ==========
    
    // Tüm cookie'leri temizle
    protected void clearAllCookies() {
        driver.manage().deleteAllCookies();
        TestUtils.logInfo("All cookies cleared");
    }

    // Local storage'ı temizle
    protected void clearLocalStorage() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.localStorage.clear();");
        TestUtils.logInfo("Local storage cleared");
    }

    // Session storage'ı temizle
    protected void clearSessionStorage() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.sessionStorage.clear();");
        TestUtils.logInfo("Session storage cleared");
    }

    // Alert'i kabul et
    protected void acceptAlert() {
        try {
            driver.switchTo().alert().accept();
            TestUtils.logSuccess("Alert accepted");
        } catch (Exception e) {
            TestUtils.logError("Failed to accept alert", e);
        }
    }

    // Alert'i reddet
    protected void dismissAlert() {
        try {
            driver.switchTo().alert().dismiss();
            TestUtils.logSuccess("Alert dismissed");
        } catch (Exception e) {
            TestUtils.logError("Failed to dismiss alert", e);
        }
    }

    // ========== COMMON WORKFLOW HELPERS ==========
    
    // Frame'e geç
    protected void switchToFrame(By frameLocator) {
        try {
            WebElement frame = driver.findElement(frameLocator);
            driver.switchTo().frame(frame);
            TestUtils.logSuccess("Switched to frame: " + frameLocator);
        } catch (Exception e) {
            TestUtils.logError("Failed to switch to frame", e);
        }
    }

    // Ana frame'e geri dön
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        TestUtils.logInfo("Switched to default content");
    }

    // ========== TAMAMEN DİNAMİK LOGIN METODU ==========
    public void performLogin(String pageUrl, String email, String searchText, String accountIndex, String password) {
        try {
            // 1. Driver setup - Her test için yeni driver oluştur
            if (driver == null || !isDriverActive()) {
                driver = MyDriver.createNewDriver();
                TestUtils.logInfo("New driver created for login");
            }
            
            // 2. DİNAMİK sayfa URL'ine git
            driver.get(pageUrl);
            TestUtils.logSuccess("Successfully navigated to: " + pageUrl);
            
            // 3. SAYFA TAM YÜKLENENE KADAR BEKLE (KONTROL İLE)
            TestUtils.waitForPageToLoad(driver, 20);
            TestUtils.logInfo("Page load check completed");
            
                                    // 4. DİNAMİK email gir
                        TestUtils.waitForElementVisible(driver, By.id("emailInputOnLoginPage"), 15);
                        driver.findElement(By.id("emailInputOnLoginPage")).clear(); // Önce temizle
                        driver.findElement(By.id("emailInputOnLoginPage")).sendKeys(email);
                        TestUtils.logSuccess("Email entered successfully: " + email);
            
            // 5. Login butonuna bas
            TestUtils.waitForElementClickable(driver, By.id("login-button"), 15);
            driver.findElement(By.id("login-button")).click();
            TestUtils.logSuccess("Login button clicked successfully");
            
            // 6. SAYFA TAM YÜKLENENE KADAR BEKLE (Login sonrası - KONTROL İLE)
            TestUtils.waitForPageToLoad(driver, 20);
            TestUtils.logInfo("Page load check after login completed");
            
                                    // 7. DİNAMİK account search
                        TestUtils.waitForElementVisible(driver, By.cssSelector("input[placeholder='Hesap ara...']"), 15);
                        driver.findElement(By.cssSelector("input[placeholder='Hesap ara...']")).clear(); // Önce temizle
                        driver.findElement(By.cssSelector("input[placeholder='Hesap ara...']")).sendKeys(searchText);
                        TestUtils.logSuccess("Account search completed: " + searchText);
            
                                    // 8. DİNAMİK account index seç
                        List<WebElement> accountButtons = driver.findElements(By.cssSelector("button[id='accountSelectButtonOnLoginPage']"));
                        int index = Integer.parseInt(accountIndex);
                        if (index < accountButtons.size()) {
                            accountButtons.get(index).click();
                            TestUtils.logSuccess("Account selected at index: " + accountIndex);
                        } else {
                            throw new RuntimeException("Account index " + accountIndex + " not found. Available accounts: " + accountButtons.size());
                        }
            
            // 9. SAYFA TAM YÜKLENENE KADAR BEKLE (Account seçim sonrası - KONTROL İLE)
            TestUtils.waitForPageToLoad(driver, 20);
            TestUtils.logInfo("Page load check after account selection completed");
            
                                    // 10. DİNAMİK password gir
                        TestUtils.waitForElementVisible(driver, By.id("passwordInputOnLoginPage"), 15);
                        driver.findElement(By.id("passwordInputOnLoginPage")).clear(); // Önce temizle
                        driver.findElement(By.id("passwordInputOnLoginPage")).sendKeys(password);
                        TestUtils.logSuccess("Password entered successfully");
            
            // 11. Final login
            TestUtils.waitForElementClickable(driver, By.id("loginButtonInPasswordCheckInLoginPage"), 15);
            driver.findElement(By.id("loginButtonInPasswordCheckInLoginPage")).click();
            TestUtils.logSuccess("Final login button clicked successfully");
            
            // 12. SAYFA TAM YÜKLENENE KADAR BEKLE (Final login sonrası - KONTROL İLE)
            TestUtils.waitForPageToLoad(driver, 20);
            TestUtils.logInfo("Page load check after final login completed");
            
                                    // 13. Wait
                        TestUtils.waitForSeconds(5);
                        TestUtils.logSuccess("Login process completed successfully");

                        // 14. VERIFY LOGIN SUCCESSFUL
                        if (driver.getCurrentUrl().contains("dashboard")) {
                            TestUtils.logSuccess("Login verification successful - Redirected to dashboard");
                        } else {
                            throw new RuntimeException("Login failed - Not redirected to dashboard. Current URL: " + driver.getCurrentUrl());
                        }
            
        } catch (Exception e) {
            TestUtils.logError("Login failed", e);
            throw new RuntimeException("Login process failed", e);
        }
    }
    
        // REUSABLE LOGOUT METODU
    public void performLogout() {
        try {
            TestUtils.logInfo("Starting logout process...");

            // Driver'ı aktif hale getir
            driver = getActiveDriver();

            // 1. Dropdown toggle butonunu bul ve tıkla
            TestUtils.waitForElementClickable(driver, By.id("__BVID__31__BV_toggle_"), 15);
            driver.findElement(By.id("__BVID__31__BV_toggle_")).click();
            TestUtils.logSuccess("Dropdown toggle button clicked successfully");
            
            // 2. Dropdown menüden logout linkini bul ve tıkla (index 3)
            List<WebElement> dropdownItems = driver.findElements(By.cssSelector("a.dropdown-item"));
            dropdownItems.get(3).click();
            TestUtils.logSuccess("Logout link clicked successfully at index 3");
            
            // 3. "Hoş Geldin" yazısının görünür olduğunu kontrol et (aynı locator)
            TestUtils.waitForElementVisible(driver, By.xpath("//h1[contains(text(),'Hoş Geldin')]"), 15);
            TestUtils.logSuccess("Logout process completed successfully");
            
        } catch (Exception e) {
            TestUtils.logError("Logout failed", e);
            throw new RuntimeException("Logout process failed", e);
        }
    }
    
    // Driver getter
    public WebDriver getDriver() {
        return driver;
    }
    
    // Driver setter
    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }
    

}
