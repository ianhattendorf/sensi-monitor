package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensimonitor.domain.Status;
import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import com.ianhattendorf.sensiapi.SensiApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner demo(StatusRepository statusRepository, SensiApi sensiApi) {
        return (args) -> {
            sensiApi.registerCallback(operationalStatus -> {
                log.debug("operationalStatus: {}", operationalStatus);
                Status status = new Status(operationalStatus);
                status = statusRepository.save(status);
                log.info("Saved status: {}", status);
            });
            sensiApi.start();
            sensiApi.subscribe();
            for (int i = 0; i < 5; ++i) {
                sensiApi.poll();
            }
            sensiApi.disconnect();
            log.info("Disconnected, all saved statuses:");
            statusRepository.findAll().forEach(s -> log.info("s: {}", s));
        };
    }

    @Bean
    public SensiApi sensiApi(@Value("${sensi.username}") String username, @Value("${sensi.password}") String password) {
        return new SensiApi.Builder()
                .setUsername(username)
                .setPassword(password)
                .build();
    }
}
