package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensimonitor.domain.Status;
import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import com.ianhattendorf.sensiapi.SensiApi;
import com.ianhattendorf.sensiapi.response.data.OperationalStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class SensiCommandLineRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SensiCommandLineRunner.class);

    private final StatusRepository statusRepository;
    private final SensiApi sensiApi;
    private final ExecutorService executor;

    SensiCommandLineRunner(StatusRepository statusRepository, SensiApi sensiApi, ExecutorService executor) {
        this.statusRepository = statusRepository;
        this.sensiApi = sensiApi;
        this.executor = executor;
    }

    @Override
    public void run(String... strings) throws Exception {
        sensiApi.registerCallback(this::statusCallback);
        sensiApi.start().thenRun(sensiApi::subscribe).get();
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    sensiApi.poll().get();
                } catch (InterruptedException e) {
                    log.trace("interrupted:", e);
                    return;
                } catch (ExecutionException e) {
                    log.error("polling exception:", e);
                }
            }
        });
        log.info("Running, press Ctrl+C to shutdown application");
        Thread.currentThread().join();
    }

    private void statusCallback(OperationalStatus operationalStatus) {
        log.debug("operationalStatus: {}", operationalStatus);
        Status status = statusRepository.save(new Status(operationalStatus));
        log.info("saved status: {}", status);
    }

    @PreDestroy
    public void preDestroy() throws ExecutionException, InterruptedException {
        log.info("shutting down ExecutorService...");
        executor.shutdownNow();
        executor.awaitTermination(45, TimeUnit.SECONDS);
        log.info("disconnecting...");
        sensiApi.disconnect().get();
        log.info("disconnected, all saved statuses:");
        // TODO remove once persisted outside h2
        statusRepository.findAll().forEach(s -> log.info("s: {}", s));
    }
}
