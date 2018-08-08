package com.uk.settlement.domain;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Appointment {
    @SerializedName("startTime")
    LocalDateTime startTime;

    @SerializedName("calendarType")
    CalendarType calendarType;

    public LocalDateTime startTime() {
        return startTime;
    }

    public CalendarType calendarType() {
        return calendarType;
    }

    enum CalendarType {
        @SerializedName("incountryprimetime")
        PrimeTime,
        @SerializedName("")
        NormalTime
    }
}
