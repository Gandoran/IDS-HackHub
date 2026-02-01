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
public class JoinTeamStrategy implements MessageStrategy {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public MessageType getSupportedType() {
        return MessageType.JOIN_TEAM_REQUEST;
    }

    @Override
    @Transactional
    public void executeAccept(Message request) {
        Long teamId = request.getReferenceId();
        User candidate = request.getSender();

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        if (candidate.getUserTeam() != null) {
            throw new IllegalStateException("User " + candidate.getUsername() + " is already in a team.");
        }

        team.getMembers().add(candidate);
        candidate.setUserTeam(team);

        candidate.addAssignment(new Assignment(team, ContextRole.T_TeamMember));

        userRepository.save(candidate);
        teamRepository.save(team);

        executeResponse(request, "Your request to join " + team.getName() + " has been accepted!", ActionStatus.ACCEPTED);
    }

    @Override
    public void executeReject(Message request) {
        executeResponse(request, "Your request to join was rejected by the team leader.", ActionStatus.REJECTED);
    }

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