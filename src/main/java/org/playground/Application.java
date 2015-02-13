package org.playground;

import org.playground.config.ConfigConstants;
import org.playground.domain.FailedLogin;
import org.playground.service.CleanupService;
import org.playground.service.FileWatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Adi U on 2/11/2015.
 */
public class Application {

    static final Logger LOG = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) throws IOException, InterruptedException {

        //load properties
        Properties configProp = new Properties();
        ClassLoader loader = Application.class.getClassLoader();
        InputStream stream = loader.getResourceAsStream("config.properties");
        configProp.load(stream);


        Set<String> logFiles = new HashSet<>();
        logFiles.add(loader.getResource("sample.log").getPath().toString());


        long expireTTL = Long.parseLong(configProp.getProperty(ConfigConstants.LOG_ENTRY_TTL_MS));
        long cleaningServicePeriod = Long.parseLong(configProp.getProperty(ConfigConstants.CLEANING_SERVICE_PERIOD));
        long cleaningServiceInitialDelay = Long.parseLong(configProp.getProperty(ConfigConstants.CLEANING_SERVICE_INITIAL_DELAY));
        int maxFailedLoginAttempts = Integer.parseInt(configProp.getProperty(ConfigConstants.MAX_FAILED_LOGIN_ATTEMPTS));

        //Create empty thread safe login store
        Map<String, FailedLogin> loginStore = new ConcurrentHashMap<>();

        //Start the cleanup service
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        CleanupService cleanupService = new CleanupService(loginStore, expireTTL);
        scheduler.scheduleAtFixedRate(cleanupService, cleaningServiceInitialDelay, cleaningServicePeriod, SECONDS);

        //Start log watchers, one thread per log file
        ExecutorService executorService = Executors.newFixedThreadPool(logFiles.size());
        for (String logFile : logFiles) {
            try {
                executorService.execute(new FileWatcherService(logFile, loginStore, expireTTL, maxFailedLoginAttempts));
            } catch (FileNotFoundException e) {
                LOG.error("Error while watching file {}", logFile, e);
            }
        }

    }
}
