package stepDefinitions;

/**
 * Authentication Step Definitions
 * Contains step definitions for login, logout, and authentication verification
 * Extends BaseTest for common WebDriver functionality
 */

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import org.testng.asserts.SoftAssert;
import utilities.BaseTest;
import utilities.TestUtils;
import java.util.List;

public class AuthenticationStepDefinitions extends BaseTest {

    private SoftAssert softAssert = new SoftAssert();
    
    // Filtreleme için değişkenler
    private WebElement selectedParameter = null;
    private WebElement selectedRecord = null;

    // ========== SETUP ==========
    
    @Before
    public void setUp() {
        try {
            // Her test için yeni driver oluştur
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            
            // Her test için değişkenleri sıfırla
            lastSelectedMenu = null;
            lastSelectedSubMenu = null;
            
            // Element referanslarını da temizle (stale element önleme)
            clearElementReferences();
            
            TestUtils.logInfo("Driver, variables and element references reset for new test");
        } catch (Exception e) {
            TestUtils.logError("Failed to setup driver", e);
        }
    }

    // ========== AUTHENTICATION STEPS ==========
    
    // YENİ LOGIN STEP - BaseTest'teki loginToSystem metodunu çağır
    @Given("Login to system with {string} and {string} and {string} and {string} and {string}")
    public void login_to_system_with_parameters(String pageUrl, String email, String searchText, String accountIndex, String password) {
        try {
            loginToSystem(pageUrl, email, searchText, accountIndex, password);
            TestUtils.logSuccess("Login to system step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Login to system step failed", e);
            softAssert.fail("Login to system step failed: " + e.getMessage());
        }
    }

    // LOGOUT STEP - BaseTest'teki performLogout metodunu çağır
    @And("perform logout from system")
    public void perform_logout_from_system() {
        try {
            performLogout();
            TestUtils.logSuccess("Logout step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Logout step failed", e);
            softAssert.fail("Logout step failed: " + e.getMessage());
        }
    }

    // ========== LOGIN VERIFICATION STEPS ==========
    
    @Then("verify login successful")
    public void verify_login_successful() {
        try {
            softAssert.assertTrue(driver.getCurrentUrl().contains("dashboard"), "Login successful - Redirected to dashboard");
        } catch (Exception e) {
            TestUtils.logError("Login verification failed", e);
            softAssert.fail("Login verification failed: " + e.getMessage());
        }
        TestUtils.logSuccess("Login verification successful");
    }
    
    // ========== MENU SELECTION STEPS ==========
    
    // YENİ BİRLEŞİK MENU STEP - BaseTest'teki selectMenuAndSubMenu metodunu çağır
    @And("Select menu {string} and sub-menu {string}")
    public void select_menu_and_submenu(String menuName, String subMenuIndex) {
        try {
            selectMenuAndSubMenu(menuName, subMenuIndex);
            TestUtils.logSuccess("Menu '" + menuName + "' and sub-menu '" + subMenuIndex + "' selection step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Menu and sub-menu selection step failed for: " + menuName + " - " + subMenuIndex, e);
            softAssert.fail("Menu and sub-menu selection step failed for: " + menuName + " - " + subMenuIndex + " - " + e.getMessage());
        }
    }

    @And("verify page opened successfully")
    public void verify_page_opened_successfully() {
        try {
            // BaseTest'teki verifyPageOpened metodunu çağır
            verifyPageOpened();
            TestUtils.logSuccess("Page verification step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Page verification step failed", e);
            softAssert.fail("Page verification step failed: " + e.getMessage());
        }
    }
    
    // ========== PARAMETER FINDING STEPS ==========
    
