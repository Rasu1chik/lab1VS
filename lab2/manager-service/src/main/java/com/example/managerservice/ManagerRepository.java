package com.example.managerservice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
public interface ManagerRepository extends ReactiveCrudRepository<Manager,Long> {
 @Query("SELECT * FROM managers ORDER BY id LIMIT :size OFFSET :offset")
 Flux<Manager> page(int size,long offset);
}
