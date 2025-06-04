package me.abdul.axi_api.controllers;

import me.abdul.axi_api.dtos.FeedbackCreateDto;
import me.abdul.axi_api.entities.Feedback;
import me.abdul.axi_api.entities.User;
import me.abdul.axi_api.services.FeedbackService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/team/{teamId}/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("")
    public ResponseEntity<?> postFeedback(@PathVariable int teamId, @RequestBody FeedbackCreateDto feedbackCreateDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            feedbackService.createFeedback(user, teamId, feedbackCreateDto);
            return ResponseEntity.ok("Feedback created");
        } catch (BadRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // can be used to get feedback sent to a specific user
    // or to get reflections
    @GetMapping("/sent/{receiverId}")
    public ResponseEntity<?> getFeedback(@PathVariable int teamId, @PathVariable Integer receiverId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            return ResponseEntity.ok(
                feedbackService.getSentFeedback(user, teamId, receiverId)
            );
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/received")
    public ResponseEntity<?> getFeedback(@PathVariable int teamId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            return ResponseEntity.ok(
                feedbackService.getReceivedFeedback(user, teamId)
            );
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFeedback(@PathVariable int teamId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            return ResponseEntity.ok(
                feedbackService.getAllFeedback(user, teamId)
            );
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all/{receiverId}")
    public ResponseEntity<?> getAllFeedback(@PathVariable int teamId, @PathVariable Integer receiverId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            return ResponseEntity.ok(
                feedbackService.getAllFeedback(user, teamId, receiverId)
            );
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
