package org.playground.domain;

/**
 * Created by Adi Ursu on 2/12/2015.
 */
public class FailedLogin {
    private long firstAttemptTimestamp;
    private long lastAttemptTimestamp;
    private int attemptsCount;

    public FailedLogin(long timestamp) {
        this.firstAttemptTimestamp = timestamp;
        this.lastAttemptTimestamp = timestamp;
        this.attemptsCount = 1;
    }

    public long getFirstAttemptTimestamp() {
        return firstAttemptTimestamp;
    }

    public long getLastAttemptTimestamp() {
        return lastAttemptTimestamp;
    }

    public int incrementAttemptCounts(long timestamp) {
        lastAttemptTimestamp = timestamp;
        return ++attemptsCount;

    }

    @Override
    public String toString() {
        return "FailedLogin{" +
                "firstAttemptTimestamp=" + firstAttemptTimestamp +
                ", lastAttemptTimestamp=" + lastAttemptTimestamp +
                ", attemptsCount=" + attemptsCount +
                '}';
    }
}
