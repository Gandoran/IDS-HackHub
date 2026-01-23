package unicam.it.idshackhub.model.service;

import unicam.it.idshackhub.model.hackathon.Hackathon;
import unicam.it.idshackhub.model.hackathon.state.HackathonState;
import unicam.it.idshackhub.model.hackathon.state.HackathonStatus;
import unicam.it.idshackhub.model.user.User;
import unicam.it.idshackhub.model.user.role.permission.Permission;
import unicam.it.idshackhub.model.utils.Submission;

import static unicam.it.idshackhub.model.service.PermissionChecker.checkPermission;

public class JudgeService {

    public Integer judgeSubmission(User judge, Submission submission, Hackathon hackathon, int vote) {
        if(checkPermission(judge, Permission.Can_Vote,hackathon)){
            if(hackathon.isActionAllowed(Permission.Can_Vote)){
                submission.setVote(vote);
                return submission.getVote();
            }else {
                throw  new RuntimeException("Hackathon not in the valutate state");
            }
        }else{
            throw new RuntimeException("Permission denied");
        }
    }

    public HackathonStatus closeEvaluationState(User judge, Hackathon hackathon){
        if(!checkPermission(judge,Permission.Can_End_Valuation_State,hackathon)){
            throw new RuntimeException("Permission denied");
        }
        for(Submission submission: hackathon.getSubmissions()){
            if(submission.getVote() == null){
                throw new RuntimeException("Submission withuout a vote");
            }
        }
        hackathon.setStatus(HackathonStatus.CONCLUSION);
        return hackathon.getStatus();
    }
}
