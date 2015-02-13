package org.playground.service;

import org.playground.domain.FailedLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Adi Ursu on 2/13/2015.
 */
public class FileWatcherService implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(FileWatcherService.class);
    
    private BufferedReader br;
    private boolean keepAlive;
    private final String filePath;
    private LogAnalyzer myLogAnalyzer;

    public FileWatcherService(String filePath, Map<String, FailedLogin> loginStore, long logEntryTTL, int maxFailedLoginAttempts) throws FileNotFoundException {
        this.filePath = filePath;
        this.br = new BufferedReader(new FileReader(filePath));
        this.keepAlive = true;
        this.myLogAnalyzer = new IntruderLogAnalyzer(loginStore,logEntryTTL, maxFailedLoginAttempts);
    }

    @Override
    public void run() {
        try {
            while (keepAlive) {
                try {
                    String line = br.readLine();
                    while ((line == null || line.isEmpty()) && keepAlive) {
                        Thread.sleep(100);
                        line = br.readLine();
                    }
                    if (!keepAlive) {
                        break;
                    }
                    LOG.debug("Processing line [{}] from file {}", line, filePath);
                    String ipaAddress = myLogAnalyzer.parseLine(line);
                    if (ipaAddress != null) {
                        //We're just logging, but here we would do something useful.
                        LOG.info("Found intruder from IP Address [{}]", ipaAddress);
                    }
                } catch (IOException | InterruptedException e) {
                    LOG.error("Error while watching/parsing file {}", filePath, e);
                }
            }
        } finally {
            try {
                br.close();
            } catch (IOException ignored) {
            }
        }
    }
}
