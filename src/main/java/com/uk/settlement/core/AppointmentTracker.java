package com.uk.settlement.core;

import com.uk.settlement.config.domain.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static com.uk.settlement.config.reader.JsonConfigReader.readConfig;
import static java.time.LocalDateTime.now;
import static javax.swing.JOptionPane.showMessageDialog;

public class AppointmentTracker {
    public static void main(String[] args) {
        Config config = readConfig("config.json", Config.class);

        System.setProperty("webdriver.gecko.driver", config.webDriverDetails().path());

        WebDriver driver = new FirefoxDriver();

        driver.get(config.loginDetails().url());

        WebElement password = driver.findElement(By.id("password"));
        password.sendKeys(config.loginDetails().password());

        WebElement submit = driver.findElement(By.id("submit"));
        submit.click();

        (new WebDriverWait(driver, 10)).until(d -> d.getTitle().contains("Choose an appointment"));

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.of(config.pollingDetails().timeout(), config.pollingDetails().timeoutUnit()))
                .pollingEvery(Duration.of(config.pollingDetails().frequency(), config.pollingDetails().frequencyUnit()))
                .ignoring(AppointmentNotFoundException.class);

        try {
            wait.until(d -> {
                d.navigate().refresh();

                boolean appointmentUnavailableDisplayed = d.findElement(By.id("AppointmentBooking")).getText().contains("We do not have any appointments in the next 45 business days at your selected location.");
                boolean availableAppointmentsDisplayed = d.findElement(By.id("available-appointments")).isDisplayed();
                if (!availableAppointmentsDisplayed && appointmentUnavailableDisplayed) {
                    throw new AppointmentNotFoundException();
                }
                return true;
            });
            showMessageDialog(null, String.format("Appointment(s) found by %s", now()));
        } finally {
            driver.quit();
        }
    }

    private static class AppointmentNotFoundException extends RuntimeException {

    }
}
