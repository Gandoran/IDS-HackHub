package unicam.it.idshackhub.model.team;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Team extends AbstractTeam {
    private String iban;
    private List<HackathonTeam> hackathonTeams;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team that = (Team) o;
        return Objects.equals(this.getLeader(), that.getLeader());
    }


}