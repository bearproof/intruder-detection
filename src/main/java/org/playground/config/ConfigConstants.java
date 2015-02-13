package org.playground.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adi Ursu on 2/13/2015.
 */
public class ConfigConstants {
    
    public static final String LOG_ENTRY_TTL_MS = "log.entry.ttl.ms";
    public static final String MAX_FAILED_LOGIN_ATTEMPTS = "max.failed.login.attempts";
    //Cleaning service
    public static final String CLEANING_SERVICE_INITIAL_DELAY = "cleaning.service.initial.delay";
    public static final String CLEANING_SERVICE_PERIOD = "cleaning.service.period";
    
    private ConfigConstants() {
        //nothing to do here
    }
}
