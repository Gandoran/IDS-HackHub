package unicam.it.idshackhub.service.strategy;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unicam.it.idshackhub.model.message.ActionStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.repository.MessageRepository;
import unicam.it.idshackhub.repository.TeamRepository;
import unicam.it.idshackhub.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class TeamInvitationStrategy implements MessageStrategy{

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.INVITE_USER_REQUEST;
    }

    @Override
    @Transactional
    public void executeAccept(Message invite) {
        Long teamId = invite.getReferenceId();
        User newTeamMember = invite.getRecipient();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (newTeamMember.getRoleByContext(team).isPresent()) {
            executeResponse(invite, "Error: You are already part of this team.", ActionStatus.REJECTED);
            return;
        }
        if(newTeamMember.getUserTeam()!=null) {
            executeResponse(invite, "Error: You are already part of a team.", ActionStatus.REJECTED);
            return;
        }

        team.getMembers().add(newTeamMember);
        newTeamMember.setUserTeam(team);
        newTeamMember.addAssignment(new Assignment(team, ContextRole.T_TeamMember));


        userRepository.save(newTeamMember);
        teamRepository.save(team);
        executeResponse(invite, "You are now a member of " + team.getName(), ActionStatus.ACCEPTED);
    }

    @Override
    public void executeReject(Message message) {
        executeResponse(message, "You declined the invitation.", ActionStatus.REJECTED);    }

    @Override
    public void executeResponse(Message original, String content, ActionStatus status) {
        Message response = new Message(
                original.getRecipient(),
                original.getSender(),
                content,
                original.getType().getOpposite(),
                status,
                original.getReferenceId()
        );
        messageRepository.save(response);
    }
}
