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
            // Her test öncesi driver'ı null yap ki performLogin'de yeni oluşturulsun
            driver = null;
            TestUtils.logInfo("Driver reset for new test");
        } catch (Exception e) {
            TestUtils.logError("Failed to reset driver", e);
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
