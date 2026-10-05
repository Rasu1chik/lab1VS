package com.example.fantasy.service;

import com.example.fantasy.entity.PlayerPerformance;
import com.example.fantasy.entity.Position;
import org.springframework.stereotype.Component;

@Component
public class PointsCalculator {
    public int calculate(Position position, PlayerPerformance performance) {
        int minutes = performance.getMinutesPlayed();
        int points = minutes == 0 ? 0 : minutes < 60 ? 1 : 2;
        int goalPoints = switch (position) {
            case GK, DEF -> 6;
            case MID -> 5;
            case FWD -> 4;
        };
        points += performance.getGoals() * goalPoints + performance.getAssists() * 3;
        if (minutes >= 60 && performance.isCleanSheet()) {
            points += switch (position) {
                case GK, DEF -> 4;
                case MID -> 1;
                case FWD -> 0;
            };
        }
        return points - performance.getYellowCards() - performance.getRedCards() * 3;
    }
}
