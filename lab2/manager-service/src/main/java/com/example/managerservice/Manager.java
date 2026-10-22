package com.example.managerservice;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("managers")
public record Manager(@Id Long id,String username,String email,Instant createdAt) {}
