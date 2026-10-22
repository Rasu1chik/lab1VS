package com.example.fantasy.controller;
import com.example.fantasy.repository.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
@RestController
public class ManagerReferencesController {
 private final FantasyTeamRepository teams;private final LeagueRepository leagues;
 public ManagerReferencesController(FantasyTeamRepository teams,LeagueRepository leagues) {this.teams=teams;this.leagues=leagues;}
 @GetMapping("/internal/managers/{id}/references")
 public Mono<Boolean> references(@PathVariable Long id) {
  return Mono.fromCallable(()->teams.existsByManagerId(id)||leagues.existsByOwnerId(id)).subscribeOn(Schedulers.boundedElastic());
 }
}
