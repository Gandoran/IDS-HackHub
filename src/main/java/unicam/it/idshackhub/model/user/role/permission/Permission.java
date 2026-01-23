package unicam.it.idshackhub.model.user.role.permission;

/**
 * Enumerates all granular access rights available within the system.
 * <p>
 * This enum acts as the catalog of all possible actions a user might perform.
 * Permissions are not assigned directly to users; instead, they are grouped into
 * {@link unicam.it.idshackhub.model.user.role.Role}s, which are then assigned to users.
 * </p>
 */
public enum Permission {
    /** Permission to spawn a temporary team for a hackathon. */
    Can_Create_HackathonTeam,
    /** Permission to register a permanent main team in the system. */
    Can_Create_Team,
    /** Permission to submit a formal request (e.g., for verification). */
    Can_Create_Request,
    /** Permission to organize a new Hackathon event. */
    Can_Create_Hackathon,
    /** Permission to cast a vote on projects during a Hackathon. */
    Can_Vote,
    /** Permission to close the evaluation phase of a Hackathon. */
    Can_End_Valuation_State,
    /** Permission to submit a final project for a Hackathon. */
    Can_Submit,
    /** Permission to register a sub-team to a specific Hackathon event. */
    Can_Register_Team,
    /** Permission to approve or reject user requests (Admin only). */
    Can_Manage_Request,
    /** Permission to declare the winners of a Hackathon. */
    Can_Proclamate_Winner,
    /** Permission to invite a user to become a Judge. */
    Can_Invite_Judge;
}