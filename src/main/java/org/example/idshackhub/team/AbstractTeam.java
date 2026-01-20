package org.example.idshackhub.team;

import java.util.ArrayList;

public abstract class AbstractTeam implements Context {

    private long id;
    private String name;
    private String description;
    private User leader;
    private List<User> members = new ArrayList<>();

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public User getLeader() {
        return leader;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public List<User> getMembers() {
        return members;
    }
}
