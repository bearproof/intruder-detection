package org.playground.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.playground.domain.FailedLogin;

import java.util.HashMap;
import java.util.Map;

public class CleanupServiceTest extends TestCase {

    @Test
    public void testCleanUpEntries() throws InterruptedException {
        long failedLoginTTL = 1000;
        final FailedLogin attempt1 = new FailedLogin(1236111000);
        final FailedLogin attempt2 = new FailedLogin(1236112000);
        final FailedLogin attempt3 = new FailedLogin(1236113000);
        Map<String,FailedLogin> failedLoginStore = new HashMap<String,FailedLogin>() {{
            put("192.168.1.0",attempt1);
            put("192.168.1.1", attempt2);
            put("192.168.1.2", attempt3);
        }};
        
        CleanupService cleanupService = new CleanupService(failedLoginStore, failedLoginTTL);
        Thread cleanEryThang = new Thread(cleanupService);
        cleanEryThang.start();
        
        cleanEryThang.join();
        assertEquals(2,failedLoginStore.size());
    }

    @Test
    public void testWorksWithNullFailedLoginStore() throws InterruptedException {
        Map<String,FailedLogin> failedLoginStore = null;

        CleanupService cleanupService = new CleanupService(failedLoginStore, 0);
        Thread cleanEryThang = new Thread(cleanupService);
        cleanEryThang.start();

        cleanEryThang.join();
    }

    @Test
    public void testWorksWithEmptyFailedLoginStore() throws InterruptedException {
        Map<String,FailedLogin> failedLoginStore = new HashMap<>();

        CleanupService cleanupService = new CleanupService(failedLoginStore, 0);
        Thread cleanEryThang = new Thread(cleanupService);
        cleanEryThang.start();

        cleanEryThang.join();
    }

}