package unicam.it.idshackhub.model.hackathon;

public class TeamRules {
    private int maxTeams;
    private int minTeams;
    private int maxPlayersPerTeam;
    private int minPlayersPerTeam;


    public TeamRules(int maxT, int minT, int maxP, int minP) {
        if(maxT<minT || maxP<minP) throw new IllegalArgumentException();
        this.maxTeams = maxT;
        this.minTeams = minT;
        this.maxPlayersPerTeam = maxP;
        this.minPlayersPerTeam = minP;
    }

}
