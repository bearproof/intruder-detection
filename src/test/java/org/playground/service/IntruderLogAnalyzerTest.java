package org.playground.service;

import junit.framework.TestCase;
import org.playground.domain.FailedLogin;
import org.playground.domain.LogEntry;

import java.util.HashMap;

public class IntruderLogAnalyzerTest extends TestCase {

    private static final long ONE_MINUTE_IN_MILLIS = 60*1000; // 1 minute
    private static final long LOG_ENTRY_TTL = 5*ONE_MINUTE_IN_MILLIS; //5 minutes
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    
    private IntruderLogAnalyzer intruderLogAnalyzer;

    @Override
    public void setUp() throws Exception {
        intruderLogAnalyzer = new IntruderLogAnalyzer(new HashMap<String, FailedLogin>(),LOG_ENTRY_TTL,MAX_FAILED_LOGIN_ATTEMPTS);
    }

    public void testParseLine() throws Exception {

    }

    public void testIsLineValid() throws Exception {
        String validLine = "30.212.19.124,1336129421,SUCCESS,Thomas.Davenport";
        assertTrue(intruderLogAnalyzer.isLineValid(validLine));

    }

    public void testIsLineInvalid() throws Exception {
        String validLine = "30.212.19.,1336129421,SUCCESS,Thomas.Davenport";
        assertFalse(intruderLogAnalyzer.isLineValid(validLine));
    }

    public void testExtractLogEntry() throws Exception {
        String validLine = "30.212.19.124,1336129421,SUCCESS,Thomas.Davenport";
        LogEntry logEntry = intruderLogAnalyzer.extractLogEntry(validLine);
        
        assertEquals("30.212.19.124",logEntry.getIpAddress());
        assertTrue(logEntry.isLoginSuccessful());
        assertEquals(1336129421,logEntry.getLoginTimestamp());
    }

}