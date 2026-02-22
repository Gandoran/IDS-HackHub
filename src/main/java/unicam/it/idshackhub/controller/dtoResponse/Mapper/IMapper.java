package unicam.it.idshackhub.controller.dtoResponse.Mapper;

import unicam.it.idshackhub.controller.dtoResponse.*;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.HackathonStaff;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.utils.Submission;

public interface IMapper {

    MessageResponseDTO toDto(Message message);

    SubmissionResponseDTO toDto(Submission submission);

    TeamResponseDTO toDto(Team team);

    HackathonResponseDTO toDto(Hackathon hackathon);

    HackathonTeamResponseDTO toDto(HackathonTeam hackathonTeam);

    UserResponseDTO toDto(User user);

    HackathonStaffResponseDTO toDto(HackathonStaff staff);
}