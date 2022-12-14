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

import static com.framework.commonUtils.RandomAction.*;

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

    protected List<WebElement> waitForElementsToAppear(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
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
            Thread.sleep(10000);
            if (waitForElementToBePresent(Locators.loc_lnkProfile).isDisplayed())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            driver.get("https://shop.sysco.com/app/discover");
            if (waitForElementToBePresent(Locators.loc_lnkProfile).isDisplayed())
                return true;
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

        List<WebElement> elements = null;

        if (isListDdlPresent()) {
            waitForElementToClickable(Locators.loc_allListDropIcon).click();
            Thread.sleep(1000);
            elements = driver.findElements(Locators.loc_allListValues);
        } else {
            elements = driver.findElements(Locators.loc_btnListNames);
        }

        for (WebElement element : elements) {
            String eleText = element.getText().toLowerCase();
            if (eleText.contains(listName.toLowerCase())) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                Thread.sleep(200);
                element.click();
                return;
            }
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
        Thread.sleep(100);
        WebElement text_input = waitForElementToBePresent(Locators.loc_inputFileName);
        text_input.click();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '';", text_input);
        Thread.sleep(200);
        text_input.sendKeys(restName.replaceAll("[^A-Za-z]+", "") + "_" + Instant.now().getEpochSecond());
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
        try {
            // search and finf acc.
            waitForElementToAppear(Locators.loc_accountSearchBtn).sendKeys(accountName);
//            waitForElementToAppear(Locators.loc_firstAccount).click();
            Thread.sleep(100);
            waitForElementToAppear(By.xpath(Locators.loc_accountNum.replace("accountName", accountName))).click();
        } catch (Exception ex) {
            logger.error("failed to select account");
            ex.printStackTrace();
        }

        // get all acc. text and compare
//        waitForElementToAppear(Locators.loc_accountSearchBtn).sendKeys(accountName);
//        List<WebElement> elements = driver.findElements(Locators.loc_allAcNums);
//        for (WebElement element : elements) {
//            if (element.getText().contains(accountName)) {
//                element.click();
//                return;
//            }
//        }
    }

    public boolean stepsToExport(String restName, String accountName, String listName) {
        try {
//            Thread.sleep(3000);
            if (isIframePresent(driver)) {
                dismissPopUp();
            }
            dismissAlert();

            if (accountName != null && !accountName.equalsIgnoreCase("")) {
                selectAccount(accountName);
                Thread.sleep(5000);
                if (!verifyAccount(accountName)) {
                    throw new Exception(String.format("account selection mismatch, expected account {} not selected", accountName));
                }
            }

            if (isIframePresent(driver)) {
                dismissPopUp();
            }
            dismissAlert();

            if (listName != null && !listName.equalsIgnoreCase("")) {
                selectList(listName);
            }

            if (isIframePresent(driver)) {
                dismissPopUp();
            }
            dismissAlert();

            exportList(restName);

            Thread.sleep(3000);
            doLogout();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean verifyAccount(String accountName) {
        return waitForElementToBePresent(By.xpath(Locators.loc_accountName.replace("acNum", accountName))).isDisplayed();
    }
}
