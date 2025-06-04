package me.abdul.axi_api.services;

import me.abdul.axi_api.dtos.QuestionDto;
import me.abdul.axi_api.dtos.Report.*;
import me.abdul.axi_api.dtos.TeamResponseDto;
import me.abdul.axi_api.dtos.UserInformationDto;
import me.abdul.axi_api.entities.*;
import me.abdul.axi_api.repos.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    private final AnswerRepository answerRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final FormCategoryRepository formCategoryRepository;
    private final ReportPdfService reportPdfService;
    private final FeedbackRepository feedbackRepository;

    public ReportService(
            AnswerRepository answerRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            FormCategoryRepository formCategoryRepository,
            ReportPdfService reportPdfService,
            FeedbackRepository feedbackRepository
    ) {
        this.answerRepository = answerRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.formCategoryRepository = formCategoryRepository;
        this.reportPdfService = reportPdfService;
        this.feedbackRepository = feedbackRepository;
    }

    public List<ReportDto> getAllReports(int teamId, Date startDate, Date endDate, int categoryId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        List<ReportDto> reports = new ArrayList<>();
        for (User user : team.getUsers()) {
            ReportDto report = getUserReport(user.getId(), teamId, startDate, endDate, categoryId);
            if (!report.getForms().isEmpty()){
                reports.add(report);
            }
        }
        return reports;
    }

    public byte[] getAllReportsPdf(int teamId, Date startDate, Date endDate, int categoryId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        List<ReportDto> reports = new ArrayList<>();
        for (User user : team.getUsers()) {
            ReportDto report = getUserReport(user.getId(), teamId, startDate, endDate, categoryId);
            if (!report.getForms().isEmpty()){
                reports.add(report);
            }
        }
        return reportPdfService.generatePdf(reports);
    }

    public ReportDto getUserReport(int userId, int teamId, Date startDate, Date endDate, int categoryId) {
        List<Feedback> feedbacks = feedbackRepository.findAllByDateRangeAndTeamIdAndReceiverIdAndCategoryId(
                startDate.toInstant(),
                endDate.toInstant(),
                teamId,
                userId,
                categoryId
        );

        ReportDto reportDto = new ReportDto();
        reportDto.setForms(generateFormReportDtos(feedbacks));
        reportDto.setStartDate(startDate);
        reportDto.setEndDate(endDate);
        reportDto.setTeam(getTeamInfo(teamId));
        reportDto.setRecipient(getUserInfo(userId));
        reportDto.setCategory(getCategoryInfo(categoryId));

        return reportDto;

    }

    private FormCategoryDto getCategoryInfo(int categoryId) {
        FormCategory formCategory = formCategoryRepository.findById(categoryId).orElseThrow();
        FormCategoryDto formCategoryDto = new FormCategoryDto();
        formCategoryDto.setName(formCategory.getName());
        return formCategoryDto;
    }

    private List<FormReportDto> generateFormReportDtos(List<Feedback> feedbacks) {
        // Create a map to store the form id and the corresponding form report dto
        Map<Integer, FormReportDto> formReportDtoMap = new HashMap<>();
        for (Feedback feedback : feedbacks) {
            FormReportDto formReportDto = formReportDtoMap.get(feedback.getForm().getId());
            if (formReportDto == null) {
                formReportDto = new FormReportDto();
                formReportDto.setName(feedback.getForm().getName());
                formReportDto.setQuestions(generateQuestionDtos(feedback.getForm().getQuestions()));
                formReportDtoMap.put(feedback.getForm().getId(), formReportDto);
            }
            List<FeedbackReportDto> feedbacksList = formReportDto.getFeedbacks();
            if (feedbacksList == null) {
                feedbacksList = new ArrayList<>();
                formReportDto.setFeedbacks(feedbacksList);
            }
            feedbacksList.add(generateFeedbackReportDto(feedback));
        }
        return new ArrayList<>(formReportDtoMap.values());
    }

    private List<FormQuestionDto> generateQuestionDtos(List<Question> questions) {
        List<FormQuestionDto> questionDtos = new ArrayList<>();
        for (Question question : questions) {
            FormQuestionDto questionDto = new FormQuestionDto();
            questionDto.setId(question.getId());
            questionDto.setQuestion(question.getQuestion());
            questionDtos.add(questionDto);
        }
        return questionDtos;
    }

    private FeedbackReportDto generateFeedbackReportDto(Feedback feedback) {
        FeedbackReportDto feedbackReportDto = new FeedbackReportDto();
        feedbackReportDto.setDate(feedback.getDate());
        feedbackReportDto.setScore(feedback.getScore());
        feedbackReportDto.setSender(getUserInfo(feedback.getSender().getId()));
        feedbackReportDto.setAnswers(generateAnswerDtos(feedback.getAnswers()));
        return feedbackReportDto;
    }

    private List<AnswerDto> generateAnswerDtos(Set<Answer> answers) {
        List<AnswerDto> answerDtos = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerDto answerDto = new AnswerDto();
            answerDto.setQuestionId(answer.getQuestion().getId());
            answerDto.setContent(answer.getContent());
            answerDtos.add(answerDto);
        }
        return answerDtos;
    }

    private TeamResponseDto getTeamInfo(int teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow();
        TeamResponseDto teamResponseDto = new TeamResponseDto();
        teamResponseDto.setId(team.getId());
        teamResponseDto.setName(team.getName());
        return teamResponseDto;
    }

    private UserInformationDto getUserInfo(int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserInformationDto userInformationDto = new UserInformationDto();
        userInformationDto.setId(user.getId());
        userInformationDto.setFirstName(user.getFirstName());
        userInformationDto.setInfix(user.getInfix());
        userInformationDto.setLastName(user.getLastName());
        return userInformationDto;
    }

public byte[] getUserReportPdf(int userId, int teamId, Date startDate, Date endDate, int categoryId) {
        ReportDto report = getUserReport(userId, teamId, startDate, endDate, categoryId);
        return reportPdfService.generatePdf(Collections.singletonList(report));
    }



}
