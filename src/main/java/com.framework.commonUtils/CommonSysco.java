package com.framework.commonUtils;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Instant;
import java.util.List;

public class CommonSysco {

    private final static Logger logger = Logger.getLogger(CommonSysco.class);
    private static final int TIMEOUT = 5;
    private static final int POLLING = 100;

    protected WebDriver driver;
    private WebDriverWait wait;
    public static String fileFormat = "CSV";


    protected WebElement waitForElementToAppear(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementToBePresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean waitForElementToDisappear(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean waitForTextToDisappear(By locator, String text) {
        return wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(locator, text)));
    }

    public CommonSysco(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, TIMEOUT, POLLING);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, TIMEOUT), this);
    }

    public boolean doLogin(String user, String password) {
        try {
            driver.get(Constant.urlSyscoShop);
            Thread.sleep(4000);
            waitForElementToBePresent(Locators.loc_userNameDiscovery).sendKeys(user);
            waitForElementToBePresent(Locators.loc_nextDiscovery).click();
//            waitForElementToAppear(Locators.loc_userName).sendKeys(user);
            waitForElementToBePresent(Locators.loc_password).sendKeys(password);
            waitForElementToBePresent(Locators.loc_login).click();
            if (waitForElementToBePresent(Locators.loc_lnkProfile).isDisplayed())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doLogout() {
        try {
            waitForElementToBePresent(Locators.loc_lnkProfile).click();
            waitForElementToBePresent(Locators.loc_signOut).click();
            waitForElementToBePresent(Locators.loc_confirmLogout).click();
        }catch (Exception ex){
            logger.debug("failed to logout");
        }
    }

    private void selectList(String listName) {
        // add more logic
        waitForElementToBePresent(Locators.loc_lists).click();
        try {
            By loc_listItems = By.xpath(Locators.listItems.replace("listName", listName));
            waitForElementToBePresent(loc_listItems).click();
            return;
        } catch (Exception ex) {
            logger.debug("account not selected by default");
        }
        waitForElementToBePresent(Locators.loc_seeAllLists).click();
        waitForElementToBePresent(Locators.loc_allListDropIcon).click();
        List<WebElement> elements = driver.findElements(Locators.loc_allListValues);
        for (WebElement element : elements) {
            if (element.getText().toLowerCase().contains(listName.toLowerCase())) {
                element.click();
                break;
            }
        }
    }

    public void exportList(String restName) {
        waitForElementToBePresent(Locators.loc_moreActions).click();
        waitForElementToBePresent(Locators.loc_exportList).click();
        waitForElementToBePresent(Locators.loc_includePrices).click();
        waitForElementToBePresent(Locators.loc_inputFileName).sendKeys(restName + Instant.now().getEpochSecond());
        waitForElementToBePresent(Locators.loc_btnExport).click();
    }

    private void selectAccount(String accountName) {
        try {
            WebElement ele = waitForElementToBePresent(Locators.loc_accountNumDash);
            if (ele.getText().contains(accountName))
                return;
            else
                throw new Exception("account not selected by default");
        } catch (Exception ex) {
            logger.debug(ex.getLocalizedMessage());
        }
        waitForElementToBePresent(Locators.loc_accountDdlBtn).click();
        waitForElementToBePresent(Locators.loc_accountSearchBtn).sendKeys(accountName);
        List<WebElement> elements = driver.findElements(Locators.loc_allAcNums);
        for (WebElement element : elements) {
            if (element.getText().contains(accountName)) {
                element.click();
                break;
            }
        }
    }

    public boolean stepsToExport(String restName, String accountName, String listName) {
        try {
            selectAccount(accountName);
            selectList(listName);
            exportList(restName);
            doLogout();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
