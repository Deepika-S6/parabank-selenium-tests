package com.parabank.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountsOverviewPage extends BasePage {

    private static final By ACCOUNT_TABLE = By.id("accountTable");
    private static final By ACCOUNT_LINKS = By.cssSelector("#accountTable tbody tr td:first-child a");
    private static final By ACCOUNT_ROWS = By.cssSelector("#accountTable tbody tr");

    public AccountsOverviewPage(WebDriver driver) {
        super(driver);
    }

    public AccountsOverviewPage waitUntilLoaded() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_TABLE));
        return this;
    }

    /** Waits for the AJAX-filled rows; returns false if no account row ever appears. */
    private boolean waitForAccountRows() {
        try {
            waitUntilLoaded();
            wait.until(d -> !d.findElements(ACCOUNT_LINKS).isEmpty());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public List<String> getAccountIds() {
        List<String> ids = new ArrayList<>();
        if (!waitForAccountRows()) {
            return ids;
        }
        for (WebElement link : driver.findElements(ACCOUNT_LINKS)) {
            ids.add(link.getText().trim());
        }
        return ids;
    }

    public BigDecimal getBalance(String accountId) {
        if (!waitForAccountRows()) {
            throw new IllegalStateException("No accounts shown on Accounts Overview");
        }
        for (WebElement row : driver.findElements(ACCOUNT_ROWS)) {
            List<WebElement> links = row.findElements(By.cssSelector("td:first-child a"));
            if (!links.isEmpty() && links.get(0).getText().trim().equals(accountId)) {
                String raw = row.findElements(By.tagName("td")).get(1).getText();
                return new BigDecimal(raw.replaceAll("[^0-9.\\-]", ""));
            }
        }
        throw new IllegalStateException("Account " + accountId + " not found on overview");
    }
}