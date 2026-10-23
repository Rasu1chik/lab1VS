package com.example.fantasy;

import com.example.fantasy.entity.PlayerPerformance;
import com.example.fantasy.entity.Position;
import com.example.fantasy.service.PointsCalculator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PointsCalculatorTest {
    private final PointsCalculator calculator = new PointsCalculator();

    @Test
    void scoresGoalsByPosition() {
        var performance = performance(90);
        performance.setGoals(1);
        performance.setAssists(1);
        performance.setCleanSheet(true);
        assertThat(calculator.calculate(Position.GK, performance)).isEqualTo(15);
        assertThat(calculator.calculate(Position.DEF, performance)).isEqualTo(15);
        assertThat(calculator.calculate(Position.MID, performance)).isEqualTo(11);
        assertThat(calculator.calculate(Position.FWD, performance)).isEqualTo(9);
    }

    @Test
    void minutesAndCardsHaveBoundaries() {
        assertThat(calculator.calculate(Position.DEF, performance(0))).isZero();
        assertThat(calculator.calculate(Position.DEF, performance(59))).isEqualTo(1);
        var performance = performance(60);
        performance.setCleanSheet(true);
        performance.setYellowCards(1);
        performance.setRedCards(1);
        assertThat(calculator.calculate(Position.DEF, performance)).isEqualTo(2);
    }

    private PlayerPerformance performance(int minutes) {
        var performance = new PlayerPerformance();
        performance.setMinutesPlayed(minutes);
        return performance;
    }
}
