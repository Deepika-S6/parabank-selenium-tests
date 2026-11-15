package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class OpenAccountPage extends BasePage {

    public enum Type {
        CHECKING("0"), SAVINGS("1");
        final String value;
        Type(String value) { this.value = value; }
    }

    private static final By TYPE_SELECT = By.id("type");
    private static final By FROM_SELECT = By.id("fromAccountId");
    private static final By OPEN_BUTTON = By.cssSelector("input[value='Open New Account']");
    private static final By NEW_ACCOUNT_ID = By.id("newAccountId");

    public OpenAccountPage(WebDriver driver) {
        super(driver);
    }

    public OpenAccountPage waitUntilReady() {
        wait.until(d -> {
            var found = d.findElements(FROM_SELECT);
            return !found.isEmpty() && new Select(found.get(0)).getOptions().size() > 0;
        });
        return this;
    }

    public String openAccount(Type type) {
        waitUntilReady();
        selectByValue(TYPE_SELECT, type.value);   // by value, not by text
        click(OPEN_BUTTON);
        // wait until the new account number is actually rendered
        wait.until(d -> {
            var found = d.findElements(NEW_ACCOUNT_ID);
            return !found.isEmpty() && found.get(0).isDisplayed() && !found.get(0).getText().trim().isEmpty();
        });
        return text(NEW_ACCOUNT_ID);
    }
}