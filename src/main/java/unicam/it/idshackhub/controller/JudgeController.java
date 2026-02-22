package unicam.it.idshackhub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicam.it.idshackhub.controller.dtoResponse.HackathonResponseDTO;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.IMapper;
import unicam.it.idshackhub.controller.dtoResponse.Mapper.MapperDTO;
import unicam.it.idshackhub.controller.dtoResponse.SubmissionResponseDTO;
import unicam.it.idshackhub.dto.GetHackathonDTO;
import unicam.it.idshackhub.dto.VoteDTO;
import unicam.it.idshackhub.service.JudgeService;

@RestController
@RequestMapping("/api/judge")
public class JudgeController {

    private final JudgeService judgeService;
    private final IMapper mapper;

    @Autowired
    public JudgeController(JudgeService judgeService, MapperDTO mapper) {
        this.mapper = mapper;
        this.judgeService = judgeService;
    }

    @PostMapping("/vote")
    public ResponseEntity<SubmissionResponseDTO> voteSubmission(@RequestHeader Long judgeId, @RequestBody VoteDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
                judgeService.judgeSubmission(judgeId, dto.submissionId(), dto.hackathonId(), dto.vote())));

    }

    @PostMapping("/close-evaluation")
    public ResponseEntity<HackathonResponseDTO> closeEvaluation(@RequestHeader Long judgeId, @RequestBody GetHackathonDTO dto) {
        return ResponseEntity.ok(mapper.toDto(
            judgeService.closeEvaluationState(judgeId, dto.hackathonId())));
    }
}