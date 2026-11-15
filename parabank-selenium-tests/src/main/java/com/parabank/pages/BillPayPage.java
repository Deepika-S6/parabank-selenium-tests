package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class BillPayPage extends BasePage {

    private static final By PAYEE_NAME = By.name("payee.name");
    private static final By STREET = By.name("payee.address.street");
    private static final By CITY = By.name("payee.address.city");
    private static final By STATE = By.name("payee.address.state");
    private static final By ZIP = By.name("payee.address.zipCode");
    private static final By PHONE = By.name("payee.phoneNumber");
    private static final By ACCOUNT = By.name("payee.accountNumber");
    private static final By VERIFY = By.name("verifyAccount");
    private static final By AMOUNT = By.name("amount");
    private static final By FROM = By.name("fromAccountId");
    private static final By SEND = By.cssSelector("input[value='Send Payment']");

    private static final By SUCCESS_BLOCK = By.id("billpayResult");
    private static final By ERRORS = By.cssSelector(
            "#rightPanel span.error, #rightPanel p.error, #rightPanel .error, #showError");

    public BillPayPage(WebDriver driver) {
        super(driver);
    }

    public BillPayPage waitUntilReady() {
        wait.until(d -> {
            java.util.List<WebElement> found = d.findElements(FROM);
            return !found.isEmpty() && new Select(found.get(0)).getOptions().size() > 0;
        });
        return this;
    }

    public BillPayPage fillPayee(String name, String street, String city, String state, String zip,
                                 String phone, String account, String verify) {
        waitUntilReady();
        type(PAYEE_NAME, name);
        type(STREET, street);
        type(CITY, city);
        type(STATE, state);
        type(ZIP, zip);
        type(PHONE, phone);
        type(ACCOUNT, account);
        type(VERIFY, verify);
        return this;
    }

    public BillPayPage pay(String amount, String fromAccountId) {
        type(AMOUNT, amount);
        selectByVisibleText(FROM, fromAccountId);
        click(SEND);
        try {
            wait.until(d -> isPaymentSuccessful() || isErrorShown());
        } catch (TimeoutException ignored) {
            // no outcome appeared: let the test's assertion report it with the page text
        }
        return this;
    }

    public boolean isPaymentSuccessful() {
        java.util.List<WebElement> block = driver.findElements(SUCCESS_BLOCK);
        return !block.isEmpty() && block.get(0).isDisplayed()
                && block.get(0).getText().toLowerCase().contains("complete");
    }

    public boolean isErrorShown() {
        for (WebElement e : driver.findElements(ERRORS)) {
            if (e.isDisplayed() && !e.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public String getPanelText() {
        return driver.findElement(By.id("rightPanel")).getText().replaceAll("\\s+", " ");
    }
}