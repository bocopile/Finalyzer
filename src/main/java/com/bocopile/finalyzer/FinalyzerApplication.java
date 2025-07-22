package com.bocopile.finalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FinalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalyzerApplication.class, args);
    }

}
