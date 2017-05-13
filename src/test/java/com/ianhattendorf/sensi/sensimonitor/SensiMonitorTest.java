package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensiapi.SensiApi;
import com.ianhattendorf.sensi.sensiapi.response.data.OperationalStatus;
import com.ianhattendorf.sensi.sensiapi.response.data.Temperature;
import com.ianhattendorf.sensi.sensimonitor.domain.Status;
import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.*;

public final class SensiMonitorTest {

    private SensiApi sensiApi;
    private StatusRepository statusRepository;
    private BackOff backOff;
    private SensiMonitor sensiMonitor;
    private OperationalStatus operationalStatus;

    @Before
    public void setUp() {
        sensiApi = mock(SensiApi.class);
        statusRepository = mock(StatusRepository.class);
        backOff = new FixedBackOff(0, 0);
        sensiMonitor = new SensiMonitor(statusRepository, () -> sensiApi, backOff);
        operationalStatus = new OperationalStatus();
        operationalStatus.setTemperature(new Temperature(123, 50));
        operationalStatus.setBatteryVoltage(3210);
    }

    @Test
    public void testRun() throws ExecutionException, InterruptedException {
        when(sensiApi.start()).thenReturn(CompletableFuture.completedFuture(null));
        when(sensiApi.subscribe()).thenReturn(CompletableFuture.completedFuture(null));

        // register callback and save for later to call when poll is called
        @SuppressWarnings("unchecked")
        BiConsumer<String, OperationalStatus>[] callbacks = (BiConsumer<String, OperationalStatus>[]) new BiConsumer[1];
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            BiConsumer<String, OperationalStatus> callback =
                    (BiConsumer<String, OperationalStatus>) invocation.getArgumentAt(0, BiConsumer.class);
            callbacks[0] = callback;
            return null;
        }).when(sensiApi).registerCallback(any());

        // poll, calling callback twice and then interrupting
        @SuppressWarnings("unchecked")
        CompletableFuture<Void> future = (CompletableFuture<Void>) mock(CompletableFuture.class);
        when(future.get()).thenThrow(new InterruptedException());
        when(sensiApi.poll()).thenAnswer(invocation -> {
            callbacks[0].accept("icd", operationalStatus);
            return CompletableFuture.completedFuture(null);
        }).thenAnswer(invocation -> {
            callbacks[0].accept("icd", new OperationalStatus());
            return CompletableFuture.completedFuture(null);
        }).thenReturn(future);

        AtomicInteger statusId = new AtomicInteger();
        when(statusRepository.save(any(), anyString())).thenAnswer(invocation -> {
            Status status = invocation.getArgumentAt(0, Status.class);
            status.setId(statusId.incrementAndGet());
            return status;
        });
        when(sensiApi.disconnect()).thenReturn(CompletableFuture.completedFuture(null));
        when(statusRepository.findAllJoin()).thenReturn(Collections.emptyList());

        sensiMonitor.run();

        verify(sensiApi).start();
        verify(sensiApi).subscribe();
        verify(sensiApi).registerCallback(any());
        verify(sensiApi, times(3)).poll();
        verify(sensiApi).disconnect();
        verify(statusRepository, times(2)).save(notNull(Status.class), eq("icd"));
        verify(statusRepository).findAllJoin();
    }
}
