package com.example.fantasy.client;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.*;
import feign.jackson.*;
import org.springframework.context.annotation.Bean;
public class FeignCodecConfig {
 @Bean Decoder decoder(ObjectMapper mapper) { return new JacksonDecoder(mapper); }
 @Bean Encoder encoder(ObjectMapper mapper) { return new JacksonEncoder(mapper); }
}
