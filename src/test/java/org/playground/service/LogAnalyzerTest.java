package org.playground.service;

import junit.framework.TestCase;
import org.playground.domain.FailedLogin;

import java.util.HashMap;
import java.util.Map;

public class LogAnalyzerTest extends TestCase {
    private static final long ONE_MINUTE_IN_MILLIS = 60*1000; // 1 minute
    private static final long FIVE_MINUTES_TTL = 5*ONE_MINUTE_IN_MILLIS; //5 minutes
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    public void testParseLineHappyFlowSimple() throws Exception {
        
        String bugsBunnyIp = "30.212.19.124";
        long firstAttemptTimestamp = 1336111000;

        Map<String, FailedLogin> failedLoginStore = new HashMap<>();
        LogAnalyzer analyzer = new IntruderLogAnalyzer(failedLoginStore, FIVE_MINUTES_TTL,MAX_FAILED_LOGIN_ATTEMPTS);

        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp)));
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp+ONE_MINUTE_IN_MILLIS)));
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp+2*ONE_MINUTE_IN_MILLIS)));
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp+3*ONE_MINUTE_IN_MILLIS)));
        String result = analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp, firstAttemptTimestamp + 4 * ONE_MINUTE_IN_MILLIS));
        assertEquals(bugsBunnyIp,result);
    }

    public void testParseLineHappyFlowSimpleExpireFirstTTL() throws Exception {

        String bugsBunnyIp = "30.212.19.124";
        long firstAttemptTimestamp = 1336111000;

        Map<String, FailedLogin> failedLoginStore = new HashMap<>();
        LogAnalyzer analyzer = new IntruderLogAnalyzer(failedLoginStore, FIVE_MINUTES_TTL,MAX_FAILED_LOGIN_ATTEMPTS);

        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp)));
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp+10*ONE_MINUTE_IN_MILLIS))); // first + 10min
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp+11*ONE_MINUTE_IN_MILLIS))); // first + 11 min
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp,firstAttemptTimestamp+12*ONE_MINUTE_IN_MILLIS))); // first + 12 min
        assertNull(analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp, firstAttemptTimestamp + 13* ONE_MINUTE_IN_MILLIS))); // first + 13 min
        String result = analyzer.parseLine(getFailedLoginWithIPAndTimestamp(bugsBunnyIp, firstAttemptTimestamp + 14* ONE_MINUTE_IN_MILLIS)); // +14 min
        assertEquals(bugsBunnyIp,result);
    }




    private String getAttempt(String ipAddress, long timestamp, boolean success, String someUser) {
        return String.format("%s,%d,%s,%s", ipAddress, timestamp, success ? "SUCCESS" : "FAILURE", someUser);
    }

    private String getFailedLoginWithIPAndTimestamp(String ipAddress, long timestamp) {
        return getAttempt(ipAddress, timestamp, false, "Bugs.Bunny");
    }

}