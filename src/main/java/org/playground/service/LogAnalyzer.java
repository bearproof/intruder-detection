package org.playground.service;

/**
 * Created by Adi U on 2/11/2015.
 */
public interface LogAnalyzer {

    /**
     * The parseLine method will be called each time a new log line is produced.
     * @param line A line of the log file. The lines are in the format<br>
     *             <b>ip,date,action,username</b><br>
     *             An example log line is: <b>30.212.19.124,1336129421,SUCCESS,Thomas.Davenport</b>
     * @return If an IP address has attempted a failed login 5 or more times within a 5 minute period returns the  IP else returns null.
     */
    public String parseLine(String line);
}
