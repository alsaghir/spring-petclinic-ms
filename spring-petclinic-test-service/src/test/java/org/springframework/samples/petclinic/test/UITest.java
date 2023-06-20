package org.springframework.samples.petclinic.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

public class UITest {


    @Test
    public void whenRequestOwnersThenElementsRenderedSuccessfully() {
        // Given
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://localhost:7778/#/owners");

        // When
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        webDriverWait.pollingEvery(Duration.ofSeconds(5)).
                until(ExpectedConditions.presenceOfElementLocated(By.tagName("flt-glass-pane"))
                        .andThen(WebElement::getShadowRoot).andThen(sc -> sc.findElements(By.cssSelector("flt-span"))));
        Optional<WebElement> atleastOneOwner = webDriver.findElement(By.tagName("flt-glass-pane")).getShadowRoot()
                .findElements(By.cssSelector("flt-span")).stream().filter(we -> we.getText().equals("City")).findAny();

        // Then
        Assertions.assertNotNull(webDriver.getTitle());
        Assertions.assertTrue(atleastOneOwner.isPresent());

        webDriver.quit();
    }

    @Test
    public void whenRequestOwnersThenElementsRenderedSuccessfully() {
        // Given
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://localhost:7778/#/owners");

        // When
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        webDriverWait.pollingEvery(Duration.ofSeconds(5)).
                until(ExpectedConditions.presenceOfElementLocated(By.tagName("flt-glass-pane"))
                        .andThen(WebElement::getShadowRoot).andThen(sc -> sc.findElements(By.cssSelector("flt-span"))));
        Optional<WebElement> atleastOneOwner = webDriver.findElement(By.tagName("flt-glass-pane")).getShadowRoot()
                .findElements(By.cssSelector("flt-span")).stream().filter(we -> we.getText().equals("City")).findAny();

        // Then
        Assertions.assertNotNull(webDriver.getTitle());
        Assertions.assertTrue(atleastOneOwner.isPresent());

        webDriver.quit();
    }

}
