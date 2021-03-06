package com.ianhattendorf.sensi.sensimonitor;

import com.ianhattendorf.sensi.sensiapi.RetrofitSensiApi;
import com.ianhattendorf.sensi.sensimonitor.domain.StatusRepository;
import com.ianhattendorf.sensi.sensiapi.SensiApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;

import javax.inject.Provider;
import javax.sql.DataSource;
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
    public CommandLineRunner sensiRunner(SensiMonitor sensiMonitor, ExecutorService executorService) {
        return new SensiCommandLineRunner(sensiMonitor, executorService);
    }

    @Bean
    public SensiMonitor sensiMonitor(StatusRepository statusRepository, Provider<SensiApi> sensiApiProvider,
                                    BackOff backOff) {
        return new SensiMonitor(statusRepository, sensiApiProvider, backOff);
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
    @Profile("pg")
    @ConfigurationProperties(prefix="datasource.postgres")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Profile("h2")
    @ConfigurationProperties(prefix="datasource.h2")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public SensiApi sensiApi(@Value("${sensi.username}") String username, @Value("${sensi.password}") String password) {
        return new RetrofitSensiApi.Builder()
                .setUsername(username)
                .setPassword(password)
                .build();
    }
}
