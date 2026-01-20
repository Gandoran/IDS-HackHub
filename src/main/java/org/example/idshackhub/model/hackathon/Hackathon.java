package org.example.idshackhub.model.hackathon;

import java.util.ArrayList;
import java.util.List;

public class Hackathon {

    public Hackathon(){}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TeamRules getRules() {
        return rules;
    }

    public void setRules(TeamRules rules) {
        this.rules = rules;
    }

    public HackathonStaff getStaff() {
        return staff;
    }

    public void setStaff(HackathonStaff staff) {
        this.staff = staff;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public List<HackathonTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<HackathonTeam> teams) {
        this.teams = teams;
    }

    private Long id;
    private String title;
    private String description;
    private TeamRules rules;
    private HackathonStaff staff = new HackathonStaff();
    private Schedule schedule;
    List<HackathonTeam> teams = new ArrayList<>();

}
