package com.example.configserver;
@org.springframework.boot.autoconfigure.SpringBootApplication
@org.springframework.cloud.config.server.EnableConfigServer
public class ConfigServerApplication {
 public static void main(String[] args) { org.springframework.boot.SpringApplication.run(ConfigServerApplication.class, args); }
}
