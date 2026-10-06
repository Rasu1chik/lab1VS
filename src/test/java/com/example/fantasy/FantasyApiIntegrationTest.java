package com.example.fantasy;

import com.example.fantasy.dto.ApiDto.*;
import com.example.fantasy.entity.*;
import com.example.fantasy.repository.*;
import tools.jackson.databind.json.JsonMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class FantasyApiIntegrationTest {
    @Container @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired MockMvc mvc;
    @Autowired JsonMapper json;
    @Autowired RealClubRepository clubs;
    @Autowired PlayerRepository players;
    @Autowired ManagerRepository managers;
    @Autowired FantasyTeamRepository teams;
    @Autowired GameweekRepository gameweeks;
    @Autowired LeagueRepository leagues;
    @Autowired TransferRepository transfers;
    @Autowired JdbcTemplate jdbc;

    @Test
    void seededPlayersHaveRealFullNames() {
        var names = players.findAll(org.springframework.data.domain.PageRequest.of(0, 15,
                        org.springframework.data.domain.Sort.by("id"))).stream()
                .map(player -> player.getFirstName() + " " + player.getLastName())
                .collect(java.util.stream.Collectors.toSet());
        assertThat(names).containsExactlyInAnyOrder(
                "David Raya", "Kepa Arrizabalaga", "William Saliba",
                "Virgil van Dijk", "Joe Gomez", "Jeremie Frimpong",
                "Levi Colwill", "Enzo Fernandez", "Moises Caicedo",
                "Phil Foden", "Rayan Cherki", "Tijjani Reijnders",
                "Yoane Wissa", "William Osula", "Harvey Barnes");
    }

    @Test
    void catalogCrudValidationAndPagination() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        var manager = createJson("/api/v1/managers", new ManagerInput("manager" + suffix, suffix + "@mail.test"), ManagerView.class);
        mvc.perform(get("/api/v1/managers/" + manager.id())).andExpect(status().isOk());
        mvc.perform(get("/api/v1/managers?size=51")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
        mvc.perform(get("/api/v1/managers?size=1")).andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"));
        mvc.perform(post("/api/v1/managers").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ManagerInput("", "bad"))))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").isArray());
        mvc.perform(post("/api/v1/managers").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ManagerInput("other" + suffix, suffix + "@mail.test"))))
                .andExpect(status().isConflict());
        mvc.perform(put("/api/v1/managers/" + manager.id()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new ManagerInput("renamed" + suffix, suffix + "@mail.test"))))
                .andExpect(status().isOk());

        var club = createJson("/api/v1/clubs", new ClubInput("Club " + suffix, suffix.substring(0, 3), "England"), ClubView.class);
        var player = createJson("/api/v1/players", new PlayerInput(club.id(), "Alex", "Smith",
                Position.MID, PlayerStatus.AVAILABLE, new BigDecimal("5.0")), PlayerView.class);
        mvc.perform(get("/api/v1/players?position=MID&size=1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.hasNext").exists()).andExpect(header().doesNotExist("X-Total-Count"));
        mvc.perform(get("/api/v1/players?size=51")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/v1/players/" + player.id()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new PlayerInput(club.id(), "Alex", "Brown",
                        Position.MID, PlayerStatus.AVAILABLE, new BigDecimal("6.0")))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.lastName").value("Brown"));
        mvc.perform(delete("/api/v1/players/" + player.id())).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/clubs/" + club.id())).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v1/managers/" + manager.id())).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/managers/" + manager.id())).andExpect(status().isNotFound());
    }

    @Test
    void teamTransferScoringAndLeagueFlow() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        var manager = createJson("/api/v1/managers", new ManagerInput("owner" + suffix, suffix + "@mail.test"), ManagerView.class);
        var roster = createRoster(suffix);
        List<Long> ids = roster.stream().map(Player::getId).toList();
        List<Long> starters = ids.subList(0, 11);
        var team = createJson("/api/v1/fantasy-teams", new TeamInput(manager.id(), "Winners", ids, starters, ids.get(0)), TeamView.class);
        assertThat(team.squad()).hasSize(15);
        assertThat(team.budget()).isEqualByComparingTo("25.0");
        mvc.perform(get("/api/v1/fantasy-teams?managerId=" + manager.id()))
                .andExpect(header().string("X-Total-Count", "1"));
        mvc.perform(patch("/api/v1/fantasy-teams/" + team.id()).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new TeamNameInput("New name"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("New name"));
        mvc.perform(put("/api/v1/fantasy-teams/" + team.id() + "/lineup").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new LineupInput(starters, ids.get(1)))))
                .andExpect(status().isOk());

        Player incoming = new Player();
        incoming.setClub(roster.get(0).getClub());
        incoming.setFirstName("Incoming"); incoming.setLastName(suffix);
        incoming.setPosition(Position.GK); incoming.setStatus(PlayerStatus.AVAILABLE);
        incoming.setPrice(new BigDecimal("6.0"));
        incoming = players.save(incoming);
        var transfer = createJson("/api/v1/fantasy-teams/" + team.id() + "/transfers",
                new TransferInput(ids.get(0), incoming.getId()), TransferView.class);
        assertThat(transfer.playerInId()).isEqualTo(incoming.getId());
        mvc.perform(get("/api/v1/fantasy-teams/" + team.id() + "/transfers"))
                .andExpect(status().isOk()).andExpect(header().doesNotExist("X-Total-Count"));
        mvc.perform(post("/api/v1/fantasy-teams/" + team.id() + "/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new TransferInput(ids.get(1), ids.get(2)))))
                .andExpect(status().isUnprocessableEntity());
        assertThat(teams.findById(team.id()).orElseThrow().getBudget()).isEqualByComparingTo("24.0");

        var league = createJson("/api/v1/leagues", new LeagueInput(manager.id(), "Friends " + suffix,
                LeagueType.PRIVATE, suffix), LeagueView.class);
        mvc.perform(post("/api/v1/leagues/" + league.id() + "/teams").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new JoinLeagueInput(team.id(), "wrong"))))
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(post("/api/v1/leagues/" + league.id() + "/teams").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new JoinLeagueInput(team.id(), suffix))))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/leagues/" + league.id() + "/standings"))
                .andExpect(status().isOk()).andExpect(header().string("X-Total-Count", "1"));

        var week = createJson("/api/v1/gameweeks", new GameweekInput(1000 + new Random().nextInt(100000),
                Instant.parse("2030-09-01T12:00:00Z")), GameweekView.class);
        mvc.perform(post("/api/v1/gameweeks/" + week.id() + "/start")).andExpect(status().isOk());
        mvc.perform(put("/api/v1/gameweeks/" + week.id() + "/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new PerformanceInput(ids.get(1), 90, 1, 0, true, 0, 0))))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/gameweeks/" + week.id() + "/performances"))
                .andExpect(header().string("X-Total-Count", "1"));
        mvc.perform(post("/api/v1/gameweeks/" + week.id() + "/finish"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("FINISHED"));
        mvc.perform(post("/api/v1/gameweeks/" + week.id() + "/finish")).andExpect(status().isConflict());
        assertThat(teams.findById(team.id()).orElseThrow().getTotalPoints()).isEqualTo(24);
        mvc.perform(delete("/api/v1/leagues/" + league.id() + "/teams/" + team.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void failedTransferLeavesSquadBudgetAndHistoryUntouched() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        var manager = createJson("/api/v1/managers", new ManagerInput("tight" + suffix,
                suffix + "@mail.test"), ManagerView.class);
        var roster = createRoster(suffix);
        for (Player player : roster) {
            player.setPrice(new BigDecimal("6.5"));
            players.save(player);
        }
        List<Long> ids = roster.stream().map(Player::getId).toList();
        var team = createJson("/api/v1/fantasy-teams", new TeamInput(manager.id(), "Tight budget",
                ids, ids.subList(0, 11), ids.get(0)), TeamView.class);
        assertThat(team.budget()).isEqualByComparingTo("2.5");
        Player expensive = new Player();
        expensive.setClub(roster.get(0).getClub());
        expensive.setFirstName("Expensive"); expensive.setLastName(suffix);
        expensive.setPosition(Position.GK); expensive.setStatus(PlayerStatus.AVAILABLE);
        expensive.setPrice(new BigDecimal("15.0"));
        expensive = players.save(expensive);
        mvc.perform(post("/api/v1/fantasy-teams/" + team.id() + "/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new TransferInput(ids.get(0), expensive.getId()))))
                .andExpect(status().isUnprocessableEntity());
        assertThat(teams.findById(team.id()).orElseThrow().getBudget()).isEqualByComparingTo("2.5");
        assertThat(transfers.findByTeamId(team.id(), org.springframework.data.domain.PageRequest.of(0, 10))
                .getContent()).isEmpty();
        assertThat(jdbc.queryForObject("select count(*) from squad_players where fantasy_team_id = ? and player_id = ?",
                Integer.class, team.id(), ids.get(0))).isEqualTo(1);
        assertThat(jdbc.queryForObject("select count(*) from squad_players where fantasy_team_id = ? and player_id = ?",
                Integer.class, team.id(), expensive.getId())).isZero();
        assertThat(jdbc.queryForObject("select position from players where id = ?", String.class, ids.get(0)))
                .isEqualTo("GK");
    }

    @Test
    void finishingScoresTeamsBeyondFirstPage() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        var manager = createJson("/api/v1/managers", new ManagerInput("batch" + suffix,
                suffix + "@mail.test"), ManagerView.class);
        var roster = createRoster(suffix);
        List<Long> ids = roster.stream().map(Player::getId).toList();
        Long lastTeamId = null;
        for (int i = 0; i < 51; i++) {
            var team = createJson("/api/v1/fantasy-teams", new TeamInput(manager.id(),
                    "Team " + i, ids, ids.subList(0, 11), ids.get(0)), TeamView.class);
            lastTeamId = team.id();
        }
        mvc.perform(get("/api/v1/fantasy-teams?managerId=" + manager.id() + "&size=50"))
                .andExpect(status().isOk()).andExpect(header().string("X-Total-Count", "51"))
                .andExpect(jsonPath("$.length()").value(50));
        var week = createJson("/api/v1/gameweeks", new GameweekInput(1000 + new Random().nextInt(100000),
                Instant.parse("2030-10-01T12:00:00Z")), GameweekView.class);
        mvc.perform(post("/api/v1/gameweeks/" + week.id() + "/start")).andExpect(status().isOk());
        mvc.perform(put("/api/v1/gameweeks/" + week.id() + "/performances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(new PerformanceInput(ids.get(0), 90, 0, 0, false, 0, 0))))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/gameweeks/" + week.id() + "/finish")).andExpect(status().isOk());
        assertThat(teams.findById(lastTeamId).orElseThrow().getTotalPoints()).isEqualTo(4);
    }

    private List<Player> createRoster(String suffix) {
        var clubList = clubs.findAll().stream().limit(5).toList();
        Position[] positions = {Position.GK,Position.GK,Position.DEF,Position.DEF,Position.DEF,
                Position.DEF,Position.DEF,Position.MID,Position.MID,Position.MID,Position.MID,
                Position.MID,Position.FWD,Position.FWD,Position.FWD};
        List<Player> result = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Player player = new Player();
            player.setClub(clubList.get(i / 3));
            player.setFirstName("Player"); player.setLastName(suffix + i);
            player.setPosition(positions[i]); player.setStatus(PlayerStatus.AVAILABLE);
            player.setPrice(new BigDecimal("5.0"));
            result.add(players.save(player));
        }
        return result;
    }

    private <T> T createJson(String path, Object body, Class<T> type) throws Exception {
        var result = mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsBytes(body))).andExpect(status().isCreated())
                .andExpect(header().exists("Location")).andReturn();
        return json.readValue(result.getResponse().getContentAsByteArray(), type);
    }
}
