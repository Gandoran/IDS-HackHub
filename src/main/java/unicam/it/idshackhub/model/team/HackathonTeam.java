package unicam.it.idshackhub.model.team;

import lombok.Getter;
import lombok.Setter;
import unicam.it.idshackhub.model.hackathon.Hackathon;

import java.util.Objects;


@Getter
@Setter
public class HackathonTeam extends AbstractTeam{
    private Team mainTeam;
    private Hackathon hackathonParticipation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        HackathonTeam that = (HackathonTeam) o;

        return Objects.equals(this.mainTeam, that.mainTeam) &&
                Objects.equals(this.hackathonParticipation, that.hackathonParticipation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainTeam, hackathonParticipation);
    }


}