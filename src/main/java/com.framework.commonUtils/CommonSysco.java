package com.framework.commonUtils;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Instant;
import java.util.List;

public class CommonSysco extends ParentPage {

    private final static Logger logger = Logger.getLogger(CommonSysco.class);
    public static WebDriver driver;
    public static WebDriverWait wait;
    public static String fileFormat = "CSV";

    public CommonSysco(WebDriver driver) {
        super(driver);
        CommonSysco.driver = driver;
    }

    public boolean doLogin(String user, String password) {
        try {
            waitForElementToAppear(Locators.loc_userName).sendKeys(user);
            waitForElementToAppear(Locators.loc_password).sendKeys(password);
            waitForElementToAppear(Locators.loc_login).click();
            if (waitForElementToAppear(Locators.loc_lnkProfile).isDisplayed())
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doLogout() {
        waitForElementToAppear(Locators.loc_lnkProfile).click();
        waitForElementToAppear(Locators.loc_signOut).click();
        waitForElementToAppear(Locators.loc_confirmLogout).click();
    }

    private void selectList(String listName) {
        // add more logic
        waitForElementToAppear(Locators.loc_lists).click();
        try {
            By loc_listItems = By.xpath(Locators.listItems.replace("listName", listName));
            waitForElementToAppear(loc_listItems).click();
            return;
        } catch (Exception ex) {
            logger.debug("account not selected by default");
        }
        waitForElementToAppear(Locators.loc_seeAllLists).click();
        waitForElementToAppear(Locators.loc_allListDropIcon).click();
        List<WebElement> elements = driver.findElements(Locators.loc_allListValues);
        for (WebElement element : elements) {
            if (element.getText().toLowerCase().contains(listName.toLowerCase())) {
                element.click();
                break;
            }
        }
    }

    public void exportList(String restName) {
        waitForElementToAppear(Locators.loc_moreActions).click();
        waitForElementToAppear(Locators.loc_exportList).click();
        waitForElementToAppear(Locators.loc_includePrices).click();
        waitForElementToAppear(Locators.loc_inputFileName).sendKeys(restName + Instant.now().getEpochSecond());
        waitForElementToAppear(Locators.loc_btnExport).click();
    }

    private void selectAccount(String accountName) {
        try {
            WebElement ele = waitForElementToAppear(Locators.loc_accountNumDash);
            if (ele.getText().contains(accountName))
                return;
            else
                throw new Exception("account not selected by default");
        } catch (Exception ex) {
            logger.debug(ex.getLocalizedMessage());
        }
        waitForElementToAppear(Locators.loc_accountDdlBtn).click();
        waitForElementToAppear(Locators.loc_accountSearchBtn).sendKeys(accountName);
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
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

    }


}
