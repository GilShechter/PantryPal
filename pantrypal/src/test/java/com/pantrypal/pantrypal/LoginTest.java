package com.pantrypal.pantrypal;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class LoginTest {

    private WebDriver driver;

    @Autowired
    private Environment env;

    private String baseUrl;

    private static final String LOGIN_URL = "/login";
    private static final String HOME_URL = "/home";
    private static final String USERNAME_INPUT_ID = "userNameInput";
    private static final String PASSWORD_INPUT_ID = "passwordInput";
    private static final String SIGN_IN_BUTTON_XPATH = "//button[text()='Sign in']";
    private static final String TEST_USER = "testUser";
    private static final String TEST_PASS = "testPass";
    private static final String WRONG_PASS = "wrongPass";

    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        baseUrl = env.getProperty("base.url");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLogin() {
        login(TEST_USER, TEST_PASS);
        assertEquals(baseUrl + HOME_URL, driver.getCurrentUrl());
    }

    @Test
    public void testLoginFail() {
        login(TEST_USER, WRONG_PASS);
        assertEquals(baseUrl + LOGIN_URL, driver.getCurrentUrl());
    }

    private void login(String username, String password) {
        driver.get(baseUrl + LOGIN_URL);
        driver.findElement(By.id(USERNAME_INPUT_ID)).sendKeys(username);
        driver.findElement(By.id(PASSWORD_INPUT_ID)).sendKeys(password);
        driver.findElement(By.xpath(SIGN_IN_BUTTON_XPATH)).click();
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
