###Intruder detection system

Imagine you are developing  a system whose users need to sign in with their username and password.
One of the requirements is a new application to detect attempts to hack the system. 

The system is recording activity log files and the new application will need to process these logs to identify any suspicious activity.

Write a Java program implementing the LogAnalyzer interface which defines the method 'parseLine'. The implementation must take one line of the log file at a time and return the IP address if any suspicious activity is detected or null if the activity seems to be normal.

package com.playground.detector;
public interface LogAnalyzer {
	public String parseLine(String line);
}
The parseLine method will be called each time a new log line is produced and the log lines are generated in chronological order.

The log lines will have the format:

	ip,date,action,username
Where 

-	ip:  is like  30.212.19.124
-	date: is in the epoch format like 1336129421
-	action:  can be SUCCESS or FAILURE
-	username: is a String like Thomas.Davenport

So a log line is like: 

	30.212.19.124,1336129421,SUCCESS,Some.User

The detection method will be to **identify a single IP address that has attempted a failed login 5 or more times within a 5 minute period in which case the suspicious IP has to be returned**.

The login page can generate around 500,000 failed signing per day so memory consumption should be considered and managed.

Try to apply all the best practices you would normally use when implementing "done" production code:
•	A well factored object oriented domain model
•	Testing
•	Clean code

You can use any testing and mocking frameworks, but please refrain from using any other framework (inversion of control, data stores, caches, ...).

Good luck!

