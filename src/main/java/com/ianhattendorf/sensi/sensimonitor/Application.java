package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import com.ianhattendorf.sensiapi.SensiApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner demo(StatusRepository statusRepository, SensiApi sensiApi, ExecutorService executor,
                                  BackOff backOff) {
        return new SensiCommandLineRunner(statusRepository, sensiApi, executor, backOff);
    }

    @Bean
    public BackOff backOff() {
        ExponentialBackOff backOff = new ExponentialBackOff(TimeUnit.SECONDS.toMillis(15), 2);
        backOff.setMaxInterval(TimeUnit.MINUTES.toMillis(5));
        return backOff;
    }

    @Bean
    public ExecutorService executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    public SensiApi sensiApi(@Value("${sensi.username}") String username, @Value("${sensi.password}") String password) {
        return new SensiApi.Builder()
                .setUsername(username)
                .setPassword(password)
                .build();
    }
}
