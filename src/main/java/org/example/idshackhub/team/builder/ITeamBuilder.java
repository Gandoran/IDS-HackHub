package org.example.idshackhub.team.builder;

import org.example.idshackhub.team.AbstractTeam;

public interface ITeamBuilder<V extends AbstractTeam, T extends ITeamBuilder<V, T>>  {
    T buildName(String name);
    T buildDescription(String description);
    T buildLeader(User leader);
    T buildMembers(List<User> members);
    V getTeam();
}
