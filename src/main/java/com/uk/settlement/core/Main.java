package com.uk.settlement.core;

import com.uk.settlement.appointment.AppointmentTracker;
import com.uk.settlement.config.domain.Config;

import static com.uk.settlement.config.reader.JsonConfigReader.readConfig;

public class Main {

    public static void main(String[] args) {
        new AppointmentTracker(readConfig("config.json", Config.class))
                .start();
    }
}
