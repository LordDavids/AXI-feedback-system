package me.abdul.axi_api.services;

import me.abdul.axi_api.dtos.AnswerCreateDto;
import me.abdul.axi_api.dtos.FeedbackCreateDto;
import me.abdul.axi_api.dtos.FeedbackDto;
import me.abdul.axi_api.dtos.UserInformationDto;
import me.abdul.axi_api.entities.*;
import me.abdul.axi_api.repos.AnswerRepository;
import me.abdul.axi_api.repos.FeedbackRepository;
import me.abdul.axi_api.repos.QuestionRepository;
import me.abdul.axi_api.repos.TeamRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class FeedbackService {
    private final TeamRepository teamRepository;
    private final FeedbackRepository feedbackRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final EmailService emailService;

    public FeedbackService(TeamRepository teamRepository, FeedbackRepository feedbackRepository, QuestionRepository questionRepository, AnswerRepository answerRepository, EmailService emailService) {
        this.teamRepository = teamRepository;
        this.feedbackRepository = feedbackRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.emailService = emailService;
    }

    public void createFeedback(User user, int teamId, FeedbackCreateDto feedbackCreateDto) throws BadRequestException {
        Team team = teamRepository.findTeamById(teamId);

        boolean userIsInTeam = team != null && team.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));

        if (!userIsInTeam) {
            throw new BadRequestException("User is not in team");
        }

        Form form = team.getForms().stream().filter(f -> f.getId().equals(feedbackCreateDto.getFormId())).findFirst().orElse(null);
        if(form == null) {
            throw new BadRequestException("Form not found");
        }

        User receiver = team.getUsers().stream().filter(u -> u.getId().equals(feedbackCreateDto.getReceiverId())).findFirst().orElse(null);

        if(receiver == null) {
            throw new BadRequestException("Receiver not found");
        }

        Feedback feedback = new Feedback();
        feedback.setSender(user);
        feedback.setReceiver(receiver);
        feedback.setForm(form);
        feedback.setTeam(team);
        feedback.setScore(feedbackCreateDto.getScore());
        feedback.setDate(Instant.now());

        feedback = feedbackRepository.save(feedback);

        Set<Answer> answers = new HashSet<>();
        for (AnswerCreateDto answerDto : feedbackCreateDto.getAnswers()) {
            Answer answer = new Answer();
            Question question = form.getQuestions().stream().filter(q -> q.getId().equals(answerDto.getQuestionId())).findFirst().orElse(null);

            if(question == null)
                continue;

            answer.setQuestion(question);
            answer.setFeedback(feedback);
            answer.setContent(answerDto.getContent());
            answers.add(answer);
        }
        answerRepository.saveAll(answers);

        feedbackRepository.flush();
        answerRepository.flush();

        if(!Objects.equals(user.getId(), receiver.getId())) {
            // Send email to receiver (except if user is receiver - reflections)
            emailService.sendEmail(user, receiver);
        }
    }

    public List<FeedbackDto> getSentFeedback(User sender, int teamId, int receiverId) throws BadRequestException {
        Team team = teamRepository.findTeamById(teamId);

        boolean userIsInTeam = team != null && team.getUsers().stream().anyMatch(u -> u.getId().equals(sender.getId()));

        if (!userIsInTeam) {
            throw new BadRequestException("User is not in team");
        }

        List<Feedback> feedbackList = feedbackRepository.findAllByTeamIdAndReceiverIdAndSenderId(teamId, receiverId, sender.getId());

        List<FeedbackDto> feedbackDtos = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            feedbackDtos.add(new FeedbackDto(
                    feedback.getId(),
                    feedback.getDate(),
                    feedback.getAnswers(),
                    new UserInformationDto(feedback.getSender()),
                    new UserInformationDto(feedback.getReceiver()),
                    feedback.getScore(),
                    feedback.getTeam().getId(),
                    feedback.getForm().getName()
            ));
        }

        return feedbackDtos;
    }

    public List<FeedbackDto> getReceivedFeedback(User user, int teamId) throws BadRequestException {
        Team team = teamRepository.findTeamById(teamId);

        boolean userIsInTeam = team != null && team.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));

        if (!userIsInTeam) {
            throw new BadRequestException("User is not in team");
        }

        List<Feedback> feedbackList = feedbackRepository.findAllByTeamIdAndReceiverIdAndSenderIdIsNot(teamId, user.getId(), user.getId());

        List<FeedbackDto> feedbackDtos = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            feedbackDtos.add(new FeedbackDto(
                    feedback.getId(),
                    feedback.getDate(),
                    feedback.getAnswers(),
                    new UserInformationDto(feedback.getSender()),
                    new UserInformationDto(feedback.getReceiver()),
                    feedback.getScore(),
                    feedback.getTeam().getId(),
                    feedback.getForm().getName()
            ));
        }

        return feedbackDtos;
    }

    public List<FeedbackDto> getAllFeedback(User user, int teamId) throws BadRequestException {
        Team team = teamRepository.findTeamById(teamId);

        boolean userIsInTeam = team != null && team.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));

        if (!userIsInTeam) {
            throw new BadRequestException("User is not in team");
        }

        List<Feedback> feedbackList = feedbackRepository.findAllByTeamId(teamId);

        List<FeedbackDto> feedbackDtos = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            feedbackDtos.add(new FeedbackDto(
                    feedback.getId(),
                    feedback.getDate(),
                    feedback.getAnswers(),
                    new UserInformationDto(feedback.getSender()),
                    new UserInformationDto(feedback.getReceiver()),
                    feedback.getScore(),
                    feedback.getTeam().getId(),
                    feedback.getForm().getName()
            ));
        }

        return feedbackDtos;
    }

    public List<FeedbackDto> getAllFeedback(User user, int teamId, Integer receiverId) throws BadRequestException {
        Team team = teamRepository.findTeamById(teamId);

        boolean userIsInTeam = team != null && team.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()));

        if (!userIsInTeam) {
            throw new BadRequestException("User is not in team");
        }

        List<Feedback> feedbackList = feedbackRepository.findAllByTeamIdAndReceiverId(teamId, receiverId);

        List<FeedbackDto> feedbackDtos = new ArrayList<>();
        for (Feedback feedback : feedbackList) {
            feedbackDtos.add(new FeedbackDto(
                    feedback.getId(),
                    feedback.getDate(),
                    feedback.getAnswers(),
                    new UserInformationDto(feedback.getSender()),
                    new UserInformationDto(feedback.getReceiver()),
                    feedback.getScore(),
                    feedback.getTeam().getId(),
                    feedback.getForm().getName()
            ));
        }

        return feedbackDtos;
    }
}
