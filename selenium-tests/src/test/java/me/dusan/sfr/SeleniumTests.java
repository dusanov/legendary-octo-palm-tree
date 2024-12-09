package me.dusan.sfr;

//import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumTests {
    private static WebDriver driver;
    private static WebDriverWait wait;

    @BeforeAll
    public static void setup() {
//        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/main/resources/chromedriver");	    
//        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
	options.addArguments("--remote-allow-origins=*");
        // options.addArguments("--headless"); // Run in headless mode (no GUI)
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
        
        driver = new ChromeDriver(options);
//        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testHomePageTitle() {
        driver.get("http://localhost:3000"); // Adjust URL as needed
        String title = driver.getTitle();
        Assertions.assertTrue(title.contains("React App")); // Adjust expected title
    }
    
    @Test
    public void testSearchSuccess() {
        driver.get("http://localhost:3000"); // Adjust URL as needed
        WebElement searchFrom = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-select-3-input")));
        searchFrom.sendKeys("beg");
        searchFrom.sendKeys(Keys.ENTER);
        WebElement searchTo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-select-5-input")));
        searchTo.sendKeys("bkk");
        searchTo.sendKeys(Keys.ENTER);
        WebElement goButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("goButton")));
        goButton.click();
        WebElement resultText = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("resultText")));
        WebElement resultDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("resultDiv")));
        Assertions.assertTrue(resultDiv.isDisplayed() == true);
        Assertions.assertTrue(resultText.getText() != "");
    }

    @Test
    public void testSearchFail() {
        driver.get("http://localhost:3000"); // Adjust URL as needed
        WebElement searchFrom = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-select-3-input")));
        searchFrom.sendKeys("fake test");
        searchFrom.sendKeys(Keys.ENTER);
        WebElement searchTo = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-select-5-input")));
        searchTo.sendKeys("fake test");
        searchTo.sendKeys(Keys.ENTER);
        WebElement goButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("goButton")));
        goButton.click();
        WebElement resultDiv = null;
        WebElement resultText = null;
        try {
            resultText = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("resultText")));
            resultDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("resultDiv")));
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        Assertions.assertTrue(resultDiv == null);
        Assertions.assertTrue(resultText == null);
    }    
} 