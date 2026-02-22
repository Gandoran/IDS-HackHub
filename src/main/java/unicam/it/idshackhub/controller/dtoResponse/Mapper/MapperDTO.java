package unicam.it.idshackhub.controller.dtoResponse.Mapper;

import org.springframework.stereotype.Component;
import unicam.it.idshackhub.controller.dtoResponse.*;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.HackathonStaff;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.utils.Submission;

import java.util.Collections;

@Component
public class MapperDTO implements IMapper {

    @Override
    public MessageResponseDTO toDto(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                toDto(message.getSender()),
                message.getRecipient() != null ?toDto(message.getRecipient()) :null,
                message.getContent(),
                message.getType(),
                message.getActionStatus()
        );
    }

    @Override
    public SubmissionResponseDTO toDto(Submission submission) {
        return new SubmissionResponseDTO(
                submission.getId(),
                submission.getDescription(),
                submission.getVote(),
                submission.getSubmissionDate()
        );
    }

    @Override
    public TeamResponseDTO toDto(Team team) {
        return new TeamResponseDTO(
                team.getName(),
                team.getDescription(),
                toDto(team.getLeader()),
                team.getMembers() != null ? team.getMembers().stream().map(this::toDto).toList() : Collections.emptyList(),
                team.getPayPalAccount()
        );
    }

    @Override
    public HackathonResponseDTO toDto(Hackathon hackathon) {
        return new HackathonResponseDTO(
                hackathon.getTitle(),
                hackathon.getDescription(),
                hackathon.getPrize(),
                hackathon.getRules(),
                toDto(hackathon.getStaff()),
                hackathon.getSchedule(),
                hackathon.getTeams() != null ? hackathon.getTeams().stream().map(this::toDto).toList() : Collections.emptyList(),
                hackathon.getSubmissions() != null ? hackathon.getSubmissions().stream().map(this::toDto).toList() : Collections.emptyList(),
                hackathon.getStatus(),
                hackathon.getWinner()!=null ?toDto(hackathon.getWinner()):null
        );
    }

    @Override
    public HackathonTeamResponseDTO toDto(HackathonTeam hackathonTeam) {
        return new HackathonTeamResponseDTO(
                hackathonTeam.getName(),
                hackathonTeam.getDescription(),
                toDto(hackathonTeam.getLeader()),
                hackathonTeam.getMembers() != null ? hackathonTeam.getMembers().stream().map(this::toDto).toList() : Collections.emptyList(),
                toDto(hackathonTeam.getMainTeam()),
                hackathonTeam.getHackathonParticipation() != null ? hackathonTeam.getHackathonParticipation().getId() : null,
                hackathonTeam.getSubmission()!=null ? toDto(hackathonTeam.getSubmission()) :null
        );
    }

    @Override
    public UserResponseDTO toDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getGlobalRole(),
                user.getUserTeam() != null ? user.getUserTeam().getId() : null
        );
    }

    @Override
    public HackathonStaffResponseDTO toDto(HackathonStaff staff) {
        return new HackathonStaffResponseDTO(
                toDto(staff.getOrganizer()),
                staff.getJudge()!=null ?toDto(staff.getJudge()):null,
                staff.getMentors() != null ?
                        staff.getMentors().stream().map(this::toDto).toList() : null
        );
    }
}
