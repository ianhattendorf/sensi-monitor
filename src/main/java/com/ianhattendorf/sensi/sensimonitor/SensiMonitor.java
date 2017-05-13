package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensimonitor.domain.Status;
import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import com.ianhattendorf.sensiapi.SensiApi;
import com.ianhattendorf.sensiapi.response.data.OperationalStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;
import retrofit2.HttpException;

import javax.inject.Provider;
import java.util.concurrent.ExecutionException;

public final class SensiMonitor {
    private static final Logger log = LoggerFactory.getLogger(SensiMonitor.class);

    private final StatusRepository statusRepository;
    private final Provider<SensiApi> sensiApiProvider;
    private final BackOff backOff;

    SensiMonitor(StatusRepository statusRepository, Provider<SensiApi> sensiApiProvider, BackOff backOff) {
        this.statusRepository = statusRepository;
        this.sensiApiProvider = sensiApiProvider;
        this.backOff = backOff;
    }

    public void run() {
        SensiApi sensiApi;
        try {
            sensiApi = getSensiApi();
        } catch (InterruptedException e) {
            log.info("interrupted, exiting");
            return;
        } catch (ExecutionException e) {
            log.error("getSensiApi exception:", e);
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
                log.info("interrupted, exiting");
                nextBackOff = BackOffExecution.STOP;
            } catch (ExecutionException e) {
                if (e.getCause() instanceof HttpException) {
                    HttpException cause = (HttpException) e.getCause();
                    // handle here for now, can't use Retrofit Authenticator because service sometimes returns
                    // 403 instead of 401
                    if (cause.code() == 401 || cause.code() == 403) {
                        try {
                            log.info("received {} code, need to reauth", cause.code());
                            sensiApi = getSensiApi();
                        } catch (InterruptedException e1) {
                            log.info("reauth interrupted, exiting");
                            nextBackOff = BackOffExecution.STOP;
                        } catch (ExecutionException e1) {
                            nextBackOff = backOffExecution.nextBackOff();
                            log.error("reauth error, backing off for {}ms", nextBackOff, e1);
                        }
                    } else {
                        nextBackOff = backOffExecution.nextBackOff();
                        log.error("unhandled HttpException code {}, backing off for {}ms",
                                cause.code(), nextBackOff, cause);
                    }
                } else {
                    nextBackOff = backOffExecution.nextBackOff();
                    log.error("polling exception, backing off for {}ms", nextBackOff, e);
                }
            }
        }
        log.info("disconnecting...");
        try {
            sensiApi.disconnect().get();
        } catch (InterruptedException e) {
            log.info("disconnect interrupted, exiting");
        } catch (ExecutionException e) {
            log.error("error disconnecting", e);
        }
        // TODO remove once persisted outside h2
        statusRepository.findAllJoin().forEach(s -> log.info("s: {}", s));
    }

    private SensiApi getSensiApi() throws ExecutionException, InterruptedException {
        SensiApi sensiApi = sensiApiProvider.get();
        sensiApi.start()
                .thenRun(() -> sensiApi.registerCallback(this::statusCallback))
                .thenRun(sensiApi::subscribe).get();
        return sensiApi;
    }

    private void statusCallback(String thermostatICD, OperationalStatus operationalStatus) {
        log.debug("operationalStatus['{}']: {}", thermostatICD, operationalStatus);
        Status status = statusRepository.save(new Status(operationalStatus), thermostatICD);
        log.info("saved status: {}", status);
    }
}
