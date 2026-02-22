package unicam.it.idshackhub.controller.dtoResponse;

import unicam.it.idshackhub.model.hackathon.Schedule;
import unicam.it.idshackhub.model.hackathon.TeamRules;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;

import java.util.List;

public record HackathonResponseDTO(
        String title,
        String description,
        Double prize,
        TeamRules teamRules,
        HackathonStaffResponseDTO hackathonStaff,
        Schedule schedule,
        List<HackathonTeamResponseDTO> teams,
        List<SubmissionResponseDTO> submissions,
        HackathonStatus status,
        HackathonTeamResponseDTO winner
) {}
