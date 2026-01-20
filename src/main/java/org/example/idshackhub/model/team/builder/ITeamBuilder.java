package org.example.idshackhub.model.team.builder;

import org.example.idshackhub.model.team.AbstractTeam;
import org.example.idshackhub.model.user.User;

import java.util.List;

public interface ITeamBuilder<V extends AbstractTeam, T extends ITeamBuilder<V, T>>  {
    T buildName(String name);
    T buildDescription(String description);
    T buildLeader(User leader);
    T buildMembers(List<User> members);
    V getTeam();
}
