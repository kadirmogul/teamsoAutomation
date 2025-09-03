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
import utilities.MyDriver;

public class AuthenticationStepDefinitions extends BaseTest {

    private SoftAssert softAssert = new SoftAssert();

    // ========== SETUP ==========
    
    @Before
    public void setUp() {
        try {
            // Sadece driver yoksa yeni oluştur, varsa kullan
            if (driver == null) {
                TestUtils.logInfo("Driver reset for new test");
            } else {
                TestUtils.logInfo("Using existing driver for test");
            }
        } catch (Exception e) {
            TestUtils.logError("Failed to setup driver", e);
        }
    }

    // ========== AUTHENTICATION STEPS ==========
    
    // LOGIN STEP - BaseTest'teki performLogin metodunu parametrelerle çağır
    @Given("perform login with {string} and {string} and {string} and {string} and {string}")
    public void perform_login_with_parameters(String pageUrl, String email, String searchText, String accountIndex, String password) {
        // Feature'dan gelen parametreleri kullan
        performLogin(pageUrl, email, searchText, accountIndex, password);
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
    
    @And("select menu {string}")
    public void select_menu(String menuName) {
        try {
            // BaseTest'teki selectMenu metodunu çağır
            selectMenu(menuName, 15);
            TestUtils.logSuccess("Menu '" + menuName + "' selection step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Menu selection step failed for: " + menuName, e);
            softAssert.fail("Menu selection step failed for: " + menuName + " - " + e.getMessage());
        }
    }
    
    @And("select sub-menu index {string}")
    public void select_sub_menu_index(String subMenuIndex) {
        try {
            // String'i int'e çevir
            int index = Integer.parseInt(subMenuIndex);
            
            // BaseTest'teki selectModuleSubMenu metodunu çağır (menuName ile)
            // Son seçilen menüyü kullan
            String lastSelectedMenu = getLastSelectedMenu();
            selectModuleSubMenu(lastSelectedMenu, index, 15);
            TestUtils.logSuccess("Sub-menu index " + index + " selection step completed successfully");
        } catch (Exception e) {
            TestUtils.logError("Sub-menu selection step failed for index: " + subMenuIndex, e);
            softAssert.fail("Sub-menu selection step failed for index: " + subMenuIndex + " - " + e.getMessage());
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
