package com.example.selenium.lesson1;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.Augmenter;

import java.io.File;
import java.util.Date;


public class SeleniumTest {
    private WebDriver driver;

    @Before
    public void init() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        System.setProperty("webdriver.ie.driver", "src/test/resources/drivers/IEDriverServer.exe");
        //System.setProperty("webdriver.ie.driver", "src/test/resources/drivers/IEDriverServer32bit.exe");
        System.setProperty("webdriver.opera.driver", "src/test/resources/drivers/operadriver.exe");
        System.setProperty("phantomjs.binary.path", "src/test/resources/drivers/phantomjs.exe");

        driver = new ChromeDriver();
//        driver = new InternetExplorerDriver();

        driver.manage().window().maximize();
    }

    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            saveScreenshot(driver);
        }

        @Override
        protected void succeeded(Description description) {
        }
    };

    @Test
    public void testGoogleSearch() {
        driver.get("http://www.google.com");
        WebElement searchInput = driver.findElement(By.cssSelector("#lst-ib"));
        searchInput.sendKeys("selenium tutorial");
        searchInput.sendKeys(Keys.RETURN);
    }

    @Test
    public void loginUsingValidCredentials() {
        // WHEN user opens login page and inputs valid credentials



        // THEN user is logged in (and see dashboard)

    }

    @Test
    public void loginUsingInvalidCredentials() {
        // WHEN user opens login page and inputs invalid credentials


        // THEN user is not logged in and error message is shown

    }

    @Test
    public void loginUsingCookies() {
        driver.get("http://digitalnizena.cz/church-crm/notExistingPage");

        String cookieName = "CRM%40%2Fchurch-crm";
        String cookieValue = "5u3i4i9q2ilgbn742f38sksd84";
        String cookieDomain = "digitalnizena.cz";
        String cookiePath = "/";
        Date cookieExpiry = null;
        boolean cookieSecure = false;



    }

    @Test
    public void expandMenuDepositAndClickVersusClickWithoutExpanding() {

    }

    @Test
    public void deposit() throws InterruptedException {

    }


    public void saveScreenshot(WebDriver webDriver) {
        WebDriver returned = new Augmenter().augment(webDriver);
        if (returned != null) {
            File f = ((TakesScreenshot) returned).getScreenshotAs(OutputType.FILE);
            f.renameTo(new File(System.currentTimeMillis() + "-screenshot.png"));
        }
    }

    @After
    public void tearDown() throws Exception {

        // driver.quit();
    }



}