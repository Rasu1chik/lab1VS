package com.example.fantasy.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="manager-service",configuration=FeignCodecConfig.class)
public interface ManagerClient {
 record ManagerRef(Long id) {}
 @GetMapping("/api/v1/managers/{id}") ManagerRef get(@PathVariable("id") Long id);
}
