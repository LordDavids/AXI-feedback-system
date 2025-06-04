package me.abdul.axi_api.controllers;


import me.abdul.axi_api.dtos.QuestionDto;
import me.abdul.axi_api.entities.Question;
import me.abdul.axi_api.services.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Question>> getAllPublicQuestions() {
        List<Question> questions = questionService.getAllPublicQuestions();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/create")
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionDto question) {
        Question createdQuestion = questionService.createQuestion(question);
        return ResponseEntity.ok(createdQuestion);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Question> updateQuestion(@PathVariable Integer id, @RequestBody QuestionDto question) {
        Question updatedQuestion = questionService.updateQuestion(id, question);
        return ResponseEntity.ok(updatedQuestion);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(@RequestParam String query) {
        List<Question> questions = questionService.searchQuestions(query);
        return ResponseEntity.ok(questions);
    }
}
