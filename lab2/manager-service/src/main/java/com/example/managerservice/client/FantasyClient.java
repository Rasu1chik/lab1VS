package com.example.managerservice.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
@FeignClient(name="fantasy-service",configuration=FeignCodecConfig.class)
public interface FantasyClient {
 
 @GetMapping("/internal/managers/{id}/references") Boolean references(@PathVariable("id") Long id);
}
