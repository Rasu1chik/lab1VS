package com.example.discoveryserver;
@org.springframework.boot.autoconfigure.SpringBootApplication
@org.springframework.cloud.netflix.eureka.server.EnableEurekaServer
public class DiscoveryServerApplication {
 public static void main(String[] args) { org.springframework.boot.SpringApplication.run(DiscoveryServerApplication.class, args); }
}
