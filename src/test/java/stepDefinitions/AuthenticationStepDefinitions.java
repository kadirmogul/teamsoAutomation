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
import org.testng.asserts.SoftAssert;
import utilities.BaseTest;
import utilities.TestUtils;

public class AuthenticationStepDefinitions extends BaseTest {

    private SoftAssert softAssert = new SoftAssert();

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
    
    @And("verify settings page opened successfully")
    public void verify_settings_page_opened_successfully() {
        try {
            // BaseTest'teki verifySettingsPageOpened metodunu çağır
            verifySettingsPageOpened();
            TestUtils.logSuccess("Settings page verification step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Settings page verification step failed", e);
            softAssert.fail("Settings page verification step failed: " + e.getMessage());
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
