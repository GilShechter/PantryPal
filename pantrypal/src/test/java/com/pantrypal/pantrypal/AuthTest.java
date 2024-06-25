package com.pantrypal.pantrypal;

import com.pantrypal.pantrypal.jwt.DBUserService;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application.properties")
public class AuthTest {

    private WebDriver driver;

    @Autowired
    private Environment env;

    @Autowired
    private DBUserService userService;

    private String baseUrl;

    private static final String LOGIN_URL = "/login";
    private static final String HOME_URL = "/home";
    private static final String SIGNUP_URL = "/signup";
    private static final String USERNAME_INPUT_ID = "userNameInput";
    private static final String PASSWORD_INPUT_ID = "passwordInput";
    private static final String CONFIRM_PASSWORD_INPUT_ID = "confirmPasswordInput";
    private static final String SIGN_IN_BUTTON_XPATH = "//button[text()='Sign in']";
    private static final String SIGN_UP_BUTTON_XPATH = "//button[text()='Sign up']";
    private static final String TEST_USER = "testUser";
    private static final String TEST_PASS = "testPass";
    private static final String WRONG_PASS = "wrongPass";
    private static final String MISMATCHED_PASS = "mismatchedPass";
    private static final String LOGIN_FAIL_MESSAGE_ID = "login-fail-message";
    private static final String LOGIN_FAIL_MESSAGE = "Sign in failed. Please try again.";
    private static final String SIGNUP_FAIL_MESSAGE_ID = "signup-fail-message";
    private static final String SIGNUP_FAIL_MESSAGE = "Signup failed. Please try again.";
    private static final String MISMATCHED_PASS_MESSAGE = "Passwords do not match. Please try again.";

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
        baseUrl = env.getProperty("base.url", "http://localhost:8080");
        try{
            userService.deleteByUserName(TEST_USER);
        } catch (Exception e) {
            // do nothing
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        try{
            userService.deleteByUserName(TEST_USER);
        } catch (Exception e) {
            // do nothing
        }
    }

    @Test
    public void testSignUp() {
        signUp(TEST_USER, TEST_PASS, TEST_PASS);
        String expectedUrl = baseUrl + HOME_URL;
        assertEquals(expectedUrl, driver.getCurrentUrl());
    }

    @Test
    public void testSignUpWithMismatchedPasswords() {
        signUp(TEST_USER, TEST_PASS, MISMATCHED_PASS);
        String expectedUrl = baseUrl + SIGNUP_URL;
        assertEquals(expectedUrl, driver.getCurrentUrl());
        WebElement error = driver.findElement(By.id(SIGNUP_FAIL_MESSAGE_ID));
        assertEquals(MISMATCHED_PASS_MESSAGE, error.getText());
    }

    @Test
    public void testSignUpWithExistingUsername() {
        signUp(TEST_USER, TEST_PASS, TEST_PASS);
        logout();
        signUp(TEST_USER, TEST_PASS, TEST_PASS);
        String expectedUrl = baseUrl + SIGNUP_URL;
        assertEquals(expectedUrl, driver.getCurrentUrl());
        WebElement error = driver.findElement(By.id(SIGNUP_FAIL_MESSAGE_ID));
        assertEquals(SIGNUP_FAIL_MESSAGE, error.getText());
    }

    @Test
    public void testLogin() {
        signUp(TEST_USER, TEST_PASS, TEST_PASS);
        logout();
        login(TEST_USER, TEST_PASS);
        assertEquals(baseUrl + HOME_URL, driver.getCurrentUrl());
    }

    @Test
    public void testLoginFail() {
        login(TEST_USER, WRONG_PASS);
        assertEquals(baseUrl + LOGIN_URL, driver.getCurrentUrl());
        WebElement error = driver.findElement(By.id(LOGIN_FAIL_MESSAGE_ID));
        assertEquals(LOGIN_FAIL_MESSAGE, error.getText());
    }

    private void signUp(String username, String password, String confirmPassword) {
        driver.get(baseUrl + SIGNUP_URL);
        driver.findElement(By.id(USERNAME_INPUT_ID)).sendKeys(username);
        driver.findElement(By.id(PASSWORD_INPUT_ID)).sendKeys(password);
        driver.findElement(By.id(CONFIRM_PASSWORD_INPUT_ID)).sendKeys(confirmPassword);
        driver.findElement(By.xpath(SIGN_UP_BUTTON_XPATH)).click();
        waitForPageLoad();
    }

    private void login(String username, String password) {
        driver.get(baseUrl + LOGIN_URL);
        driver.findElement(By.id(USERNAME_INPUT_ID)).sendKeys(username);
        driver.findElement(By.id(PASSWORD_INPUT_ID)).sendKeys(password);
        driver.findElement(By.xpath(SIGN_IN_BUTTON_XPATH)).click();
        waitForPageLoad();
    }

    private void logout() {
        driver.get(baseUrl + HOME_URL);
        driver.findElement(By.id("logoutBtn")).click();
        waitForPageLoad();
    }

    private void waitForPageLoad() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
