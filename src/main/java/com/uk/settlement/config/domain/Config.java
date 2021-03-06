package com.uk.settlement.config.domain;

import com.google.gson.annotations.SerializedName;

import java.time.temporal.ChronoUnit;

public class Config {

    @SerializedName("login-details")
    private LoginDetails loginDetails;

    @SerializedName("web-driver-details")
    private WebDriverDetails webDriverDetails;

    @SerializedName("polling-details")
    private PollingDetails pollingDetails;

    @SerializedName("email-details")
    private EmailDetails emailDetails;

    public EmailDetails emailDetails() {
        return emailDetails;
    }

    public LoginDetails loginDetails() {
        return loginDetails;
    }

    public WebDriverDetails webDriverDetails() {
        return webDriverDetails;
    }

    public PollingDetails pollingDetails() {
        return pollingDetails;
    }

    public class EmailDetails {
        @SerializedName("sender-username")
        private String senderUsername;

        @SerializedName("sender-password")
        private String senderPassword;

        @SerializedName("recipient-email-addresses")
        private String[] recipientEmailAddresses;

        @SerializedName("subject")
        private String subject;

        public String senderUsername() {
            return senderUsername;
        }

        public String senderPassword() {
            return senderPassword;
        }

        public String[] recipientEmailAddresses() {
            return recipientEmailAddresses;
        }

        public String subject() {
            return subject;
        }
    }

    public class PollingDetails {
        @SerializedName("frequency")
        private long frequency;

        @SerializedName("frequency-unit")
        private ChronoUnit frequencyUnit;

        @SerializedName("timeout")
        private long timeout;

        @SerializedName("timeout-unit")
        private ChronoUnit timeoutUnit;

        public long frequency() {
            return frequency;
        }

        public ChronoUnit frequencyUnit() {
            return frequencyUnit;
        }

        public long timeout() {
            return timeout;
        }

        public ChronoUnit timeoutUnit() {
            return timeoutUnit;
        }
    }

    public class WebDriverDetails {
        @SerializedName("path")
        private String path;

        public String path() {
            return path;
        }
    }

    public class LoginDetails {
        @SerializedName("url")
        private String url;

        @SerializedName("password")
        private String password;

        public String url() {
            return url;
        }

        public String password() {
            return password;
        }
    }
}