    // Parameter Name - custom-accordion div'lerini bul ve index'e göre seç
    @And("find parameter name at index {string}")
    public void find_parameter_name_at_index(String parameterIndex) {
        try {
            TestUtils.logInfo("Finding parameter name at index: " + parameterIndex);
            
            // custom-accordion class'ına sahip div'leri bul
            List<WebElement> accordionElements = driver.findElements(By.cssSelector("div.custom-accordion"));
            
            if (accordionElements.isEmpty()) {
                TestUtils.logError("No custom-accordion elements found on page", new Exception("No elements found"));
                softAssert.fail("No custom-accordion elements found on page");
                return;
            }
            
            int index = Integer.parseInt(parameterIndex);
            if (index < 0 || index >= accordionElements.size()) {
                TestUtils.logError("Parameter index " + index + " is out of range. Found " + accordionElements.size() + " elements", new Exception("Index out of range"));
                softAssert.fail("Parameter index " + index + " is out of range. Found " + accordionElements.size() + " elements");
                return;
            }
            
            WebElement targetAccordion = accordionElements.get(index);
            String parameterName = targetAccordion.getText().trim();
            
            TestUtils.logSuccess("Parameter name found at index " + index + ": " + parameterName);
            
            // Element'i tıklanabilir hale getir
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetAccordion);
            TestUtils.waitForSeconds(1);
            
            // Element'i tıkla
            targetAccordion.click();
            TestUtils.logSuccess("Parameter name clicked successfully");
            
            // Seçilen parameter'i kaydet (filtreleme için)
            selectedParameter = targetAccordion;
            TestUtils.logInfo("Selected parameter saved for filtering: " + parameterName);
            
        } catch (Exception e) {
            TestUtils.logError("Failed to find parameter name at index: " + parameterIndex, e);
            softAssert.fail("Failed to find parameter name at index: " + parameterIndex + " - " + e.getMessage());
        }
    }
    
    // Record Number - parametre tanımına bağlı kayıtları bul
    @And("find record number at index {string}")
    public void find_record_number_at_index(String recordIndex) {
        try {
            TestUtils.logInfo("Finding record number at index: " + recordIndex);
            
            List<WebElement> recordElements;
            
            // Eğer parameter seçilmişse, o parameter'e ait kayıtları bul
            if (selectedParameter != null) {
                TestUtils.logInfo("Filtering records by selected parameter...");
                try {
                    // Seçilen parameter'in altındaki kayıtları bul (parameter'in kendisi değil, onun altındaki kayıtlar)
                    // Önce parameter'in parent container'ını bul, sonra onun altındaki kayıtları bul
                    WebElement parameterContainer = selectedParameter.findElement(By.xpath("./.."));
                    recordElements = parameterContainer.findElements(By.cssSelector("div.col-xxl-4.col-md-6.col-12.pb-3"));
                    TestUtils.logInfo("Found " + recordElements.size() + " records under selected parameter");
                    
                    // Eğer parameter altında kayıt bulunamazsa, genel arama yap
                    if (recordElements.isEmpty()) {
                        TestUtils.logInfo("No records found under parameter, trying general search...");
                        recordElements = driver.findElements(By.cssSelector("div.col-xxl-4.col-md-6.col-12.pb-3"));
                    }
                } catch (Exception e) {
                    TestUtils.logInfo("Could not find records under parameter, using general search...");
                    recordElements = driver.findElements(By.cssSelector("div.col-xxl-4.col-md-6.col-12.pb-3"));
                }
            } else {
                TestUtils.logInfo("No parameter selected, using general record search...");
                // Genel kayıt arama
                recordElements = driver.findElements(By.cssSelector("div.col-xxl-4.col-md-6.col-12.pb-3"));
            }
            
            if (recordElements.isEmpty()) {
                TestUtils.logError("No record elements found on page", new Exception("No elements found"));
                softAssert.fail("No record elements found on page");
                return;
            }
            
            int index = Integer.parseInt(recordIndex);
            if (index < 0 || index >= recordElements.size()) {
                TestUtils.logError("Record index " + index + " is out of range. Found " + recordElements.size() + " elements", new Exception("Index out of range"));
                softAssert.fail("Record index " + index + " is out of range. Found " + recordElements.size() + " elements");
                return;
            }
            
            WebElement targetRecord = recordElements.get(index);
            String recordNumber = targetRecord.getText().trim();
            
            TestUtils.logSuccess("Record number found at index " + index + ": " + recordNumber);
            
            // Element'i tıklanabilir hale getir
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetRecord);
            TestUtils.waitForSeconds(1);
            
            // Overlay removal
            try {
                ((JavascriptExecutor) driver).executeScript(
                    "var overlays = document.querySelectorAll('.welcome-text-for-desktop, .navbar-header, .sticky-header, .overlay, .modal-backdrop');" +
                    "overlays.forEach(function(overlay) { overlay.style.display = 'none'; });"
                );
                TestUtils.logInfo("Overlay elements removed for record click");
            } catch (Exception e) {
                TestUtils.logInfo("Could not remove overlays, continuing...");
            }
            
            // Multi-click strategy
            boolean clickSuccessful = false;
            
            // 1. Normal click
            try {
                targetRecord.click();
                TestUtils.logSuccess("Normal click successful for record: " + recordNumber);
                clickSuccessful = true;
            } catch (Exception e) {
                TestUtils.logInfo("Normal click failed, trying JavaScript click...");
                
                // 2. JavaScript click
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetRecord);
                    TestUtils.logSuccess("JavaScript click successful for record: " + recordNumber);
                    clickSuccessful = true;
                } catch (Exception e2) {
                    TestUtils.logInfo("JavaScript click failed, trying Actions click...");
                    
                    // 3. Actions click
                    try {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(targetRecord).click().perform();
                        TestUtils.logSuccess("Actions click successful for record: " + recordNumber);
                        clickSuccessful = true;
                    } catch (Exception e3) {
                        TestUtils.logError("All click methods failed for record: " + recordNumber, e3);
                    }
                }
            }
            
            if (!clickSuccessful) {
                TestUtils.logError("All click methods failed for record: " + recordNumber, new Exception("Click failed"));
                softAssert.fail("Failed to click record: " + recordNumber);
                return;
            }
            
            TestUtils.logSuccess("Record number clicked successfully");
            
            // Seçilen record'u kaydet (filtreleme için)
            selectedRecord = targetRecord;
            TestUtils.logInfo("Selected record saved for filtering: " + recordNumber);
            
        } catch (Exception e) {
            TestUtils.logError("Failed to find record number at index: " + recordIndex, e);
            softAssert.fail("Failed to find record number at index: " + recordIndex + " - " + e.getMessage());
        }
    }
    
    // Option ID - seçenekler listesini bul ve metin'e göre seç
    @And("find option id with text {string}")
    public void find_option_id_with_text(String optionText) {
        try {
            TestUtils.logInfo("Finding option id with text: " + optionText);
            
            List<WebElement> optionElements;
            
            // Eğer record seçilmişse, o record'a ait seçenekleri bul
            if (selectedRecord != null) {
                TestUtils.logInfo("Filtering options by selected record...");
                try {
                    // Seçilen record'un kendisindeki seçenekleri bul
                    optionElements = selectedRecord.findElements(By.cssSelector("div.col-lg-1.d-flex.justify-content-end.align-items-center"));
                    TestUtils.logInfo("Found " + optionElements.size() + " options in selected record");
                    
                    // Eğer record'da seçenek bulunamazsa, genel arama yap
                    if (optionElements.isEmpty()) {
                        TestUtils.logInfo("No options found in record, trying general search...");
                        optionElements = driver.findElements(By.cssSelector("div.col-lg-1.d-flex.justify-content-end.align-items-center"));
                    }
                } catch (Exception e) {
                    TestUtils.logInfo("Could not find options in record, using general search...");
                    optionElements = driver.findElements(By.cssSelector("div.col-lg-1.d-flex.justify-content-end.align-items-center"));
                }
            } else {
                TestUtils.logInfo("No record selected, using general option search...");
                // Genel seçenek arama
                optionElements = driver.findElements(By.cssSelector("div.col-lg-1.d-flex.justify-content-end.align-items-center"));
            }
            
            // DEBUG: Bulunan elementleri listele
            TestUtils.logInfo("=== OPTION ELEMENTS DEBUG ===");
            TestUtils.logInfo("Found " + optionElements.size() + " option elements");
            
            for (int i = 0; i < optionElements.size(); i++) {
                WebElement element = optionElements.get(i);
                try {
                    String text = element.getText().trim();
                    String tag = element.getTagName();
                    String classes = element.getAttribute("class");
                    boolean displayed = element.isDisplayed();
                    boolean enabled = element.isEnabled();
                    
                    TestUtils.logInfo("Option Element " + i + " - Tag: " + tag + 
                                     ", Class: '" + classes + "'" +
                                     ", Text: '" + text + "'" +
                                     ", Displayed: " + displayed + 
                                     ", Enabled: " + enabled);
                } catch (Exception e) {
                    TestUtils.logInfo("Option Element " + i + " - Error reading element: " + e.getMessage());
                }
            }
            TestUtils.logInfo("=== END OPTION ELEMENTS DEBUG ===");
            
            if (optionElements.isEmpty()) {
                TestUtils.logError("No option elements found on page", new Exception("No elements found"));
                softAssert.fail("No option elements found on page");
                return;
            }
            
            // Metin araması yap (hem text hem de title attribute)
            WebElement targetOption = null;
            for (WebElement element : optionElements) {
                try {
                    String elementText = element.getText().trim();
                    String elementTitle = element.getAttribute("title");
                    
                    TestUtils.logInfo("Checking element - Text: '" + elementText + "', Title: '" + elementTitle + "'");
                    
                    if (elementText.contains(optionText) || (elementTitle != null && elementTitle.contains(optionText))) {
                        targetOption = element;
                        TestUtils.logInfo("Found matching option with text: '" + elementText + "' or title: '" + elementTitle + "'");
                        break;
                    }
                } catch (Exception e) {
                    TestUtils.logInfo("Error reading element text/title: " + e.getMessage());
                }
            }
            
            if (targetOption == null) {
                TestUtils.logError("No option found with text containing: " + optionText, new Exception("Text not found"));
                softAssert.fail("No option found with text containing: " + optionText);
                return;
            }
            
            String optionId = targetOption.getText().trim();
            
            // DEBUG: Tıklamadan önce bulunan değeri göster
            TestUtils.logInfo("=== FOUND OPTION BEFORE CLICK ===");
            TestUtils.logInfo("Option Text: '" + optionId + "'");
            TestUtils.logInfo("Option Tag: " + targetOption.getTagName());
            TestUtils.logInfo("Option Class: '" + targetOption.getAttribute("class") + "'");
            TestUtils.logInfo("Option Displayed: " + targetOption.isDisplayed());
            TestUtils.logInfo("Option Enabled: " + targetOption.isEnabled());
            TestUtils.logInfo("=== END FOUND OPTION ===");
            
            TestUtils.logSuccess("Option id found with text: " + optionId);
            
            // Element'i tıklanabilir hale getir
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", targetOption);
            TestUtils.waitForSeconds(1);
            
            // Overlay elementlerini kaldır
            try {
                ((JavascriptExecutor) driver).executeScript(
                    "var overlays = document.querySelectorAll('.navbar-header, .sticky-header, .overlay, .modal-backdrop');" +
                    "overlays.forEach(function(overlay) { overlay.style.display = 'none'; });"
                );
                TestUtils.logInfo("Overlay elements removed for option click");
            } catch (Exception e) {
                TestUtils.logInfo("Could not remove overlays, continuing...");
            }
            
            // Çoklu tıklama stratejisi
            boolean clickSuccessful = false;
            
            // 1. Normal tıklama dene
            try {
                targetOption.click();
                TestUtils.logSuccess("Normal click successful for option id: " + optionId);
                clickSuccessful = true;
            } catch (Exception e) {
                TestUtils.logInfo("Normal click failed, trying JavaScript click...");
                
                // 2. JavaScript ile tıkla
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetOption);
                    TestUtils.logSuccess("JavaScript click successful for option id: " + optionId);
                    clickSuccessful = true;
                } catch (Exception e2) {
                    TestUtils.logInfo("JavaScript click failed, trying Actions click...");
                    
                    // 3. Actions ile tıkla
                    try {
                        Actions actions = new Actions(driver);
                        actions.moveToElement(targetOption).click().perform();
                        TestUtils.logSuccess("Actions click successful for option id: " + optionId);
                        clickSuccessful = true;
                    } catch (Exception e3) {
                        TestUtils.logError("All click methods failed for option id: " + optionId, e3);
                    }
                }
            }
            
            if (!clickSuccessful) {
                TestUtils.logError("All click methods failed for option id: " + optionId, new Exception("Click failed"));
                softAssert.fail("Failed to click option id: " + optionId);
            } else {
                // Tıklama başarılı oldu, 5 saniye bekle
                TestUtils.logInfo("Click successful, waiting 5 seconds to observe the result...");
                TestUtils.waitForSeconds(5);
                TestUtils.logInfo("5 second wait completed");
            }
            
        } catch (Exception e) {
            TestUtils.logError("Failed to find option id with text: " + optionText, e);
            softAssert.fail("Failed to find option id with text: " + optionText + " - " + e.getMessage());
        }
    }
    

    // ========== TEARDOWN ==========
    
    @After
    public void tearDown() {
        try {
            // BaseTest'teki driver'ı kullan, yenisini oluşturma
            if (driver != null && isDriverActive()) {
                driver.quit();
                driver = null;
                TestUtils.logSuccess("Driver closed and reference cleared");
            }
        } catch (Exception e) {
            TestUtils.logError("Failed to close driver", e);
        } finally {
            softAssert.assertAll();
        }
    }
}
