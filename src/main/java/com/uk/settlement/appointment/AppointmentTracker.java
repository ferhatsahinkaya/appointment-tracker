package com.uk.settlement.appointment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.uk.settlement.config.domain.Config;
import com.uk.settlement.domain.Appointment;
import com.uk.settlement.mail.EmailSender;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.LocalDateTime.parse;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.groupingBy;

public class AppointmentTracker {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) ->
                            parse(json.getAsJsonPrimitive().getAsString()))
            .create();
    private final Config config;
    private final EmailSender emailSender;

    public AppointmentTracker(final Config config) {
        this.config = config;
        this.emailSender = new EmailSender(config);
    }

    public void start() {
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
                .ignoring(RuntimeException.class);

        final AtomicReference<String> lastAppointments = new AtomicReference<>("");

        try {
            wait.until(d -> {
                d.navigate().refresh();

                final List<WebElement> appointments = d.findElements(By.name("appointment"));
                final Map<LocalDate, List<Appointment>> groupedAppointments = appointments
                        .stream()
                        .map(c -> c.getAttribute("value"))
                        .map(c -> GSON.fromJson(c, Appointment.class))
                        .collect(groupingBy(appointment -> appointment.startTime().toLocalDate()));

                StringBuilder builder = new StringBuilder();
                groupedAppointments.entrySet()
                        .stream()
                        .sorted(comparingByKey())
                        .forEach(appointment -> {
                            builder.append(appointment.getKey().toString());
                            builder.append("\n");
                            appointment
                                    .getValue()
                                    .forEach(appt -> builder
                                            .append(appt.startTime().toLocalTime())
                                            .append(" (")
                                            .append(appt.calendarType())
                                            .append(")")
                                            .append("\n"));
                            builder.append("\n");
                        });

                final String availableAppointments = builder.toString();

                if (!lastAppointments.get().equals(availableAppointments)) {
                    emailSender.sendEmail(builder.toString());
                    lastAppointments.set(availableAppointments);
                }
                throw new RuntimeException();
            });
        } finally {
            driver.quit();
        }
    }
}
