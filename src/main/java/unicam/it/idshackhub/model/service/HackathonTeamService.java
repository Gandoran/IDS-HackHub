package unicam.it.idshackhub.model.service;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;

import java.util.List;
import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

public class HackathonTeamService {
    public Submission postSubmission(User hackathonTeamLeader, String description, HackathonTeam team,Hackathon hackathon) {
        if(!checkPermission(hackathonTeamLeader,Permission.Can_Submit,team)) throw new RuntimeException("Permission denied");
        Submission submission = new Submission(description,team);
        team.setSubmission(submission);
        List<Submission> submissions = team.getHackathonParticipation().getSubmissions();
        submissions.removeIf(s -> s.getTeam().equals(team));
        submissions.add(submission);
        return submission;
    }
}
