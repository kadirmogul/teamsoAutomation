package utilities;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class MyDriver {
    
    private static WebDriver driver;
    
    private static final String[] CHROME_OPTIONS = {
        "--remote-allow-origins=*",
        "--disable-dev-shm-usage",
        "--no-sandbox", 
        "--disable-web-security",
        "--disable-features=VizDisplayCompositor",
        "--disable-extensions",
        "--disable-gpu",
        "--window-size=2560,1440",
        "--lang=tr",
        "--accept-lang=tr"
    };
    
    private static final int IMPLICIT_WAIT_SECONDS = 10;
    
    public static WebDriver getDriver() {
        if (driver == null) {
            driver = createChromeDriver();
        }
        return driver;
    }
    
    // Her test için yeni driver oluştur
    public static WebDriver createNewDriver() {
        // Eski driver'ı kapat
        closeDriver();
        // Yeni driver oluştur
        driver = createChromeDriver();
        return driver;
    }
    
    private static ChromeDriver createChromeDriver() {
        // Force update ChromeDriver to latest version
        WebDriverManager.chromedriver().clearDriverCache().setup();
        ChromeOptions options = createChromeOptions();
        ChromeDriver chromeDriver = new ChromeDriver(options);
        
        configureDriver(chromeDriver);
        return chromeDriver;
    }
    
    private static ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        
        for (String option : CHROME_OPTIONS) {
            options.addArguments(option);
        }
        
        return options;
    }
    
    private static void configureDriver(WebDriver driver) {
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT_SECONDS));
    }
    
    public static void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error while closing driver: " + e.getMessage());
            } finally {
                driver = null;
            }
        }
    }
    
    public static WebDriver getCurrentDriver() {
        return driver;
    }
    
    public static boolean isDriverActive() {
        return driver != null;
    }
}
