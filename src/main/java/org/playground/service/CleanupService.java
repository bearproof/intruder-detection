package org.playground.service;

import org.playground.domain.FailedLogin;
import org.playground.utils.TimestampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Adi Ursu on 2/12/2015.
 * <p>
 * This class is designed to cleanup old entries from a failedLoginStore.<br>
 * <ol>
 * <li>It identifies the timestamp of the most recent failed login attempt in that map</li>
 * <li>It cleans up entries with a timestamp older than <b>latestLoginTimestamp</b> - <b>failedLoginTTL</b></li>
 * </ol>
 * </p>
 */
public class CleanupService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(CleanupService.class);

    private Map<String, FailedLogin> failedLoginStore;
    private long failedLoginTTL;


    public CleanupService(Map<String, FailedLogin> failedLoginStore, long failedLoginTTL) {
        this.failedLoginStore = failedLoginStore;
        this.failedLoginTTL = failedLoginTTL;
    }

    @Override
    public void run() {
        if (failedLoginStore == null || failedLoginStore.isEmpty()) {
            LOG.warn("An empty or null failedLoginStore was provided. Nothing to do. Returning,");
            return;
        }
        long latestFailedLoginAttempt = getLatestFailedLoginAttempt();

        Set<String> ipAddressesToRemove = new HashSet<>();
        for (Map.Entry<String, FailedLogin> entry : failedLoginStore.entrySet()) {
            String ipAddress = entry.getKey();
            FailedLogin failedLogin = entry.getValue();
            if (TimestampUtil.isOlderThan(failedLogin.getLastAttemptTimestamp(), latestFailedLoginAttempt, failedLoginTTL)) {
                LOG.info("Marking for removal IP address [{}].", ipAddress);
                ipAddressesToRemove.add(ipAddress);
            }
        }
        if (!ipAddressesToRemove.isEmpty()) {
            failedLoginStore.keySet().removeAll(ipAddressesToRemove);
            LOG.info("Evicted [{}] entries, new count is [{}].", ipAddressesToRemove.size(), failedLoginStore.size());
        }

    }


    private long getLatestFailedLoginAttempt() {
        long timestamp = 0;
        for (FailedLogin failedLogin : failedLoginStore.values()) {
            long lastAttemptTimestamp = failedLogin.getLastAttemptTimestamp();
            if (timestamp < lastAttemptTimestamp) {
                timestamp = lastAttemptTimestamp;
            }
        }
        return timestamp;
    }
}
