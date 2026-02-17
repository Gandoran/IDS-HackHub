package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.CreateHackathonDTO;
import unicam.it.idshackhub.dto.CreateTeamDTO;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.service.SystemService;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemService systemService;

    @Autowired
    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @PostMapping("/teams")
    public ResponseEntity<?> createMainTeam(@RequestBody CreateTeamDTO dto) {
        Team team = systemService.createTeam(
                dto.leaderId(),
                dto.name(),
                dto.description(),
                dto.payPalAccount()
        );
        return ResponseEntity.ok("Main Team created with ID: " + team.getId());
    }

    @PostMapping("/hackathon")
    public ResponseEntity<?> createHackathon(@RequestBody CreateHackathonDTO dto) {
        TeamRules rules = new TeamRules(dto.maxTeams(), dto.minTeams(), dto.maxPlayers(), dto.minPlayers());
        Schedule schedule = new Schedule(dto.startReg(), dto.startEvent(), dto.endEvent(), dto.location());
        Hackathon hackathon = systemService.createHackathon(dto.organizerId(), dto.title(), dto.description(), dto.prize(), rules, schedule);
        return ResponseEntity.ok("Hackathon created with ID: " + hackathon.getId());
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getAllTeams() {
        List<Team> teams = systemService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/teams/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        Team team = systemService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    @GetMapping("/hackathons")
    public ResponseEntity<List<Hackathon>> getAllHackathons() {
        List<Hackathon> hackathons = systemService.getAllHackathons();
        return ResponseEntity.ok(hackathons);
    }

    @GetMapping("/hackathons/{id}")
    public ResponseEntity<Hackathon> getHackathonById(@PathVariable Long id) {
        Hackathon hackathon = systemService.getHackathonById(id);
        return ResponseEntity.ok(hackathon);
    }


}