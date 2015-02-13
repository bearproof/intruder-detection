package org.playground.utils;

import org.playground.domain.FailedLogin;

/**
 * Created by Adi Ursu on 2/13/2015.
 */
public class TimestampUtil {

    private TimestampUtil() {
    }

    public static boolean isOlderThan(FailedLogin failedLogin, long age) {
        return isOlderThan(failedLogin.getFirstAttemptTimestamp(), failedLogin.getLastAttemptTimestamp(), age);
        
    }
    
    public static boolean isOlderThan(long firstTimestamp, long lastTimestamp, long age) {
        return lastTimestamp - firstTimestamp > age;
    }
}
