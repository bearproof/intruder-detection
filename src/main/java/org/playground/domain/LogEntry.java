package org.playground.domain;

/**
 * Created by Adi Ursu on 2/12/2015.
 */
public class LogEntry {
    private final String ipAddress;
    private final boolean loginSuccessful;
    private final long loginTimestamp;

    public LogEntry(String ipAddress, boolean loginSuccessful, long loginTimestamp) {
        this.ipAddress = ipAddress;
        this.loginSuccessful = loginSuccessful;
        this.loginTimestamp = loginTimestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public long getLoginTimestamp() {
        return loginTimestamp;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAddress='" + ipAddress + '\'' +
                ", loginSuccessful=" + loginSuccessful +
                ", loginTimestamp=" + loginTimestamp +
                '}';
    }
}
