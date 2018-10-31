package com.example.selenium.lesson1;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


public class SeleniumLectorTest {
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

        driver.get("http://digitalnizena.cz/church/");

        driver.findElement(By.cssSelector(".login-box"));

        WebElement usernameInput = driver.findElement(By.id("UserBox"));
        WebElement passwordInput = driver.findElement(By.id("PasswordBox"));
        WebElement loginButton = driver.findElement(By.className("btn-primary"));

        usernameInput.sendKeys("church");
        //passwordInput.sendKeys("#V%q1@Fmdr");
        passwordInput.sendKeys("church12345");
        loginButton.click();

        // THEN user is logged in (and see dashboard)
        assertThat(driver.getCurrentUrl()).isEqualTo("http://digitalnizena.cz/church/Menu.php");
        assertThat(driver.findElements(By.cssSelector(".login-box"))).isEmpty();

        assertThat(driver.getTitle()).contains("ChurchCRM: Welcome to");
        driver.findElement(By.cssSelector("aside.main-sidebar"));
    }

    @Test
    public void loginUsingInvalidCredentials() {
        // WHEN user opens login page and inputs invalid credentials
        driver.get("http://digitalnizena.cz/church/");

        WebElement usernameInput = driver.findElement(By.id("UserBox"));
        WebElement passwordInput = driver.findElement(By.id("PasswordBox"));
        WebElement loginButton = driver.findElement(By.className("btn-primary"));

        usernameInput.sendKeys("invalidUsername");
        passwordInput.sendKeys("invalidPassword");
        loginButton.click();

        // THEN user is not logged in and error message is shown
        assertThat(driver.getCurrentUrl()).isEqualTo("http://digitalnizena.cz/church/Login.php");
        assertThat(driver.findElements(By.cssSelector(".login-box"))).isNotEmpty();
        WebElement errorDiv = driver.findElement(By.cssSelector("#Login .alert.alert-error"));    // there are in fact two .alert .alert-error boxes in page, we want for login div
        assertThat(errorDiv.getText()).isEqualTo("Invalid login or password");
    }

    @Test
    public void loginUsingCookies() {
        driver.get("http://digitalnizena.cz/church/notExistingPage");

        String cookieName = "CRM%40%2Fchurch";
        String cookieValue = "5u3i4i9q2ilgbn742f38sksd84";
        String cookieDomain = "digitalnizena.cz";
        String cookiePath = "/";
        Date cookieExpiry = null;
        boolean cookieSecure = false;

        Cookie ck = new Cookie(cookieName, cookieValue, cookieDomain, cookiePath, cookieExpiry, cookieSecure);
        driver.manage().addCookie(ck); // This will add the stored cookie to your current session

        driver.get("http://digitalnizena.cz/church/FindDepositSlip.php");
    }

    @Test
    public void expandMenuDepositAndClickVersusClickWithoutExpanding() {
        loginUsingValidCredentials();

        WebElement depositTreeViewItem = driver.findElement(By.cssSelector("aside.main-sidebar .sidebar ul.sidebar-menu > li.treeview:nth-child(8) > a"));
        // compare it with this expression      driver.findElement(By.cssSelector("aside.main-sidebar .sidebar ul.sidebar-menu > li.treeview:nth-child(8) a")).click();

        // TODO try without expanding tree
        depositTreeViewItem.click();

        WebElement viewAllDepositsItem = driver.findElement(By.xpath("/html/body/div[2]/aside[1]/section/ul/li[8]/ul/li[1]/a"));
        viewAllDepositsItem.click();

        assertThat(driver.getCurrentUrl()).isEqualTo("http://digitalnizena.cz/church/FindDepositSlip.php");
    }

    @Test
    public void insertDeposit() throws InterruptedException {
        loginUsingValidCredentials();

        driver.get("http://digitalnizena.cz/church/FindDepositSlip.php");

        WebElement depositCommentInput = driver.findElement(By.cssSelector("#depositComment"));
        String depositComment = "deposit-PavelG-" + UUID.randomUUID().toString();
        depositCommentInput.sendKeys(depositComment);

        WebElement depositTypeElement = driver.findElement(By.cssSelector("#depositType"));
        Select depositTypeSelect = new Select(depositTypeElement);
        depositTypeSelect.selectByVisibleText("Credit Card");

        WebElement depositDateInput = driver.findElement(By.cssSelector("#depositDate"));
        depositDateInput.click();
        depositDateInput.clear();
        depositDateInput.sendKeys("2018-10-30");

        WebElement addDepositButton = driver.findElement(By.cssSelector("#addNewDeposit"));
        addDepositButton.click();

        Thread.sleep(2000);       // FIXME   !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        List<WebElement> depositRows = driver.findElements(By.cssSelector("#depositsTable_wrapper #depositsTable tbody tr"));
        WebElement firstRow = depositRows.get(0);
        String innerHTML = firstRow.getAttribute("innerHTML");
        assertThat(innerHTML).contains("10-30-18");    // TODO pozor jiny format
        assertThat(innerHTML).contains(depositComment);


        for (WebElement row: depositRows) {
            row.click();
        }

        WebElement deleteButton = driver.findElement(By.cssSelector("#deleteSelectedRows"));
        deleteButton.click();

        //TODO compare this WebElement confirmDeleteButton = driver.findElement(By.cssSelector(".modal-dialog .btn-primary"));
        WebElement confirmDeleteButton = driver.findElement(By.cssSelector(".modal-content > .modal-footer .btn-primary"));
        WebDriverWait wait = new WebDriverWait(driver, 1);
        wait.until(ExpectedConditions.visibilityOf(confirmDeleteButton));
        confirmDeleteButton.click();




//
//        for (int i = 0; i < depositRows.size(); i++) {
//            WebElement row = depositRows.get(i);
//            String rowHTML = row.getAttribute("innerHTML");
//        }

        // option0 existuje aspon 1 radek
        // option1 row innerHTML contains
        // option2
        // option3 wrap html fragment into some DOM / XML builder and traverse it
//            row.findElement(By.cssSelector(""))
//            row.
//            driver.findElement(By.cssSelector("#depositsTable_wrapper #depositsTable tbody tr"))
//            new Table
//elem.getAttribute("innerHTML");
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

    public void hokusyPokusy() {
        //        List<WebElement> elements = driver.findElements(By.cssSelector("login-box"));

        driver.get("http://digitalnizena.cz/church/");
        WebDriverWait wait = new WebDriverWait(driver, 0);
//        wait.until(ExpectedConditions.textpresenceOfElementLocated(By.cssSelector(".login-box")));
        wait.until(ExpectedConditions.urlToBe("http://digitalnizena.cz/church/ddd"));
    }

}