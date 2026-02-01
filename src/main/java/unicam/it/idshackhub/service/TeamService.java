package unicam.it.idshackhub.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.message.Message;
import unicam.it.idshackhub.model.message.MessageType;
import unicam.it.idshackhub.model.team.HackathonTeam;
import unicam.it.idshackhub.model.team.Team;
import unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.assignment.Assignment;
import unicam.it.idshackhub.model.user.role.ContextRole;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.repository.HackathonRepository;
import unicam.it.idshackhub.repository.HackathonTeamRepository;
import unicam.it.idshackhub.repository.UserRepository;

import java.util.List;

import static unicam.it.idshackhub.service.EntityUtils.getEntity;
import static unicam.it.idshackhub.service.PermissionChecker.checkPermission;

/**
 * Manages team participation within a Hackathon.
 * <p>
 * This service mainly implements the use case of registering a main {@link unicam.it.idshackhub.model.team.Team}
 * to an {@link unicam.it.idshackhub.model.hackathon.Hackathon} by creating a {@link unicam.it.idshackhub.model.team.HackathonTeam}
 * and assigning the appropriate roles to participants.
 * </p>
 */
@Service
public class TeamService {

    private final HackathonTeamRepository hackathonTeamRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final HackathonRepository hackathonRepository;

    @Autowired
    public TeamService(HackathonTeamRepository hackathonTeamRepository,
                       UserRepository userRepository,
                       MessageService messageService,
                       HackathonRepository hackathonRepository) {
        this.hackathonTeamRepository = hackathonTeamRepository;
        this.userRepository = userRepository;
        this.messageService = messageService;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Registers a team to participate in a specific Hackathon.
     * <p>
     * The registration is allowed only if:
     * <ul>
     *   <li>the acting user has {@link unicam.it.idshackhub.model.user.role.permission.Permission#Can_Register_Team} on the main team;</li>
     *   <li>the Hackathon phase allows team registration;</li>
     *   <li>the main team is not already registered to the same Hackathon;</li>
     *   <li>all requested members are available and comply with Hackathon rules.</li>
     * </ul>
     * The method creates a {@link unicam.it.idshackhub.model.team.HackathonTeam} via {@link unicam.it.idshackhub.model.team.builder.HackathonTeamBuilder},
     * links it to the Hackathon and main team, persists it, and assigns roles/assignments to participants.
     * </p>
     */
    @Transactional
    public HackathonTeam registerHackathonTeam(Long teamLeaderId, String name, String description, Long hackathonTeamLeaderId, List<Long> membersId, Long hackathonID) {
        User teamLeader = getEntity(userRepository, teamLeaderId, "Leader");
        User hackathonTeamLeader = getEntity(userRepository, hackathonTeamLeaderId, "HackTeam Leader");
        Hackathon hackathon = getEntity(hackathonRepository, hackathonID, "Hackathon");

        List<User> members = userRepository.findAllById(membersId);
        if (members.size() != membersId.size()) {
            throw new RuntimeException("Some member IDs provided were not found.");
        }
        Team mainTeam = this.getMainTeam(teamLeader);
        validatePermissions(teamLeader, mainTeam);
        validateHackathonPermissions(hackathon);
        validateMainTeamUniqueness(mainTeam, hackathon);
        validateMembersAvailability(members, hackathon);
        validateHackathonRules(members.size(), hackathon);

        HackathonTeam hackathonTeam = buildHackathonTeam(name, description, hackathonTeamLeader, mainTeam, members);

        hackathon.getTeams().add(hackathonTeam);
        hackathonTeam.setHackathonParticipation(hackathon);

        mainTeam.getHackathonTeams().add(hackathonTeam);

        hackathonTeam = hackathonTeamRepository.save(hackathonTeam);

        assignHackathonRoles(hackathonTeam, hackathon);

        userRepository.saveAll(members);
        userRepository.save(hackathonTeamLeader);

        return hackathonTeam;
    }

    /**
     * A Team Leader can invite a user to join his Team.
     * The user will receive an invite Message.
     */
    @Transactional
    public Message inviteUserToTeam(Long invitedUserId, Long teamLeaderId) {
        User invitedUser = getEntity(userRepository, invitedUserId, "Invited User");
        User teamLeader = getEntity(userRepository, teamLeaderId, "Team Leader");

        Team mainTeam = teamLeader.getUserTeam();

        if (!checkPermission(teamLeader, Permission.Can_Invite_Users, teamLeader.getUserTeam())) {
            throw new RuntimeException("Permission denied: Cannot invite user.");
        }
        if(invitedUser.getUserTeam()!=null) {
            throw new RuntimeException("User already in a team");
        }
        return messageService.sendMessage(
                teamLeader,
                invitedUser,
                MessageType.INVITE_USER_REQUEST,
                "You have been invited to join "+ mainTeam.getName(),
                mainTeam.getId()
        );

    }

    @Transactional
    public Message manageJoinRequest(Long leaderId, Long messageId, boolean accept) {
        User leader = getEntity(userRepository, leaderId, "Leader");
        if (!checkPermission(leader, Permission.Can_Invite_Users, leader.getUserTeam())) {
            throw new RuntimeException("Permission denied: Cannot manage join requests.");
        }
        return messageService.processReply(messageId, accept, leader,null);
    }


    private void validatePermissions(User user, Team mainTeam) {
        if (!checkPermission(user, Permission.Can_Register_Team, mainTeam)) {
            throw new RuntimeException("Permission denied");
        }
    }

    private void validateHackathonPermissions(Hackathon hackathon) {
        if (!hackathon.isActionAllowed(Permission.Can_Register_Team)) {
            throw new RuntimeException("Permission denied: cannot register team.");
        }
        if (hackathon.getStatus() != HackathonStatus.REGISTRATION) {
            throw new RuntimeException("Hackathon not in the registration phase");
        }
    }

    private void validateMembersAvailability(List<User> members, Hackathon hackathon) {
        for (User member : members) {
            if (member.getRoleByContext(hackathon).isPresent()) {
                throw new RuntimeException("User already in the hackathon");
            }
        }
    }

    private void validateMainTeamUniqueness(Team mainTeam, Hackathon hackathon) {
        boolean alreadyParticipating = hackathon.getTeams().stream()
                .anyMatch(ht -> ht.getMainTeam().equals(mainTeam));
        if (alreadyParticipating) {
            throw new RuntimeException("Main Team already has a Hackathon Team");
        }
    }

    private void validateHackathonRules(int teamSize, Hackathon hackathon) {
        if (hackathon.getTeams().size() >= hackathon.getRules().getMaxTeams()) {
            throw new RuntimeException("Maximum team amount reached");
        }
        if (teamSize > hackathon.getRules().getMaxPlayersPerTeam()) {
            throw new RuntimeException("Team size is too big");
        }
        if (teamSize < hackathon.getRules().getMinPlayersPerTeam()) {
            throw new RuntimeException("Team size is too small");
        }
    }

    private HackathonTeam buildHackathonTeam(String name, String description, User leader, Team mainTeam, List<User> members) {
        return new HackathonTeamBuilder().reset()
                .buildName(name)
                .buildDescription(description)
                .buildLeader(leader)
                .buildMainTeam(mainTeam)
                .buildMembers(members)
                .getResult();
    }

    private void assignHackathonRoles(HackathonTeam hackTeam, Hackathon hackathon) {
        hackTeam.getLeader().addAssignment(new Assignment(hackathon,ContextRole.H_HackathonTeamLeader));
        for (User member : hackTeam.getMembers()) {
            member.addAssignment(new Assignment(hackathon,ContextRole.H_HackathonTeamMember));
        }
    }

    private Team getMainTeam(User teamLeader) {
        return teamLeader.getContextByRole(ContextRole.T_TeamLeader)
                .map(context -> (Team) context)
                .orElseThrow(() -> new RuntimeException("You have to be a Team Leader of a Main Team to create a Hackathon Team"));
    }
}