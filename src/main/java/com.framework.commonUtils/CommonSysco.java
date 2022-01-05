package com.framework.commonUtils;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Instant;
import java.util.List;

public class CommonSysco {

    private final static Logger logger = Logger.getLogger(CommonSysco.class);
    private static final int TIMEOUT = 10;
    private static final int POLLING = 500;
    public static String fileFormat = "CSV";
    private final WebDriverWait wait;
    protected WebDriver driver;


    public CommonSysco(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, TIMEOUT, POLLING);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, TIMEOUT), this);
    }

    protected WebElement waitForElementToAppear(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementToClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
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

    public boolean doLogin(String user, String password) {
        try {
            driver.get(Constant.urlSyscoShop);
            Thread.sleep(4000);
            waitForElementToBePresent(Locators.loc_userNameDiscovery).sendKeys(user);
            waitForElementToBePresent(Locators.loc_nextDiscovery).click();
//            waitForElementToAppear(Locators.loc_userName).sendKeys(user);
            waitForElementToAppear(Locators.loc_password).sendKeys(password);
            waitForElementToAppear(Locators.loc_password).sendKeys(Keys.ENTER);
            Thread.sleep(1000);
            waitForElementToAppear(Locators.loc_login).click();
            Thread.sleep(7000);
            if (waitForElementToBePresent(Locators.loc_lnkProfile).isDisplayed())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doLogout() {
        try {
            waitForElementToBePresent(Locators.loc_lnkProfile);
            new Actions(driver).
                    clickAndHold(driver.findElement(Locators.loc_lnkProfile)).
                    build().
                    perform();
            Thread.sleep(1000);
            waitForElementToBePresent(Locators.loc_signOut).click();
            Thread.sleep(1000);
            waitForElementToBePresent(Locators.loc_confirmLogout).click();
        } catch (Exception ex) {
            logger.info("failed to logout");
        }
    }

    private void selectList(String listName) throws Exception {
/*        waitForElementToBePresent(Locators.loc_lists);
        logger.info("list name is "+ listName);
        new Actions(driver).
                moveToElement(driver.findElement(Locators.loc_lists)).
                clickAndHold().
                build().
                perform();
        try {
            By loc_listItems = By.xpath("//li[contains(text(),'"+listName+"')]");
            waitForElementToBePresent(loc_listItems).click();
            return;
        } catch (Exception ex) {
            logger.info("account not selected by default");
//            waitForElementToBePresent(Locators.loc_seeAllLists).click();
            driver.get("https://shop.sysco.com/app/lists");
        }*/
        driver.get("https://shop.sysco.com/app/lists");
        Thread.sleep(3000);

        if (isListDdlPresent()) {
            waitForElementToClickable(Locators.loc_allListDropIcon).click();
            Thread.sleep(2000);
            List<WebElement> elements = driver.findElements(Locators.loc_allListValues);
            for (WebElement element : elements) {
                String eleText = element.getText().toLowerCase();
                if (eleText.contains(listName.toLowerCase())) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                    Thread.sleep(500);
                    element.click();
                    return;
                }
            }
        }

        try {
            waitForElementToBePresent(By.xpath("//button[contains(.,'" + listName + "')]")).click();
        } catch (Exception ex) {
            logger.error("list sidebar also not present");
        }
//        throw new Exception("list Name not found in all lists");
    }


    public boolean isListDdlPresent() {
        try {
            waitForElementToAppear(Locators.loc_allListDropIcon);
            return true;
        } catch (Exception e) {
            logger.debug("no list drop down");
            return false;
        }
    }

    public void exportList(String restName) throws InterruptedException {
        waitForElementToClickable(Locators.loc_moreActions).click();
        waitForElementToClickable(Locators.loc_exportList).click();
        Thread.sleep(3000);
        waitForElementToClickable(Locators.loc_includePrices).click();
        waitForElementToBePresent(Locators.loc_inputFileName).clear();
        Thread.sleep(500);
        waitForElementToBePresent(Locators.loc_inputFileName).sendKeys(restName + "_" + Instant.now().getEpochSecond());
        waitForElementToClickable(Locators.loc_btnExport).click();
    }

    private void selectAccount(String accountName) throws Exception {
        /*try {
            WebElement ele = waitForElementToBePresent(Locators.loc_accountNumDash);
            if (ele.getText().contains(accountName))
                return;
            else
                throw new Exception("account not selected by default");
        } catch (Exception ex) {
            logger.info(ex.getLocalizedMessage());
        }*/
        Thread.sleep(3000);

        waitForElementToClickable(Locators.loc_accountDdlBtn).click();
        waitForElementToAppear(Locators.loc_accountSearchBtn).sendKeys(accountName);
        List<WebElement> elements = driver.findElements(Locators.loc_allAcNums);
        for (WebElement element : elements) {
            if (element.getText().contains(accountName)) {
                element.click();
                return;
            }
        }
        throw new Exception("account Name not found in all account names");
    }

    public boolean stepsToExport(String restName, String accountName, String listName) {
        try {
            Thread.sleep(3000);
            if (accountName != null && !accountName.equalsIgnoreCase(""))
                selectAccount(accountName);

            Thread.sleep(3000);
            if (listName != null && !listName.equalsIgnoreCase(""))
                selectList(listName);

            Thread.sleep(3000);
            exportList(restName);

            Thread.sleep(3000);
            doLogout();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
