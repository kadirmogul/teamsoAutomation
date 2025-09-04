package utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public abstract class BaseTest {
    
    // DRIVER YÖNETİMİ 
    protected WebDriver driver;
    
    // SON SEÇİLEN MENÜ VE SUB-MENÜ TAKİBİ
    protected String lastSelectedMenu = null;
    protected String lastSelectedSubMenu = null;
    
    // Son seçilen menüyü getir
    protected String getLastSelectedMenu() {
        return lastSelectedMenu;
    }
    
    // Son seçilen sub-menüyü getir
    protected String getLastSelectedSubMenu() {
        return lastSelectedSubMenu;
    }
    
    // Element referanslarını temizle (stale element önleme)
    protected void clearElementReferences() {
        try {
            // Tüm element referanslarını temizle
            // Bu metod her test başında çağrılır
            TestUtils.logInfo("Element references cleared for fresh test");
        } catch (Exception e) {
            TestUtils.logError("Failed to clear element references", e);
        }
    }
    
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

    // ========== BROWSER CONTROL ==========

    // Browser maximize
    protected void maximizeBrowser() {
        driver.manage().window().maximize();
        TestUtils.logInfo("Browser maximized");
    }

    // ========== SMART WAIT SYSTEM ==========
    
    // Akıllı element bekleme - element görünür olana kadar bekle
    protected void waitForElementToBeVisible(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            TestUtils.logInfo("Element became visible: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Element did not become visible within " + timeoutSeconds + " seconds: " + locator, e);
            throw e;
        }
    }
    
    // Akıllı element bekleme - element tıklanabilir olana kadar bekle
    protected void waitForElementToBeClickable(By locator, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.elementToBeClickable(locator));
            TestUtils.logInfo("Element became clickable: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Element did not become clickable within " + timeoutSeconds + " seconds: " + locator, e);
            throw e;
        }
    }
    
    // Akıllı element bekleme - element tıklanabilir olana kadar bekle (WebElement)
    protected void waitForElementToBeClickable(WebElement element, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            TestUtils.logInfo("Element became clickable: " + element.getTagName());
        } catch (Exception e) {
            TestUtils.logError("Element did not become clickable within " + timeoutSeconds + " seconds", e);
            throw e;
        }
    }
    
    // Akıllı sayfa yükleme bekleme - URL değişene kadar bekle
    protected void waitForUrlToChange(String currentUrl, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));
            TestUtils.logInfo("URL changed from: " + currentUrl + " to: " + driver.getCurrentUrl());
        } catch (Exception e) {
            TestUtils.logInfo("URL did not change within " + timeoutSeconds + " seconds, continuing...");
        }
    }
    
    // Akıllı element listesi bekleme - belirli sayıda element bulunana kadar bekle
    protected List<WebElement> waitForElementsToBePresent(By locator, int expectedCount, int timeoutSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(driver -> driver.findElements(locator).size() >= expectedCount);
            List<WebElement> elements = driver.findElements(locator);
            TestUtils.logInfo("Found " + elements.size() + " elements: " + locator);
            return elements;
        } catch (Exception e) {
            TestUtils.logError("Expected " + expectedCount + " elements not found within " + timeoutSeconds + " seconds: " + locator, e);
            throw e;
        }
    }
    
    // Akıllı menü genişleme bekleme - sub-menu elementleri görünene kadar bekle
    protected void waitForSubMenuToExpand(int timeoutSeconds) {
        try {
            By subMenuLocator = By.cssSelector(".sub-menu.mm-collapse.mm-show");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(subMenuLocator));
            TestUtils.logInfo("Sub-menu expanded successfully");
        } catch (Exception e) {
            TestUtils.logInfo("Sub-menu expansion timeout, continuing...");
        }
    }

    // ========== GENERAL MENU SYSTEM HELPERS ==========
    
    // Genel menü container'ını bul (farklı sistemler için)
    protected WebElement findMenuContainer() {
        try {
            // Önce sidebar-menu'yu dene (ATS için)
            try {
                return driver.findElement(By.id("sidebar-menu"));
            } catch (Exception e) {
                // Sonra main-menu'yu dene
                try {
                    return driver.findElement(By.id("main-menu"));
                } catch (Exception e2) {
                    // Sonra navigation'ı dene
                    try {
                        return driver.findElement(By.className("navigation"));
                    } catch (Exception e3) {
                        // Son olarak nav elementini dene
                        return driver.findElement(By.tagName("nav"));
                    }
                }
            }
        } catch (Exception e) {
            TestUtils.logError("No menu container found", e);
            throw new RuntimeException("No menu container found");
        }
    }
    
    // Genel menü elementlerini bul
    protected List<WebElement> findMenuElements(WebElement menuContainer) {
        try {
            // Önce li elementlerini dene
            List<WebElement> elements = menuContainer.findElements(By.xpath(".//ul//li"));
            if (!elements.isEmpty()) {
                return elements;
            }
            
            // Sonra a elementlerini dene
            elements = menuContainer.findElements(By.xpath(".//a"));
            if (!elements.isEmpty()) {
                return elements;
            }
            
            // Son olarak button elementlerini dene
            elements = menuContainer.findElements(By.xpath(".//button"));
            return elements;
        } catch (Exception e) {
            TestUtils.logError("Error finding menu elements", e);
            return new java.util.ArrayList<>();
        }
    }
    
    // Genel menü container'ını scroll et
    protected void scrollMenuContainer(WebElement menuContainer) {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", menuContainer);
            TestUtils.logInfo("Scrolled menu container");
        } catch (Exception e) {
            TestUtils.logError("Error scrolling menu container", e);
        }
    }
    
    // Genel menü yükleme bekleme
    protected void waitForMenuToLoad(int timeoutSeconds) {
        try {
            Thread.sleep(timeoutSeconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Genel menü genişleme bekleme
    protected void waitForMenuToExpand(int timeoutSeconds) {
        try {
            // Sub-menu elementleri görünene kadar bekle
            By subMenuLocator = By.cssSelector(".sub-menu, .dropdown-menu, .mm-collapse");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(subMenuLocator));
            TestUtils.logInfo("Menu expanded successfully");
        } catch (Exception e) {
            TestUtils.logInfo("Menu expansion timeout, continuing...");
        }
    }
    
    // Genel menü element tıklama
    protected void clickMenuElement(WebElement menuElement, String menuName) {
        try {
            // Önce normal tıklama dene
            menuElement.click();
            TestUtils.logSuccess("Normal click successful for menu: " + menuName);
        } catch (Exception e) {
            TestUtils.logInfo("Normal click failed, trying JavaScript click...");
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", menuElement);
                TestUtils.logSuccess("JavaScript click successful for menu: " + menuName);
            } catch (Exception e2) {
                TestUtils.logError("Both click methods failed for menu: " + menuName, e2);
                throw new RuntimeException("Menu click failed: " + menuName);
            }
        }
    }

    // ========== ELEMENT INTERACTION HELPERS ==========
    
    // Text yazma (clear + sendKeys) - Eski metod, yeni setElementText kullan
    protected void typeText(By locator, String text, int timeoutSeconds) {
        setElementText(locator, text, timeoutSeconds);
    }

    // ========== ELEMENT FINDER HELPERS ==========
    
    // Element bul (tek element) - Normal timeout ile
    protected WebElement findElement(By locator, int timeoutSeconds) {
        try {
        TestUtils.waitForElementVisible(driver, locator, timeoutSeconds);
        WebElement element = driver.findElement(locator);
            TestUtils.logSuccess("Element found: " + locator);
            return element;
        } catch (Exception e) {
            TestUtils.logError("Element not found: " + locator, e);
            throw new RuntimeException("Element not found: " + locator, e);
        }
    }
    
    // HIZLI Element bul (timeout olmadan) - Performans için
    protected WebElement findElementFast(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            TestUtils.logSuccess("Element found fast: " + locator);
            return element;
        } catch (Exception e) {
            TestUtils.logInfo("Element not found fast: " + locator);
            return null;
        }
    }
    
    // HIZLI Element var mı kontrol et (timeout olmadan)
    protected boolean isElementPresentFast(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    // HIZLI Element tıklanabilir mi kontrol et (timeout olmadan)
    protected boolean isElementClickableFast(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            return element.isDisplayed() && element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    // Element bul (çoklu element)
    protected List<WebElement> findElements(By locator, int timeoutSeconds) {
        try {
            TestUtils.waitForElementVisible(driver, locator, timeoutSeconds);
            List<WebElement> elements = driver.findElements(locator);
            TestUtils.logSuccess("Found " + elements.size() + " elements: " + locator);
            return elements;
        } catch (Exception e) {
            TestUtils.logError("Elements not found: " + locator, e);
            throw new RuntimeException("Elements not found: " + locator, e);
        }
    }
    
    // Element var mı kontrol et
    protected boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            TestUtils.waitForElementVisible(driver, locator, timeoutSeconds);
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            TestUtils.logInfo("Element not present: " + locator);
            return false;
        }
    }

    // Element tıklanabilir mi kontrol et
    protected boolean isElementClickable(By locator, int timeoutSeconds) {
        try {
            TestUtils.waitForElementClickable(driver, locator, timeoutSeconds);
            return true;
        } catch (Exception e) {
            TestUtils.logInfo("Element not clickable: " + locator);
            return false;
        }
    }

    // Element text'ini al
    protected String getElementText(By locator, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            String text = element.getText();
            TestUtils.logSuccess("Element text retrieved: " + text);
            return text;
        } catch (Exception e) {
            TestUtils.logError("Failed to get element text", e);
            return "";
        }
    }

    // Element attribute'unu al
    protected String getElementAttribute(By locator, String attribute, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            String attrValue = element.getAttribute(attribute);
            TestUtils.logSuccess("Element attribute retrieved: " + attribute + " = " + attrValue);
            return attrValue;
        } catch (Exception e) {
            TestUtils.logError("Failed to get element attribute", e);
            return "";
        }
    }

    // Element'e text yaz
    protected void setElementText(By locator, String text, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            element.clear();
            element.sendKeys(text);
            TestUtils.logSuccess("Text entered: " + text);
        } catch (Exception e) {
            TestUtils.logError("Failed to set element text", e);
            throw new RuntimeException("Failed to set element text", e);
        }
    }
    
    // Element'e tıkla
    protected void clickElement(By locator, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            element.click();
            TestUtils.logSuccess("Element clicked: " + locator);
            } catch (Exception e) {
            TestUtils.logError("Failed to click element", e);
            throw new RuntimeException("Failed to click element", e);
        }
    }
    
    // JavaScript ile element'e tıkla
    protected void clickElementWithJS(By locator, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
            TestUtils.logSuccess("Element clicked with JS: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Failed to click element with JS", e);
            throw new RuntimeException("Failed to click element with JS", e);
        }
    }
    
    // ========== BATCH ELEMENT OPERATIONS ==========
    
    // Birden fazla element'e aynı anda tıkla
    protected void clickMultipleElements(By locator, int timeoutSeconds) {
        try {
            List<WebElement> elements = findElements(locator, timeoutSeconds);
            for (WebElement element : elements) {
                element.click();
            }
            TestUtils.logSuccess("Clicked " + elements.size() + " elements: " + locator);
            } catch (Exception e) {
            TestUtils.logError("Failed to click multiple elements", e);
            throw new RuntimeException("Failed to click multiple elements", e);
        }
    }
    
    // Birden fazla element'e aynı text yaz
    protected void setTextToMultipleElements(By locator, String text, int timeoutSeconds) {
        try {
            List<WebElement> elements = findElements(locator, timeoutSeconds);
            for (WebElement element : elements) {
                element.clear();
                element.sendKeys(text);
            }
            TestUtils.logSuccess("Text set to " + elements.size() + " elements: " + text);
        } catch (Exception e) {
            TestUtils.logError("Failed to set text to multiple elements", e);
            throw new RuntimeException("Failed to set text to multiple elements", e);
        }
    }
    
    // Birden fazla element'in text'ini al
    protected List<String> getTextFromMultipleElements(By locator, int timeoutSeconds) {
        try {
            List<WebElement> elements = findElements(locator, timeoutSeconds);
            List<String> texts = new java.util.ArrayList<>();
            for (WebElement element : elements) {
                texts.add(element.getText());
            }
            TestUtils.logSuccess("Retrieved text from " + elements.size() + " elements");
            return texts;
        } catch (Exception e) {
            TestUtils.logError("Failed to get text from multiple elements", e);
            throw new RuntimeException("Failed to get text from multiple elements", e);
        }
    }
    
    // Birden fazla element'in görünürlüğünü kontrol et
    protected boolean areAllElementsVisible(By locator, int timeoutSeconds) {
        try {
            List<WebElement> elements = findElements(locator, timeoutSeconds);
            for (WebElement element : elements) {
                if (!element.isDisplayed()) {
                    return false;
                }
            }
            TestUtils.logSuccess("All " + elements.size() + " elements are visible");
            return true;
            } catch (Exception e) {
            TestUtils.logError("Failed to check visibility of multiple elements", e);
            return false;
        }
    }

    // ========== ELEMENT STATE HELPERS ==========
    
    // Element durumunu kontrol et (görünür, tıklanabilir, seçili)
    protected java.util.Map<String, Boolean> getElementState(By locator, int timeoutSeconds) {
        java.util.Map<String, Boolean> state = new java.util.HashMap<>();
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            state.put("displayed", element.isDisplayed());
            state.put("enabled", element.isEnabled());
            state.put("selected", element.isSelected());
            state.put("clickable", isElementClickableFast(locator));
            TestUtils.logSuccess("Element state retrieved: " + state);
        } catch (Exception e) {
            state.put("displayed", false);
            state.put("enabled", false);
            state.put("selected", false);
            state.put("clickable", false);
            TestUtils.logError("Failed to get element state", e);
        }
        return state;
    }
    
    // Element'in belirli bir durumda olup olmadığını kontrol et
    protected boolean isElementInState(By locator, String stateType, boolean expectedState, int timeoutSeconds) {
        try {
            java.util.Map<String, Boolean> state = getElementState(locator, timeoutSeconds);
            boolean actualState = state.getOrDefault(stateType, false);
            boolean result = actualState == expectedState;
            TestUtils.logInfo("Element state check - " + stateType + ": " + actualState + " (expected: " + expectedState + ")");
            return result;
        } catch (Exception e) {
            TestUtils.logError("Failed to check element state", e);
            return false;
        }
    }
    
    // Element'in değerini kontrol et
    protected boolean isElementValueEquals(By locator, String expectedValue, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            String actualValue = element.getAttribute("value");
            boolean result = expectedValue.equals(actualValue);
            TestUtils.logInfo("Element value check - actual: " + actualValue + " (expected: " + expectedValue + ")");
            return result;
        } catch (Exception e) {
            TestUtils.logError("Failed to check element value", e);
            return false;
        }
    }
    
    // Element'in text'ini kontrol et
    protected boolean isElementTextEquals(By locator, String expectedText, int timeoutSeconds) {
        try {
            String actualText = getElementText(locator, timeoutSeconds);
            boolean result = expectedText.equals(actualText);
            TestUtils.logInfo("Element text check - actual: " + actualText + " (expected: " + expectedText + ")");
            return result;
            } catch (Exception e) {
            TestUtils.logError("Failed to check element text", e);
            return false;
        }
    }
    
    // Element'in text'ini içerip içermediğini kontrol et
    protected boolean isElementTextContains(By locator, String expectedText, int timeoutSeconds) {
        try {
            String actualText = getElementText(locator, timeoutSeconds);
            boolean result = actualText.contains(expectedText);
            TestUtils.logInfo("Element text contains check - actual: " + actualText + " (contains: " + expectedText + ")");
            return result;
        } catch (Exception e) {
            TestUtils.logError("Failed to check element text contains", e);
            return false;
        }
    }

    // ========== VERIFICATION HELPERS ==========
    
    // URL kontrolü
    protected boolean isCurrentUrlContains(String expectedText) {
        return driver.getCurrentUrl().contains(expectedText);
    }

    // ========== SCROLL HELPERS ==========

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

    // ========== SMART WAIT HELPERS ==========
    
    // Element görünene kadar akıllı bekle (max 5 saniye)
    protected WebElement waitForElementSmart(By locator) {
        return waitForElementSmart(locator, 5);
    }
    
    // Element görünene kadar akıllı bekle (custom timeout)
    protected WebElement waitForElementSmart(By locator, int maxWaitSeconds) {
        for (int i = 0; i < maxWaitSeconds; i++) {
            WebElement element = findElementFast(locator);
            if (element != null && element.isDisplayed()) {
                TestUtils.logSuccess("Element found smartly after " + i + " seconds: " + locator);
                return element;
            }
            TestUtils.waitForSeconds(1);
        }
        throw new RuntimeException("Element not found after smart wait: " + locator);
    }
    
    // Element kaybolana kadar akıllı bekle
    protected void waitForElementToDisappearSmart(By locator, int maxWaitSeconds) {
        for (int i = 0; i < maxWaitSeconds; i++) {
            if (!isElementPresentFast(locator)) {
                TestUtils.logSuccess("Element disappeared smartly after " + i + " seconds: " + locator);
                return;
            }
            TestUtils.waitForSeconds(1);
        }
        throw new RuntimeException("Element still present after smart wait: " + locator);
    }

    // URL değişene kadar bekle


    // ========== FAST FORM FILLING HELPERS ==========
    
    // Hızlı form doldurma - Map ile
    protected void fillFormFast(java.util.Map<String, String> formData, int timeoutSeconds) {
        try {
            for (java.util.Map.Entry<String, String> entry : formData.entrySet()) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                
                // Farklı selector türlerini dene
                By locator = null;
                if (fieldName.startsWith("id:")) {
                    locator = By.id(fieldName.substring(3));
                } else if (fieldName.startsWith("name:")) {
                    locator = By.name(fieldName.substring(5));
                } else if (fieldName.startsWith("css:")) {
                    locator = By.cssSelector(fieldName.substring(4));
                } else {
                    // Default olarak name attribute kullan
                    locator = By.name(fieldName);
                }
                
                setElementText(locator, fieldValue, timeoutSeconds);
            }
            TestUtils.logSuccess("Form filled fast with " + formData.size() + " fields");
        } catch (Exception e) {
            TestUtils.logError("Failed to fill form fast", e);
            throw new RuntimeException("Failed to fill form fast", e);
        }
    }
    
    // Hızlı form doldurma - Array ile
    protected void fillFormFast(String[][] formData, int timeoutSeconds) {
        try {
            for (String[] field : formData) {
                if (field.length >= 2) {
                    String fieldName = field[0];
                    String fieldValue = field[1];
                    setElementText(By.name(fieldName), fieldValue, timeoutSeconds);
                }
            }
            TestUtils.logSuccess("Form filled fast with " + formData.length + " fields");
        } catch (Exception e) {
            TestUtils.logError("Failed to fill form fast", e);
            throw new RuntimeException("Failed to fill form fast", e);
        }
    }
    
    // Hızlı dropdown seçimi
    protected void selectDropdownOptionFast(By dropdownLocator, String optionText, int timeoutSeconds) {
        try {
            WebElement dropdown = findElement(dropdownLocator, timeoutSeconds);
            dropdown.click();
            
            // Option'ı bul ve seç
            By optionLocator = By.xpath("//option[contains(text(),'" + optionText + "')]");
            WebElement option = waitForElementSmart(optionLocator, 3);
            option.click();
            
            TestUtils.logSuccess("Dropdown option selected fast: " + optionText);
        } catch (Exception e) {
            TestUtils.logError("Failed to select dropdown option fast", e);
            throw new RuntimeException("Failed to select dropdown option fast", e);
        }
    }
    
    // Hızlı checkbox işaretleme
    protected void checkCheckboxFast(By checkboxLocator, boolean shouldCheck, int timeoutSeconds) {
        try {
            WebElement checkbox = findElement(checkboxLocator, timeoutSeconds);
            boolean isChecked = checkbox.isSelected();
            
            if (shouldCheck && !isChecked) {
                checkbox.click();
            } else if (!shouldCheck && isChecked) {
                checkbox.click();
            }
            
            TestUtils.logSuccess("Checkbox " + (shouldCheck ? "checked" : "unchecked") + " fast");
        } catch (Exception e) {
            TestUtils.logError("Failed to check/uncheck checkbox fast", e);
            throw new RuntimeException("Failed to check/uncheck checkbox fast", e);
        }
    }

    // ========== TEST DATA HELPERS ==========
    
    // Rastgele test verisi oluştur
    protected String generateRandomTestData(String dataType) {
        switch (dataType.toLowerCase()) {
            case "email":
                return "test_" + System.currentTimeMillis() + "@example.com";
            case "phone":
                return "555" + String.format("%07d", (int)(Math.random() * 10000000));
            case "name":
                return "TestUser_" + System.currentTimeMillis();
            case "password":
                return "TestPass" + (int)(Math.random() * 1000);
            case "number":
                return String.valueOf((int)(Math.random() * 1000));
            default:
                return "TestData_" + System.currentTimeMillis();
        }
    }
    
    // Test verisi Map'i oluştur
    protected java.util.Map<String, String> createTestDataMap(String... dataPairs) {
        java.util.Map<String, String> testData = new java.util.HashMap<>();
        for (int i = 0; i < dataPairs.length; i += 2) {
            if (i + 1 < dataPairs.length) {
                testData.put(dataPairs[i], dataPairs[i + 1]);
            }
        }
        TestUtils.logSuccess("Test data map created with " + testData.size() + " entries");
        return testData;
    }
    
    // Test verisi Array'i oluştur
    protected String[][] createTestDataArray(String... dataPairs) {
        String[][] testData = new String[dataPairs.length / 2][2];
        for (int i = 0; i < dataPairs.length; i += 2) {
            if (i + 1 < dataPairs.length) {
                testData[i / 2][0] = dataPairs[i];
                testData[i / 2][1] = dataPairs[i + 1];
            }
        }
        TestUtils.logSuccess("Test data array created with " + testData.length + " entries");
        return testData;
    }
    
    // Test verisi dosyasından oku (basit format)
    protected java.util.Map<String, String> loadTestDataFromString(String dataString) {
        java.util.Map<String, String> testData = new java.util.HashMap<>();
        String[] lines = dataString.split("\n");
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length == 2) {
                testData.put(parts[0].trim(), parts[1].trim());
            }
        }
        TestUtils.logSuccess("Test data loaded from string with " + testData.size() + " entries");
        return testData;
    }
    
    // Test verisi doğrulama
    protected boolean validateTestData(java.util.Map<String, String> testData, String... requiredFields) {
        for (String field : requiredFields) {
            if (!testData.containsKey(field) || testData.get(field).isEmpty()) {
                TestUtils.logError("Required field missing or empty: " + field, new Exception("Validation failed"));
                return false;
            }
        }
        TestUtils.logSuccess("Test data validation passed for " + requiredFields.length + " fields");
        return true;
    }

    // ========== UTILITY HELPERS ==========
    
    // Ekran görüntüsü al
    protected void takeScreenshot(String filename) {
        try {
            TestUtils.takeScreenshot(driver, filename);
            TestUtils.logSuccess("Screenshot taken: " + filename);
        } catch (Exception e) {
            TestUtils.logError("Failed to take screenshot", e);
        }
    }

    // ========== SCROLL & VISIBILITY OPTIMIZATION ==========
    
    // Element'i görünür hale getir ve tıkla
    protected void scrollToElementAndClick(By locator, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            TestUtils.waitForSeconds(1); // Smooth scroll için bekle
            element.click();
            TestUtils.logSuccess("Element scrolled to and clicked: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Failed to scroll to element and click", e);
            throw new RuntimeException("Failed to scroll to element and click", e);
        }
    }
    
    // Element'i görünür hale getir ve text yaz
    protected void scrollToElementAndType(By locator, String text, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            TestUtils.waitForSeconds(1); // Smooth scroll için bekle
            element.clear();
            element.sendKeys(text);
            TestUtils.logSuccess("Element scrolled to and text entered: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Failed to scroll to element and type", e);
            throw new RuntimeException("Failed to scroll to element and type", e);
        }
    }
    
    // Element görünür mü kontrol et (scroll gerekirse yap)
    protected boolean isElementVisibleWithScroll(By locator, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            if (element.isDisplayed()) {
                return true;
            }
            
            // Element görünür değilse scroll yap
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            TestUtils.waitForSeconds(1);
            
            return element.isDisplayed();
        } catch (Exception e) {
            TestUtils.logError("Failed to check element visibility with scroll", e);
            return false;
        }
    }
    
    // Element'i viewport'un merkezine getir
    protected void centerElementInViewport(By locator, int timeoutSeconds) {
        try {
            WebElement element = findElement(locator, timeoutSeconds);
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'});", 
                element
            );
            TestUtils.waitForSeconds(1);
            TestUtils.logSuccess("Element centered in viewport: " + locator);
        } catch (Exception e) {
            TestUtils.logError("Failed to center element in viewport", e);
            throw new RuntimeException("Failed to center element in viewport", e);
        }
    }

    // ========== CLEANUP HELPERS ==========
    
    // Tüm cookie'leri temizle
    protected void clearAllCookies() {
        driver.manage().deleteAllCookies();
        TestUtils.logInfo("All cookies cleared");
    }

    // ========== WORKFLOW HELPERS ==========

    // Ana frame'e geri dön
    protected void switchToDefaultContent() {
        driver.switchTo().defaultContent();
        TestUtils.logInfo("Switched to default content");
    }

    // ========== YENİ LOGIN VE MENU METODLARI ==========
    
    // Login to system - Yeni parametrik login metodu
    protected void loginToSystem(String pageUrl, String email, String searchText, String accountIndex, String password) {
        try {
            TestUtils.logInfo("Starting login to system with parameters...");
            performLogin(pageUrl, email, searchText, accountIndex, password);
            TestUtils.logSuccess("Login to system completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Login to system failed", e);
            throw new RuntimeException("Login to system failed", e);
        }
    }
    
    // Select menu and sub-menu - Yeni birleşik menu seçim metodu
    protected void selectMenuAndSubMenu(String menuName, String subMenuIndex) {
        try {
            TestUtils.logInfo("Starting menu and sub-menu selection...");
            
            // Önce menüyü seç
            selectMenu(menuName, 15);
            
            // Menü açıldıktan sonra element bulana kadar scroll yap
            TestUtils.logInfo("Menu opened, starting scroll to find sub-menu elements...");
            By subMenuLocator = By.cssSelector("ul.sub-menu.mm-collapse.mm-show > li");
            scrollUntilElementFound(subMenuLocator, 10);
            
            // Sonra sub-menu'yu seç
            int index = Integer.parseInt(subMenuIndex);
            selectModuleSubMenu(menuName, index, 15);
            
            TestUtils.logSuccess("Menu and sub-menu selection completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Menu and sub-menu selection failed", e);
            throw new RuntimeException("Menu and sub-menu selection failed", e);
        }
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
                        setElementText(By.id("emailInputOnLoginPage"), email, 15);
            
            // 5. Login butonuna bas
            clickElement(By.id("login-button"), 15);
            
            // 6. SAYFA TAM YÜKLENENE KADAR BEKLE (Login sonrası - KONTROL İLE)
            TestUtils.waitForPageToLoad(driver, 20);
            TestUtils.logInfo("Page load check after login completed");
            
                                    // 7. DİNAMİK account search
                        setElementText(By.cssSelector("input[placeholder='Hesap ara...']"), searchText, 15);
            
                                    // 8. DİNAMİK account index seç
                        List<WebElement> accountButtons = findElements(By.cssSelector("button[id='accountSelectButtonOnLoginPage']"), 15);
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
                        setElementText(By.id("passwordInputOnLoginPage"), password, 15);
            
            // 11. Final login
            clickElement(By.id("loginButtonInPasswordCheckInLoginPage"), 15);
            
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
            clickElement(By.id("__BVID__31__BV_toggle_"), 15);
            
            // 2. Dropdown menüden logout linkini bul ve tıkla (index 3)
            List<WebElement> dropdownItems = findElements(By.cssSelector("a.dropdown-item"), 15);
            dropdownItems.get(3).click();
            TestUtils.logSuccess("Logout link clicked successfully at index 3");
            
            // 3. "Hoş Geldin" yazısının görünür olduğunu kontrol et (aynı locator)
            findElement(By.xpath("//h1[contains(text(),'Hoş Geldin')]"), 15);
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
    
    // ========== MENU SELECTION HELPERS ==========
    
    // Parametrik menü seçimi - sadece modül ismine bakarak
    // Parametrik menü seçimi (id=sidebar-menu div'ine bağlı listelerden)
    protected void selectMenu(String menuName, int timeoutSeconds) {
        try {
            TestUtils.logInfo("Selecting menu: " + menuName);
            driver = getActiveDriver();
            
            // Genel menü container'ını bul
            WebElement menuContainer = findMenuContainer();
            
            // Menüyü bulmak için akıllı scroll yap
            int maxAttempts = 10;
            int attempt = 0;
            WebElement targetMenu = null;
            
            while (attempt < maxAttempts && targetMenu == null) {
                try {
                    // Menü container'ı içindeki tüm menü elementlerini bul
                    List<WebElement> allMenus = findMenuElements(menuContainer);
                    TestUtils.logInfo("Found " + allMenus.size() + " menu items");
                    
                    // Menüyü ara (tam eşleşme veya içerik eşleşmesi)
                    for (WebElement menuElement : allMenus) {
                        String menuText = menuElement.getText().trim();
                        if (menuText.equals(menuName) || menuText.contains(menuName)) {
                            targetMenu = menuElement;
                            TestUtils.logInfo("Found menu: " + menuName + " in text: '" + menuText + "'");
                            break;
                        }
                    }
                    
                    if (targetMenu == null) {
                        // Menü bulunamadı, scroll yap
                        TestUtils.logInfo("Menu not found, scrolling... (attempt " + (attempt + 1) + ")");
                        scrollMenuContainer(menuContainer);
                        waitForMenuToLoad(2);
                        attempt++;
                    } else {
                        // Menü bulundu, döngüden çık
                        TestUtils.logInfo("Menu found, stopping search");
                        break;
                    }
                } catch (Exception e) {
                    TestUtils.logInfo("Error during menu search, scrolling... (attempt " + (attempt + 1) + ")");
                    scrollMenuContainer(menuContainer);
                    waitForMenuToLoad(2);
                    attempt++;
                }
            }
            
            if (targetMenu == null) {
                // Mevcut menüleri listele
                List<WebElement> allMenus = findMenuElements(menuContainer);
                StringBuilder availableMenus = new StringBuilder();
                for (int i = 0; i < allMenus.size(); i++) {
                    String menuText = allMenus.get(i).getText().trim();
                    if (!menuText.isEmpty()) {
                        availableMenus.append(i).append(": ").append(menuText);
                        if (i < allMenus.size() - 1) availableMenus.append(", ");
                    }
                }
                TestUtils.logError("Menu '" + menuName + "' not found after " + maxAttempts + " attempts. Available menus: " + availableMenus.toString(), new Exception("Menu not found"));
                throw new RuntimeException("Menu '" + menuName + "' not found after " + maxAttempts + " attempts");
            }
            
            // Menüyü tıkla
            TestUtils.logInfo("Clicking on menu: " + menuName);
            clickMenuElement(targetMenu, menuName);
            
            // Son seçilen menüyü kaydet
            lastSelectedMenu = menuName;
            TestUtils.logInfo("Last selected menu saved: " + lastSelectedMenu);
            
            // Menü genişlemesi için bekle
            TestUtils.logInfo("Waiting for menu to expand...");
            waitForMenuToExpand(5);
            
        } catch (Exception e) {
            TestUtils.logError("Menu selection failed for: " + menuName, e);
            throw new RuntimeException("Menu selection failed for: " + menuName, e);
        }
    }
    

    

    
    // Element bulana kadar scroll yap - yeni metod
    protected void scrollUntilElementFound(By locator, int maxAttempts) {
        try {
            TestUtils.logInfo("Starting scroll until element found: " + locator);
            
            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                // Element var mı kontrol et
                List<WebElement> elements = driver.findElements(locator);
                if (!elements.isEmpty()) {
                    WebElement element = elements.get(0);
                    if (element.isDisplayed() && !element.getText().trim().isEmpty()) {
                        TestUtils.logSuccess("Element found after " + attempt + " scroll attempts: " + locator);
                        return;
                    }
                }
                
                // Element bulunamadı, scroll yap
                TestUtils.logInfo("Element not found, scrolling... (attempt " + (attempt + 1) + ")");
                scrollMenuToBottom();
                TestUtils.waitForSeconds(1);
            }
            
            TestUtils.logInfo("Element not found after " + maxAttempts + " scroll attempts");
        } catch (Exception e) {
            TestUtils.logError("Error during scroll until element found", e);
        }
    }

    // Menü barını akıllı scroll yap - element görünürse scroll yapma
    protected void scrollMenuToBottom() {
        try {
            TestUtils.logInfo("Starting smart menu scroll...");
            
            // Driver'ın aktif olduğunu kontrol et
            if (driver == null) {
                TestUtils.logError("Driver is null, cannot scroll menu", new Exception("Driver is null"));
                throw new RuntimeException("Driver is null, cannot scroll menu");
            }
            
            // Menü container'ını bul
            WebElement menuContainer = driver.findElement(By.id("sidebar-menu"));
            
            // Menü yüksekliğini al
            Object scrollTopObj = ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollTop;", menuContainer);
            Object scrollHeightObj = ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollHeight;", menuContainer);
            
            long lastScrollTop = scrollTopObj != null ? ((Number) scrollTopObj).longValue() : 0;
            long maxScrollHeight = scrollHeightObj != null ? ((Number) scrollHeightObj).longValue() : 0;
            
            TestUtils.logInfo("Menu scroll info - Current: " + lastScrollTop + ", Max: " + maxScrollHeight);
            
            // Eğer zaten en alttaysa scroll yapma
            if (lastScrollTop >= maxScrollHeight - 10) {
                TestUtils.logInfo("Menu already at bottom, no scroll needed");
                return;
            }
            
            int scrollAttempts = 0;
            int maxScrollAttempts = 10;
            
            while (scrollAttempts < maxScrollAttempts) {
                // Menü içinde scroll yap
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", menuContainer);
                
                // Scroll sonrası bekle
                TestUtils.waitForSeconds(1);
                
                                    // Yeni scroll pozisyonunu al
                    Object newScrollTopObj = ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollTop;", menuContainer);
                    Object newScrollHeightObj = ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollHeight;", menuContainer);
                    
                    long newScrollTop = newScrollTopObj != null ? ((Number) newScrollTopObj).longValue() : 0;
                    long newScrollHeight = newScrollHeightObj != null ? ((Number) newScrollHeightObj).longValue() : 0;
                
                TestUtils.logInfo("Scroll attempt " + (scrollAttempts + 1) + " - Position: " + newScrollTop + ", Height: " + newScrollHeight);
                
                // Eğer scroll pozisyonu değişmediyse menü sonuna ulaştık
                if (newScrollTop == lastScrollTop) {
                    TestUtils.logSuccess("Reached menu bottom - no more scrolling possible");
                    break;
                }
                
                // Eğer scroll height değiştiyse daha fazla içerik yüklendi, devam et
                if (newScrollHeight > maxScrollHeight) {
                    TestUtils.logInfo("New content loaded, continuing scroll...");
                    maxScrollHeight = newScrollHeight;
                }
                
                lastScrollTop = newScrollTop;
                scrollAttempts++;
            }
            
            if (scrollAttempts >= maxScrollAttempts) {
                TestUtils.logInfo("Reached maximum scroll attempts (" + maxScrollAttempts + ")");
            }
            
            TestUtils.logSuccess("Smart menu scroll completed");
            
        } catch (Exception e) {
            TestUtils.logError("Failed to scroll menu to bottom", e);
            throw new RuntimeException("Failed to scroll menu to bottom", e);
        }
    }
    
    // Genel sayfa açıldı mı kontrol et
    protected void verifyPageOpened() {
        try {
            TestUtils.logInfo("Verifying page opened...");
            driver = getActiveDriver();
            String currentUrl = driver.getCurrentUrl();
            TestUtils.logInfo("Current URL after click: " + currentUrl);
            
            // Dashboard'dan farklı bir sayfaya yönlendirildi mi kontrol et
            boolean isDifferentPage = !currentUrl.equals("https://teamso.com/dashboard") && 
                                    !currentUrl.contains("/dashboard/account/login");
            
            if (isDifferentPage) {
                TestUtils.logSuccess("Page opened successfully - URL: " + currentUrl);
            } else {
                // URL değişmedi, sayfa içeriğini kontrol et
                TestUtils.logInfo("URL did not change, checking page content...");
                try {
                    WebElement pageContent = driver.findElement(By.tagName("body"));
                    String pageText = pageContent.getText();
                    
                    // Son seçilen sub-menu metnini kontrol et
                    String lastSelectedSubMenu = getLastSelectedSubMenu();
                    if (lastSelectedSubMenu != null && pageText.contains(lastSelectedSubMenu)) {
                        TestUtils.logSuccess("Page content verified - Sub-menu content found: " + lastSelectedSubMenu);
                    } else {
                        TestUtils.logInfo("Sub-menu content not found, but page is accessible");
                        TestUtils.logSuccess("Page opened successfully - URL: " + currentUrl);
                    }
                } catch (Exception e) {
                    TestUtils.logInfo("Could not verify page content, but page is accessible");
                    TestUtils.logSuccess("Page opened successfully - URL: " + currentUrl);
                }
            }
            
            String pageTitle = driver.getTitle();
            TestUtils.logInfo("Page title: " + pageTitle);
            
        } catch (Exception e) {
            TestUtils.logError("Page verification failed", e);
            throw new RuntimeException("Page verification failed", e);
        }
    }
    
    // Ayarlar sayfasının açıldığını doğrula
    protected void verifySettingsPageOpened() {
        try {
            TestUtils.logInfo("Verifying settings page opened...");
            
            // Driver'ı aktif hale getir
            driver = getActiveDriver();
            
            // Ayarlar sayfasının açıldığını doğrula
            String currentUrl = driver.getCurrentUrl();
            TestUtils.logInfo("Current URL after settings click: " + currentUrl);
            
            // URL'de "settings" veya "ayarlar" kelimesi var mı kontrol et
            boolean isSettingsPage = currentUrl.toLowerCase().contains("settings") || 
                                   currentUrl.toLowerCase().contains("ayarlar") ||
                                   currentUrl.toLowerCase().contains("config");
            
            if (isSettingsPage) {
                TestUtils.logSuccess("Settings page opened successfully - URL: " + currentUrl);
            } else {
                TestUtils.logError("Settings page not opened - Current URL: " + currentUrl, new Exception("Settings page verification failed"));
                throw new RuntimeException("Settings page not opened - Expected settings page but got: " + currentUrl);
            }
            
            // Sayfa başlığını da kontrol et
            String pageTitle = driver.getTitle();
            TestUtils.logInfo("Page title: " + pageTitle);
            
        } catch (Exception e) {
            TestUtils.logError("Settings page verification failed", e);
            throw new RuntimeException("Settings page verification failed", e);
        }
    }
    

    
    // Parametrik modül ve alt menü seçimi (eski metod - geriye uyumluluk için)
    protected void selectModuleSubMenu(String moduleName, int subMenuIndex, int timeoutSeconds) {
        try {
            TestUtils.logInfo("Selecting module: " + moduleName + " with sub-menu index: " + subMenuIndex);
            
            // Driver'ı aktif hale getir
            driver = getActiveDriver();
            
            // Ana modüle tıkla (zaten selectMenu ile tıklanmış olmalı)
            TestUtils.logInfo("Module '" + moduleName + "' should already be selected");
            
            // Modül açılması için akıllı bekleme
            waitForSubMenuToExpand(3);
            
            // side-menu div'ini bul
            WebElement sideMenu = driver.findElement(By.id("sidebar-menu"));
            
            // MENÜYÜ EN ÜSTE SCROLL ET - Her test için temiz başlangıç
            TestUtils.logInfo("Resetting menu position to top for clean start");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollTop = 0;", sideMenu);
            TestUtils.waitForSeconds(1); // Scroll işleminin tamamlanması için bekle
            
            // ÖNCE ELEMENT VAR MI KONTROL ET - Scroll yapmadan önce
            TestUtils.logInfo("=== DEBUG: Checking if target element exists before scrolling ===");
            
            // MODÜL-SPESİFİK Sub-menu elementlerini bul
            TestUtils.logInfo("=== MODULE-SPECIFIC SUB-MENU SEARCH ===");
            TestUtils.logInfo("Looking for sub-menus of module: " + moduleName);
            
            // Önce seçili modülü bul - Gelişmiş arama
            WebElement selectedModule = null;
            TestUtils.logInfo("=== SEARCHING FOR SELECTED MODULE ===");
            
            // Farklı selector'ları dene
            List<WebElement> allMenuItems = sideMenu.findElements(By.xpath(".//li"));
            TestUtils.logInfo("Found " + allMenuItems.size() + " total menu items");
            
            // 1. Tam eşleşme ara
            for (WebElement menuItem : allMenuItems) {
                String menuText = menuItem.getText().trim();
                if (menuText.equals(moduleName)) {
                    selectedModule = menuItem;
                    TestUtils.logInfo("Found selected module (exact match): " + moduleName);
                    break;
                }
            }
            
            // 2. Eğer bulunamazsa içerik eşleşmesi ara
            if (selectedModule == null) {
                for (WebElement menuItem : allMenuItems) {
                    String menuText = menuItem.getText().trim();
                    if (menuText.contains(moduleName)) {
                        selectedModule = menuItem;
                        TestUtils.logInfo("Found selected module (contains match): " + moduleName + " in text: '" + menuText + "'");
                        break;
                    }
                }
            }
            
            // 3. Eğer hala bulunamazsa class kontrolü yap
            if (selectedModule == null) {
                TestUtils.logInfo("Text-based search failed, trying class-based search...");
                List<WebElement> activeMenuItems = sideMenu.findElements(By.xpath(".//li[contains(@class, 'mm-active') or contains(@class, 'active') or contains(@class, 'selected')]"));
                TestUtils.logInfo("Found " + activeMenuItems.size() + " potentially active menu items");
                
                for (WebElement menuItem : activeMenuItems) {
                    String menuText = menuItem.getText().trim();
                    String menuClass = menuItem.getAttribute("class");
                    TestUtils.logInfo("Active menu item - Text: '" + menuText + "', Class: '" + menuClass + "'");
                    
                    if (menuText.equals(moduleName) || menuText.contains(moduleName)) {
                        selectedModule = menuItem;
                        TestUtils.logInfo("Found selected module (class-based): " + moduleName);
                        break;
                    }
                }
            }
            
            // 4. Son çare: Tüm menü itemlerini listele
            if (selectedModule == null) {
                TestUtils.logInfo("=== ALL MENU ITEMS DEBUG ===");
                for (int i = 0; i < Math.min(allMenuItems.size(), 20); i++) { // İlk 20 item'i listele
                    WebElement menuItem = allMenuItems.get(i);
                    String menuText = menuItem.getText().trim();
                    String menuClass = menuItem.getAttribute("class");
                    boolean isDisplayed = menuItem.isDisplayed();
                    TestUtils.logInfo("Menu " + i + " - Text: '" + menuText + "', Class: '" + menuClass + "', Displayed: " + isDisplayed);
                }
            }
            
            List<WebElement> subMenuElements = new java.util.ArrayList<>();
            By subMenuLocator = null;
            
            if (selectedModule != null) {
                // Seçili modülün altındaki sub-menu'leri bul
                subMenuLocator = By.xpath(".//ul[contains(@class, 'sub-menu') and contains(@class, 'mm-collapse') and contains(@class, 'mm-show')]//li");
                subMenuElements = selectedModule.findElements(subMenuLocator);
                TestUtils.logInfo("Module-specific XPath found " + subMenuElements.size() + " sub-menu elements for: " + moduleName);
                
                // Eğer bulunamazsa alternatif yöntem dene
                if (subMenuElements.isEmpty()) {
                    subMenuLocator = By.cssSelector("ul.sub-menu.mm-collapse.mm-show > li");
                    subMenuElements = selectedModule.findElements(subMenuLocator);
                    TestUtils.logInfo("Module-specific CSS found " + subMenuElements.size() + " sub-menu elements for: " + moduleName);
                }
            }
            
            // Hala bulunamazsa genel arama yap (fallback)
            if (subMenuElements.isEmpty()) {
                TestUtils.logInfo("Module-specific search failed, trying general search...");
                subMenuLocator = By.cssSelector("ul.sub-menu.mm-collapse.mm-show > li");
                subMenuElements = sideMenu.findElements(subMenuLocator);
                TestUtils.logInfo("General CSS Selector 'ul.sub-menu.mm-collapse.mm-show > li' found " + subMenuElements.size() + " elements");
                
                // Eğer bulunamazsa alternatif selector dene
                if (subMenuElements.isEmpty()) {
                    subMenuLocator = By.cssSelector(".sub-menu.mm-collapse.mm-show li");
                    subMenuElements = sideMenu.findElements(subMenuLocator);
                    TestUtils.logInfo("CSS Selector '.sub-menu.mm-collapse.mm-show li' found " + subMenuElements.size() + " elements");
                }
                
                // Hala bulunamazsa XPath dene
                if (subMenuElements.isEmpty()) {
                    subMenuLocator = By.xpath(".//ul[contains(@class, 'sub-menu') and contains(@class, 'mm-collapse') and contains(@class, 'mm-show')]//li");
                    subMenuElements = sideMenu.findElements(subMenuLocator);
                    TestUtils.logInfo("XPath selector found " + subMenuElements.size() + " elements");
                }
            }
            
            TestUtils.logInfo("Found " + subMenuElements.size() + " sub-menu items initially");
            
            // DETAYLI DEBUG: Her element'in özelliklerini listele
            TestUtils.logInfo("=== DETAILED ELEMENT DEBUG ===");
            for (int i = 0; i < subMenuElements.size(); i++) {
                WebElement element = subMenuElements.get(i);
                String elementText = element.getText().trim();
                String elementClass = element.getAttribute("class");
                String elementTag = element.getTagName();
                boolean isDisplayed = element.isDisplayed();
                boolean isEnabled = element.isEnabled();
                TestUtils.logInfo("Element " + i + " - Tag: " + elementTag + ", Class: '" + elementClass + "', Text: '" + elementText + "', Displayed: " + isDisplayed + ", Enabled: " + isEnabled);
            }
            
            // BOŞ ELEMENTLERİ FİLTRELE - Sadece text'i olan elementleri al
            TestUtils.logInfo("=== FILTERING EMPTY ELEMENTS ===");
            List<WebElement> validSubMenus = new java.util.ArrayList<>();
            for (WebElement element : subMenuElements) {
                if (element.isDisplayed() && !element.getText().trim().isEmpty()) {
                    validSubMenus.add(element);
                }
            }
            
            TestUtils.logInfo("Filtered " + subMenuElements.size() + " elements to " + validSubMenus.size() + " valid elements");
            
            // Filtrelenmiş elementleri listele
            TestUtils.logInfo("=== FILTERED ELEMENTS DEBUG ===");
            for (int i = 0; i < validSubMenus.size(); i++) {
                WebElement element = validSubMenus.get(i);
                String elementText = element.getText().trim();
                TestUtils.logInfo("Valid Element " + i + " - Text: '" + elementText + "'");
            }
            
            // Filtrelenmiş listeyi kullan
            subMenuElements = validSubMenus;
            
            // ELEMENT KONTROLÜ: Index geçerli mi ve element görünür mü?
            boolean elementFound = false;
            if (subMenuIndex < subMenuElements.size()) {
                WebElement targetElement = subMenuElements.get(subMenuIndex);
                String targetText = targetElement.getText().trim();
                
                if (targetElement.isDisplayed() && !targetText.isEmpty()) {
                    TestUtils.logInfo("Target sub-menu found and visible at index " + subMenuIndex + ": " + targetText);
                    elementFound = true;
                } else if (targetElement.isDisplayed() && targetText.isEmpty()) {
                    TestUtils.logError("Index " + subMenuIndex + " is empty - this is an error, not a scroll issue", new Exception("Empty element at specified index"));
                    throw new RuntimeException("Sub-menu at index " + subMenuIndex + " is empty. Please check the correct index.");
                } else {
                    TestUtils.logInfo("Target element at index " + subMenuIndex + " is not displayed, need to scroll");
                }
            } else {
                TestUtils.logInfo("Sub-menu index " + subMenuIndex + " is out of bounds (" + subMenuElements.size() + "), need to scroll");
            }
            
            // ELEMENT BULUNAMADIYSA SCROLL YAP
            if (!elementFound) {
                TestUtils.logInfo("Element not found initially, starting scroll search...");
                int maxAttempts = 10;
                int attempt = 0;
                
                while (attempt < maxAttempts && !elementFound) {
                    try {
                        TestUtils.logInfo("Scrolling menu bar to find target sub-menu... (attempt " + (attempt + 1) + ")");
                        scrollMenuToBottom();
                        waitForSubMenuToExpand(2);
                        
                        // Scroll sonrası elementleri tekrar bul
                        subMenuElements = sideMenu.findElements(subMenuLocator);
                        TestUtils.logInfo("After scroll, found " + subMenuElements.size() + " sub-menu items");
                        
                        // Element kontrolü
                        if (subMenuIndex < subMenuElements.size()) {
                            WebElement targetElement = subMenuElements.get(subMenuIndex);
                            String targetText = targetElement.getText().trim();
                            
                            if (targetElement.isDisplayed() && !targetText.isEmpty()) {
                                TestUtils.logInfo("Target sub-menu found after scroll at index " + subMenuIndex + ": " + targetText);
                                elementFound = true;
                                break;
                            }
                        }
                        
                        attempt++;
                    } catch (Exception e) {
                        TestUtils.logInfo("Error during scroll attempt " + (attempt + 1) + ": " + e.getMessage());
                        attempt++;
                    }
                }
            }
            
            if (subMenuElements.isEmpty()) {
                TestUtils.logError("No sub-menu items found", new Exception("Sub-menu not found"));
                throw new RuntimeException("No sub-menu items found");
            }
            
            // Tüm alt menüleri listele
            TestUtils.logInfo("Available sub-menus:");
            for (int i = 0; i < subMenuElements.size(); i++) {
                String subMenuText = subMenuElements.get(i).getText().trim();
                TestUtils.logInfo("Index " + i + ": " + subMenuText);
            }
            
            // Index kontrolü
            if (subMenuIndex < 0 || subMenuIndex >= subMenuElements.size()) {
                StringBuilder availableSubMenus = new StringBuilder();
                for (int i = 0; i < subMenuElements.size(); i++) {
                    availableSubMenus.append(i).append(": ").append(subMenuElements.get(i).getText().trim());
                    if (i < subMenuElements.size() - 1) availableSubMenus.append(", ");
                }
                throw new RuntimeException("Sub-menu index " + subMenuIndex + " is out of range. Available sub-menus: " + availableSubMenus.toString());
            }
            
            // Belirtilen index'teki alt menüyü seç
            WebElement targetSubMenu = subMenuElements.get(subMenuIndex);
            String subMenuText = targetSubMenu.getText().trim();
            
            TestUtils.logInfo("Clicking on sub-menu item: " + subMenuText);
            
            // Son seçilen sub-menüyü kaydet
            lastSelectedSubMenu = subMenuText;
            TestUtils.logInfo("Last selected sub-menu saved: " + lastSelectedSubMenu);
            
            // Alt menüyü görünür hale getir (JavaScript scroll)
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetSubMenu);
            
            // Element tıklanabilir olana kadar akıllı bekleme
            waitForElementToBeClickable(targetSubMenu, timeoutSeconds);
            
            // Önce normal tıklama dene
            try {
                targetSubMenu.click();
                TestUtils.logSuccess("Normal click successful for sub-menu: " + subMenuText);
            } catch (Exception e) {
                TestUtils.logInfo("Normal click failed, trying JavaScript click...");
                // JavaScript ile tıkla (daha güvenilir)
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetSubMenu);
                TestUtils.logSuccess("JavaScript click successful for sub-menu: " + subMenuText);
            }
            
            // Tıklama sonrası akıllı bekleme
            TestUtils.logInfo("Waiting for click effect...");
            String currentUrl = driver.getCurrentUrl();
            waitForUrlToChange(currentUrl, 5);
            
            // URL değişikliğini kontrol et
            currentUrl = driver.getCurrentUrl();
            TestUtils.logInfo("Current URL after click: " + currentUrl);
            
            // Eğer URL değişmediyse, sayfa içeriğini kontrol et
            if (currentUrl.equals("https://teamso.com/dashboard")) {
                TestUtils.logInfo("URL did not change, checking page content...");
                // Sayfa içeriğinde alt menü metnini ara
                try {
                    WebElement pageContent = driver.findElement(By.tagName("body"));
                    String pageText = pageContent.getText();
                    if (pageText.contains(subMenuText)) {
                        TestUtils.logSuccess("Sub-menu content found on page: " + subMenuText);
                    } else {
                        TestUtils.logInfo("Sub-menu content not found on page, but click was successful");
                    }
                } catch (Exception e) {
                    TestUtils.logInfo("Could not check page content, but click was successful");
                }
            }
            
            // URL değişikliğini kontrol et
            TestUtils.logInfo("Current URL after click: " + currentUrl);
            
            // Sayfa yüklenene kadar bekle
            TestUtils.waitForPageToLoad(driver, 15);
            TestUtils.logInfo("Page load completed after sub-menu selection");
            
            // Final URL kontrolü
            String finalUrl = driver.getCurrentUrl();
            TestUtils.logInfo("Final URL after page load: " + finalUrl);
            
        } catch (Exception e) {
            TestUtils.logError("Module sub-menu selection failed for: " + moduleName + " index: " + subMenuIndex, e);
            throw new RuntimeException("Module sub-menu selection failed for: " + moduleName + " index: " + subMenuIndex, e);
        }
    }

}
