package org.playground.service;

import org.playground.config.ConfigConstants;
import org.playground.domain.FailedLogin;
import org.playground.domain.LogEntry;
import org.playground.utils.TimestampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adi Ursu on 2/12/2015.
 * The log lines will have the format:
 * ip,date,action,username
 * Where
 * •	ip:  is like  30.212.19.124
 * •	date: is in the epoch format like 1336129421
 * •	action:  can be SUCCESS or FAILURE
 * •	username: is a String like Thomas.Davenport
 * So a log line is like:
 * 30.212.19.124,1336129421,SUCCESS,Thomas.Davenport
 * <p/>
 * The detection method will be to identify a single IP address that has attempted a failed login 5 or more times within a 5 minute period in which case the suspicious IP has to be returned.
 */
public class IntruderLogAnalyzer implements LogAnalyzer {
    private static final Logger LOG = LoggerFactory.getLogger(IntruderLogAnalyzer.class);

    private static final Pattern LOG_LINE_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}\\d{1,3},\\d+,(SUCCESS|FAILURE),[a-zA-Z0-9\\.]+");
    private static final String LOG_ENTRY_DELIMITER = ",";
    private static final String SUCCESS_LOGIN_MARKER = "SUCCESS";
    private final Map<String, FailedLogin> failedLoginStore;
    private final long logEntryTTL;
    private final int maxFailedLoginAttempts;
    
    public IntruderLogAnalyzer(Map<String, FailedLogin> failedLoginStore, long logEntryTTL, int maxFailedLoginAttempts) {
        this.failedLoginStore = failedLoginStore;
        this.logEntryTTL = logEntryTTL;
        this.maxFailedLoginAttempts =maxFailedLoginAttempts;
    }

    @Override
    public String parseLine(String line) {
        if (!isLineValid(line)) {
            LOG.warn("Line [{}] is invalid, discarding", line);
            return null;
        }
        LogEntry logEntry = extractLogEntry(line);
        if (logEntry.isLoginSuccessful()) {
            return null;
        }
        String ipAddress = logEntry.getIpAddress();
        FailedLogin failedLogin = failedLoginStore.get(ipAddress);
        long currentAttemptTimestamp = logEntry.getLoginTimestamp();
        if (failedLogin == null) {
            LOG.debug("Found a new unsuccessful attempt from IP [{}]. Adding to watchlist.", ipAddress);
            failedLogin = new FailedLogin(currentAttemptTimestamp);
            failedLoginStore.put(ipAddress, failedLogin);
        } else {
            LOG.debug("Already watching IP address [{}].Updating with new info.", ipAddress);
            //check if older than TTL
            if (TimestampUtil.isOlderThan(failedLogin.getLastAttemptTimestamp(),currentAttemptTimestamp,logEntryTTL)){
                failedLoginStore.put(ipAddress,  new FailedLogin(currentAttemptTimestamp));
                return null;
            }
            int attemptsCount = failedLogin.incrementAttemptCounts(currentAttemptTimestamp);
            if (attemptsCount >= maxFailedLoginAttempts) {
                if (TimestampUtil.isOlderThan(failedLogin, logEntryTTL)) {
                    failedLoginStore.put(ipAddress,  new FailedLogin(currentAttemptTimestamp));
                } else {
                    LOG.debug("Intruder IP Address [{}] at timestamp [{}]", ipAddress, currentAttemptTimestamp);
                    return ipAddress;
                }
            }

        }

        return null;
    }

    boolean isLineValid(String line) {
        Matcher matcher = LOG_LINE_PATTERN.matcher(line);
        return matcher.matches();
    }

    LogEntry extractLogEntry(String line) {
        String[] items = line.split(LOG_ENTRY_DELIMITER);
        String ipAddress = items[0];
        long loginTimestamp = Long.valueOf(items[1]);
        boolean loginSuccessful = SUCCESS_LOGIN_MARKER.equals(items[2]);

        return new LogEntry(ipAddress, loginSuccessful, loginTimestamp);
    }
}
