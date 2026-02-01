package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.dto.VoteDTO;
import unicam.it.idshackhub.service.JudgeService;

@RestController
@RequestMapping("/api/judge")
public class JudgeController {

    private final JudgeService judgeService;

    @Autowired
    public JudgeController(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    @PostMapping("/vote")
    public ResponseEntity<?> voteSubmission(@RequestBody VoteDTO dto) {
        judgeService.judgeSubmission(dto.judgeId(), dto.submissionId(), dto.hackathonId(), dto.vote());
        return ResponseEntity.ok("Vote assigned: " + dto.vote());
    }

    @PostMapping("/close-evaluation")
    public ResponseEntity<?> closeEvaluation(@RequestParam Long judgeId, @RequestParam Long hackathonId) {
        judgeService.closeEvaluationState(judgeId, hackathonId);
        return ResponseEntity.ok("Evaluation closed.");
    }
}