package com.ianhattendorf.sensi.sensimonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class SensiCommandLineRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SensiCommandLineRunner.class);

    private final SensiMonitor sensiMonitor;
    private final ExecutorService executor;

    public SensiCommandLineRunner(SensiMonitor sensiMonitor, ExecutorService executor) {
        this.sensiMonitor = sensiMonitor;
        this.executor = executor;
    }

    @Override
    public void run(String... strings) throws Exception {
        executor.submit(sensiMonitor::run);
        log.info("Running, press Ctrl+C to shutdown application");
        Thread.currentThread().join();
    }

    @PreDestroy
    public void preDestroy() throws ExecutionException, InterruptedException {
        log.info("shutting down ExecutorService...");
        executor.shutdownNow();
        executor.awaitTermination(45, TimeUnit.SECONDS);
        log.info("disconnected, all saved statuses:");
    }
}
