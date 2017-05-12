package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensimonitor.domain.Status;
import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import com.ianhattendorf.sensiapi.SensiApi;
import com.ianhattendorf.sensiapi.response.data.OperationalStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;
import retrofit2.HttpException;

import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public final class SensiCommandLineRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SensiCommandLineRunner.class);

    private final StatusRepository statusRepository;
    private final SensiApi sensiApi;
    private final ExecutorService executor;
    private final BackOff backOff;

    SensiCommandLineRunner(StatusRepository statusRepository, SensiApi sensiApi, ExecutorService executor, BackOff backOff) {
        this.statusRepository = statusRepository;
        this.sensiApi = sensiApi;
        this.executor = executor;
        this.backOff = backOff;
    }

    @Override
    public void run(String... strings) throws Exception {
        sensiApi.registerCallback(this::statusCallback);
        executor.submit(() -> {
            try {
                init().get();
            } catch (InterruptedException e) {
                log.trace("interrupted:", e);
                return;
            } catch (ExecutionException e) {
                log.error("init exception:", e);
                // exit exceptionally
                throw new RuntimeException(e);
            }
            BackOffExecution backOffExecution = backOff.start();
            long nextBackOff = 0;
            while (!Thread.currentThread().isInterrupted() && nextBackOff != BackOffExecution.STOP) {
                try {
                    if (nextBackOff > 0) {
                        log.debug("sleeping for {} due to back-off", nextBackOff);
                        Thread.sleep(nextBackOff);
                        nextBackOff = 0;
                    }
                    sensiApi.poll().get();
                } catch (InterruptedException e) {
                    log.trace("interrupted, exiting", e);
                    return;
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof HttpException
                            && ((HttpException) (e.getCause())).code() == 403) {
                        // need to re-auth
                        try {
                            init().get();
                        } catch (InterruptedException e1) {
                            log.trace("reauth interrupted, exiting", e1);
                            return;
                        } catch (ExecutionException e1) {
                            nextBackOff = backOffExecution.nextBackOff();
                            log.error("reauth error, backing off for {}ms", nextBackOff, e1);
                        }
                    } else {
                        nextBackOff = backOffExecution.nextBackOff();
                        log.error("polling exception, backing off for {}ms", nextBackOff, e);
                    }
                }
            }
        });
        log.info("Running, press Ctrl+C to shutdown application");
        Thread.currentThread().join();
    }

    private CompletableFuture<Void> init() {
        return sensiApi.start().thenRun(sensiApi::subscribe);
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
