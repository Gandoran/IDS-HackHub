package unicam.it.idshackhub.model.team;

import java.util.List;

public class Team extends AbstractTeam {
    private String iban;

    public void setIban(String iban) {
        this.iban = iban;
    }
    public String getIban() {
        return iban;
    }

    public void setHackathonTeams(List<HackathonTeam> hackathonTeams) {this.hackathonTeams = hackathonTeams;}
    public List<HackathonTeam> getHackathonTeams() {return hackathonTeams;}
    public HackathonTeam getHackathonTeamById(long id) {
        return hackathonTeams.stream()
                .filter(team -> team.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<HackathonTeam> hackathonTeams;
}