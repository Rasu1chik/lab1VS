package com.example.fantasy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.cloud.openfeign.EnableFeignClients
public class FantasyFootballApplication {
    public static void main(String[] args) {
        SpringApplication.run(FantasyFootballApplication.class, args);
    }
}
